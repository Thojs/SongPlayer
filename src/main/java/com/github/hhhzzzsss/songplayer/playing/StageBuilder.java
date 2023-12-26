package com.github.hhhzzzsss.songplayer.playing;

import com.github.hhhzzzsss.songplayer.Config;
import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.mixin.ClientPlayerInteractionManagerAccessor;
import com.github.hhhzzzsss.songplayer.song.Instrument;
import com.github.hhhzzzsss.songplayer.song.Song;
import com.github.hhhzzzsss.songplayer.stage.StageType;
import com.github.hhhzzzsss.songplayer.stage.StageTypeRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.util.*;
import java.util.stream.Collectors;

public class StageBuilder {
	public boolean isBuilding = false;
	
	public BlockPos position = null;
	public HashMap<Integer, BlockPos> noteblockPositions = new HashMap<>();

	public LinkedList<BlockPos> requiredBreaks = new LinkedList<>();
	public TreeSet<Integer> missingNotes = new TreeSet<>();
	public int totalMissingNotes = 0;

	private final SongHandler handler;

	public StageBuilder(SongHandler handler) {
		this.handler = handler;
	}

	int buildStartDelay = 0;
	int buildEndDelay = 0;

	public void handleBuilding(boolean tick) {
		if (!tick) return;

		setBuildProgressDisplay();

		if (buildStartDelay > 0) {
			buildStartDelay--;
			return;
		}

		ClientWorld world = SongPlayer.MC.world;
		if (SongPlayer.MC.interactionManager.getCurrentGameMode() != GameMode.CREATIVE) return;

		if (nothingToBuild()) {
			if (buildEndDelay > 0) {
				buildEndDelay--;
				return;
			} else {
				checkBuildStatus(handler.loadedSong);
				sendMovementPacketToStagePosition();
			}
		}

		if (!requiredBreaks.isEmpty()) {
			for (int i=0; i<5; i++) {
				if (requiredBreaks.isEmpty()) break;
				BlockPos bp = requiredBreaks.poll();
				handler.attackBlock(bp);
			}
			buildEndDelay = 20;
		} else if (!missingNotes.isEmpty()) {
			int desiredNoteId = missingNotes.pollFirst();
			BlockPos bp = noteblockPositions.get(desiredNoteId);
			if (bp == null) return;
			int blockId = Block.getRawIdFromState(world.getBlockState(bp));
			int currentNoteId = (blockId-SongPlayer.NOTEBLOCK_BASE_ID)/2;
			if (currentNoteId != desiredNoteId) {
				holdNoteblock(desiredNoteId, buildSlot);
				if (blockId != 0) {
					handler.attackBlock(bp);
				}
				handler.placeBlock(bp);
			}
			buildEndDelay = 20;
		} else { // Switch to playing
			restoreBuildSlot();
			isBuilding = false;
			handler.setSurvivalIfNeeded();
			sendMovementPacketToStagePosition();
			SongPlayer.addChatMessage("§6Now playing §3" + handler.loadedSong.name);
		}
	}

	private void setBuildProgressDisplay() {
		MutableText buildText = Text.empty()
				.append(Text.literal("Building noteblocks | " ).formatted(Formatting.GOLD))
				.append(Text.literal((totalMissingNotes - missingNotes.size()) + "/" + totalMissingNotes).formatted(Formatting.DARK_AQUA));
		MutableText playlistText = Text.empty();
		ProgressDisplay.instance.setText(buildText, playlistText);
	}
	
	public void movePlayerToStagePosition() {
		if (position == null) return;
		ClientPlayerEntity player = SongPlayer.MC.player;
		player.getAbilities().allowFlying = true;
		player.getAbilities().flying = true;
		player.refreshPositionAndAngles(position.getX() + 0.5, position.getY() + 0.0, position.getZ() + 0.5, player.getYaw(), player.getPitch());
		player.setVelocity(Vec3d.ZERO);
		sendMovementPacketToStagePosition();
	}

