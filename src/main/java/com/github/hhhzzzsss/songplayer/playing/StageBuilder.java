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
import net.minecraft.block.NoteBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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

	private LinkedList<BlockPos> requiredBreaks = new LinkedList<>();
	public TreeSet<Integer> missingNotes = new TreeSet<>();
	private int totalMissingNotes = 0;

	private final SongHandler handler;
	StageBuilder(SongHandler handler) {
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
		if (handler.getGameMode() != GameMode.CREATIVE) return;

		// Check if building has finished & all needed note-blocks have been placed.
		if (nothingToBuild()) {
			if (buildEndDelay > 0) {
				buildEndDelay--;
				return;
			}

			checkBuildStatus(handler.loadedSong);
			handler.sendMovementPacketToStagePosition();
		}

		if (buildSlot == -1) {
			getAndSaveBuildSlot();
			SongPlayer.addChatMessage("§6Building noteblocks");
		}

		// Break blocks from list if there are any.
		if (!requiredBreaks.isEmpty()) {
			for (int i=0; i<5; i++) {
				if (requiredBreaks.isEmpty()) break;
				BlockPos bp = requiredBreaks.poll();
				handler.attackBlock(bp);
			}
			buildEndDelay = 20;
			return;
		}

		// Place missing notes if there are any
		if (!missingNotes.isEmpty()) {
			int desiredNoteId = missingNotes.pollFirst();
			BlockPos bp = noteblockPositions.get(desiredNoteId);
			if (bp == null) return;
			BlockState state = world.getBlockState(bp);
			int blockId = Block.getRawIdFromState(state);

			int currentNoteId = (blockId-SongPlayer.NOTEBLOCK_BASE_ID)/2;
			if (currentNoteId != desiredNoteId) {
				holdNoteblock(desiredNoteId, buildSlot);
				if (blockId != 0) {
					handler.attackBlock(bp);
				}
				handler.placeBlock(bp);
			}

			buildEndDelay = 20;
			return;
		}

		// restore everything, go to play state.
		restoreBuildSlot();
		isBuilding = false;
		handler.setSurvivalIfNeeded();
		handler.sendMovementPacketToStagePosition();
		SongPlayer.addChatMessage("§6Now playing §3" + handler.loadedSong.name);
	}

	private void setBuildProgressDisplay() {
		MutableText buildText = Text.empty()
				.append(Text.literal("Building noteblocks | " ).formatted(Formatting.GOLD))
				.append(Text.literal((totalMissingNotes - missingNotes.size()) + "/" + totalMissingNotes).formatted(Formatting.DARK_AQUA));
		ProgressDisplay.instance.setText(buildText, Text.empty());
	}
	
	public void movePlayerToStagePosition() {
		if (position == null) return;
		ClientPlayerEntity player = handler.getPlayer();
		player.getAbilities().allowFlying = true;
		player.getAbilities().flying = true;
		player.refreshPositionAndAngles(position.getX() + 0.5, position.getY() + 0.0, position.getZ() + 0.5, player.getYaw(), player.getPitch());
		player.setVelocity(Vec3d.ZERO);
		handler.sendMovementPacketToStagePosition();
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

			BlockState state = SongPlayer.MC.world.getBlockState(nbPos);

			int blockId = Block.getRawIdFromState(state);
			if (state.getBlock() instanceof NoteBlock) {
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

		// Set total missing notes
		totalMissingNotes = missingNotes.size();
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

			if (actualNoteId < 0 || actualNoteId >= 400) return true;

			int actualInstrument = actualNoteId / 25;
			int actualPtich = actualNoteId % 25;
			int targetInstrument = entry.getKey() / 25;
			int targetPitch = entry.getKey() % 25;

			if (targetPitch != actualPtich) return true;

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

	private void getAndSaveBuildSlot() {
		buildSlot = handler.getPlayer().getInventory().getSwappableHotbarSlot();
		prevHeldItem = handler.getPlayer().getInventory().getStack(buildSlot);
	}

	void restoreBuildSlot() {
		if (buildSlot == -1) return;

		handler.getPlayer().getInventory().setStack(buildSlot, prevHeldItem);
		handler.getInteractionManager().clickCreativeStack(prevHeldItem, 36 + buildSlot);
		buildSlot = -1;
		prevHeldItem = null;
	}

	private void holdNoteblock(int id, int slot) {
		PlayerInventory inventory = handler.getPlayer().getInventory();
		inventory.selectedSlot = slot;
		((ClientPlayerInteractionManagerAccessor) handler.getInteractionManager()).invokeSyncSelectedSlot();
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
		handler.getInteractionManager().clickCreativeStack(noteblockStack, 36 + slot);
	}
}