	public void sendMovementPacketToStagePosition() {
		AbstractClientPlayerEntity entity;
		if (SongPlayer.fakePlayer != null) {
			entity = SongPlayer.fakePlayer;
		} else {
			entity = SongPlayer.MC.player;
		}

		if (entity == null) return;

		SongPlayer.MC.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(
				position.getX() + 0.5, position.getY(), position.getZ() + 0.5,
				entity.getYaw(), entity.getPitch(),
				true));
	}

	public void checkBuildStatus(Song song) {
		noteblockPositions.clear();
		missingNotes.clear();

		// Add all required notes to missingNotes
		for (int i=0; i<song.requiredNotes.length; i++) {
			if (song.requiredNotes[i]) {
				missingNotes.add(i);
			}
		}

		ArrayList<BlockPos> noteblockLocations = new ArrayList<>();
		HashSet<BlockPos> breakLocations = new HashSet<>();
		StageType type = StageTypeRegistry.instance.getType(Config.getConfig().stageType);

		if (type != null) type.getBlocks(noteblockLocations, breakLocations);
		noteblockLocations = noteblockLocations.stream()
				.map((bp) -> bp.add(position.getX(), position.getY(), position.getZ()))
				.sorted(this::sortBlocks)
				.collect(Collectors.toCollection(ArrayList::new));

		// Remove already-existing notes from missingNotes, adding their positions to noteblockPositions, and create a list of unused noteblock locations
		ArrayList<BlockPos> unusedNoteblockLocations = new ArrayList<>();
		for (BlockPos nbPos : noteblockLocations) {
			BlockState bs = SongPlayer.MC.world.getBlockState(nbPos);
			int blockId = Block.getRawIdFromState(bs);
			if (blockId >= SongPlayer.NOTEBLOCK_BASE_ID && blockId < SongPlayer.NOTEBLOCK_BASE_ID+800) {
				int noteId = (blockId-SongPlayer.NOTEBLOCK_BASE_ID)/2;
				if (missingNotes.contains(noteId)) {
					missingNotes.remove(noteId);
					noteblockPositions.put(noteId, nbPos);
				} else {
					unusedNoteblockLocations.add(nbPos);
				}
			} else {
				unusedNoteblockLocations.add(nbPos);
			}
		}

		// Cull noteblocks that won't fit in stage
		if (missingNotes.size() > unusedNoteblockLocations.size()) {
			while (missingNotes.size() > unusedNoteblockLocations.size()) {
				missingNotes.pollLast();
			}
		}

		// Populate missing noteblocks into the unused noteblock locations
		int idx = 0;
		for (int noteId : missingNotes) {
			BlockPos bp = unusedNoteblockLocations.get(idx++);
			noteblockPositions.put(noteId, bp);
		}

		for (BlockPos bp : noteblockPositions.values()) { // Optional break locations
			breakLocations.add(bp.up());
		}

		requiredBreaks = breakLocations.stream()
				.map((bp) -> bp.add(position.getX(), position.getY(), position.getZ()))
				.filter((bp) -> {
					BlockState bs = SongPlayer.MC.world.getBlockState(bp);
					return !bs.isAir() && !bs.isLiquid();
				})
				.sorted(this::sortBlocks)
				.collect(Collectors.toCollection(LinkedList::new));

		if (requiredBreaks.stream().noneMatch(bp -> withinBreakingDist(bp.getX()-position.getX(), bp.getY()-position.getY(), bp.getZ()-position.getZ()))) {
			requiredBreaks.clear();
		}

		// Set total missing notes
		totalMissingNotes = missingNotes.size();
	}

	// This doesn't check for whether the block above the noteblock position is also reachable
	// Usually there is sky above you though so hopefully this doesn't cause a problem most of the time
	boolean withinBreakingDist(int dx, int dy, int dz) {
		double dy1 = dy + 0.5 - 1.62; // Standing eye height
		double dy2 = dy + 0.5 - 1.27; // Crouching eye height
		return dx*dx + dy1*dy1 + dz*dz < 5.99999*5.99999 && dx*dx + dy2*dy2 + dz*dz < 5.99999*5.99999;
	}

	public boolean nothingToBuild() {
		return requiredBreaks.isEmpty() && missingNotes.isEmpty();
	}

	private static final int WRONG_INSTRUMENT_TOLERANCE = 3;
	public boolean hasBreakingModification() {
		int wrongInstruments = 0;
		for (Map.Entry<Integer, BlockPos> entry : noteblockPositions.entrySet()) {
			BlockState bs = SongPlayer.MC.world.getBlockState(entry.getValue());
			int blockId = Block.getRawIdFromState(bs);
			int actualNoteId = (blockId-SongPlayer.NOTEBLOCK_BASE_ID)/2;
			if (actualNoteId < 0 || actualNoteId >= 400) {
				return true;
			}
			int actualInstrument = actualNoteId / 25;
			int actualPtich = actualNoteId % 25;
			int targetInstrument = entry.getKey() / 25;
			int targetPitch = entry.getKey() % 25;
			if (targetPitch != actualPtich) {
				return true;
			}
			if (targetInstrument != actualInstrument) {
				wrongInstruments++;
				if (wrongInstruments > WRONG_INSTRUMENT_TOLERANCE) {
					return true;
				}
			}

			BlockState aboveBs = SongPlayer.MC.world.getBlockState(entry.getValue().up());
			if (!aboveBs.isAir() && !aboveBs.isLiquid()) {
				return true;
			}
		}
		return false;
	}

	private int sortBlocks(BlockPos a, BlockPos b) {
		// First sort by y
		int a_dy = a.getY() - position.getY();
		int b_dy = b.getY() - position.getY();
		if (a_dy == -1) a_dy = 0; // same layer
		if (b_dy == -1) b_dy = 0; // same layer
		if (Math.abs(a_dy) < Math.abs(b_dy)) {
			return -1;
		} else if (Math.abs(a_dy) > Math.abs(b_dy)) {
			return 1;
		}
		// Then sort by horizontal distance
		int a_dx = a.getX() - position.getX();
		int a_dz = a.getZ() - position.getZ();
		int b_dx = b.getX() - position.getX();
		int b_dz = b.getZ() - position.getZ();
		int a_dist = a_dx*a_dx + a_dz*a_dz;
		int b_dist = b_dx*b_dx + b_dz*b_dz;
		if (a_dist < b_dist) {
			return -1;
		} else if (a_dist > b_dist) {
			return 1;
		}
		// Finally sort by angle
		double a_angle = Math.atan2(a_dz, a_dx);
		double b_angle = Math.atan2(b_dz, b_dx);
        return Double.compare(a_angle, b_angle);
	}

	// Build slot
	private ItemStack prevHeldItem = null;
	int buildSlot = -1;

	void getAndSaveBuildSlot() {
		buildSlot = SongPlayer.MC.player.getInventory().getSwappableHotbarSlot();
		prevHeldItem = SongPlayer.MC.player.getInventory().getStack(buildSlot);
	}

	void restoreBuildSlot() {
		if (buildSlot != -1) {
			SongPlayer.MC.player.getInventory().setStack(buildSlot, prevHeldItem);
			SongPlayer.MC.interactionManager.clickCreativeStack(prevHeldItem, 36 + buildSlot);
			buildSlot = -1;
		}
	}

	private void holdNoteblock(int id, int slot) {
		PlayerInventory inventory = SongPlayer.MC.player.getInventory();
		inventory.selectedSlot = slot;
		((ClientPlayerInteractionManagerAccessor) SongPlayer.MC.interactionManager).invokeSyncSelectedSlot();
		String instrument = Instrument.getInstrumentFromId(id/25).instrumentName;
		int note = id%25;

		NbtCompound nbt = new NbtCompound();
		nbt.putString("id", "minecraft:note_block");
		nbt.putByte("Count", (byte) 1);

		NbtCompound tag = new NbtCompound();
		NbtCompound bsTag = new NbtCompound();
		bsTag.putString("instrument", instrument);
		bsTag.putString("note", Integer.toString(note));

		tag.put("BlockStateTag", bsTag);
		nbt.put("tag", tag);

		ItemStack noteblockStack = ItemStack.fromNbt(nbt);
		inventory.main.set(slot, noteblockStack);
		SongPlayer.MC.interactionManager.clickCreativeStack(noteblockStack, 36 + slot);
	}
}
