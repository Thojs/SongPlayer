package com.github.hhhzzzsss.songplayer.playing;

import com.github.hhhzzzsss.songplayer.Config;
import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.mixin.ClientPlayerInteractionManagerAccessor;
import com.github.hhhzzzsss.songplayer.song.Instrument;
import com.github.hhhzzzsss.songplayer.song.Song;
import com.github.hhhzzzsss.songplayer.stage.StageType;
import com.github.hhhzzzsss.songplayer.stage.StageTypeRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class StageBuilder implements Phase {
	private boolean isBuilding = false;

	public BlockPos position = null;
	public HashMap<Integer, BlockPos> noteblockPositions = new HashMap<>();

	private LinkedList<BlockPos> requiredBreaks = new LinkedList<>();
	private final TreeSet<Integer> missingNotes = new TreeSet<>();
	private int totalMissingNotes = 0;

	private static final int TOTAL_PITCHES = NoteBlock.NOTE.getValues().size(); // total amount of pitches available in base game.

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

		handler.requestGameMode(this);
		if (handler.getGameMode() != getRequiredGamemode()) return;

		// Check if building has finished & all needed note-blocks have been placed.
		if (nothingToBuild()) {
			if (buildEndDelay > 0) {
				buildEndDelay--;
				return;
			}

			checkBuildStatus();
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

			int currentNoteId = -1;
			if (state.getBlock() instanceof NoteBlock) currentNoteId = getCombinedNoteBlockId(state);

			if (currentNoteId != desiredNoteId) {
				holdNoteblock(desiredNoteId);
				if (!state.isAir()) handler.attackBlock(bp);
				handler.placeBlock(bp);
			}

			buildEndDelay = 20;
			return;
		}

		// restore everything, go to play state.
		restoreBuildSlot();
		isBuilding = false;
		SongPlayer.addChatMessage("§6Now playing §3" + handler.getLoadedSong().name);
	}

	public void needsToBuild(boolean forceCheck) {
		if (isBuilding) return;

		if (forceCheck || hasBreakingModification()) {
			checkBuildStatus();
		}

		if (nothingToBuild()) return;

		// Switch to building.
		isBuilding = true;
		handler.getLoadedSong().pause();
		buildStartDelay = 20;
	}

	private void setBuildProgressDisplay() {
		MutableText buildText = Text.empty()
					.append(Text.literal("Building noteblocks | " ).formatted(Formatting.GOLD))
					.append(Text.literal((totalMissingNotes - missingNotes.size()) + "/" + totalMissingNotes).formatted(Formatting.DARK_AQUA));

		ProgressDisplay.INSTANCE.setText(buildText, handler.getGameMode() != GameMode.CREATIVE ? Text.literal("Waiting for creative mode").formatted(Formatting.RED) : Text.empty());
	}

	public void checkBuildStatus() {
		Song song = handler.getLoadedSong();
		noteblockPositions.clear();
		missingNotes.clear();

		// Add all required notes to missingNotes
		for (int i=0; i<song.requiredNotes.length; i++) {
			if (song.requiredNotes[i]) {
				missingNotes.add(i);
			}
		}

		ArrayList<BlockPos> noteblockLocations = new ArrayList<>();
		ArrayList<BlockPos> breakLocations = new ArrayList<>();
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

			if (state.getBlock() instanceof NoteBlock) {
				int noteId = getCombinedNoteBlockId(state);
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

			if (!(bs.getBlock() instanceof NoteBlock)) return true;

			int actualInstrument = bs.get(NoteBlock.INSTRUMENT).ordinal();
			int actualPitch = bs.get(NoteBlock.NOTE);
			int targetInstrument = entry.getKey() / TOTAL_PITCHES;
			int targetPitch = entry.getKey() % TOTAL_PITCHES;

			if (targetPitch != actualPitch) return true;

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

	private int getCombinedNoteBlockId(BlockState state) {
		return state.get(NoteBlock.INSTRUMENT).ordinal() * TOTAL_PITCHES + state.get(NoteBlock.NOTE);
	}

	private void holdNoteblock(int id) {
		Instrument instrument = Instrument.getInstrumentFromId(id/TOTAL_PITCHES);
		int note = id%TOTAL_PITCHES;
		holdNoteBlock(instrument, note);
	}

	private void holdNoteBlock(Instrument instrument, int pitch) {
		PlayerInventory inventory = handler.getPlayer().getInventory();
		inventory.selectedSlot = buildSlot;
		((ClientPlayerInteractionManagerAccessor) handler.getInteractionManager()).invokeSyncSelectedSlot();

		ItemStack item = Items.NOTE_BLOCK.getDefaultStack();
		item.set(DataComponentTypes.BLOCK_STATE, new BlockStateComponent(Map.of(
				"instrument", instrument.instrumentName,
				"note", Integer.toString(pitch)
		)));

		inventory.main.set(buildSlot, item);
		handler.getInteractionManager().clickCreativeStack(item, 36 + buildSlot);
	}

	public void cleanup() {
		noteblockPositions.clear();
		requiredBreaks.clear();
		position = null;
		missingNotes.clear();
		totalMissingNotes = 0;
	}

	// Accessors
	public boolean isBuilding() {
		return isBuilding;
	}

	@Nullable
	@Override
	public GameMode getRequiredGamemode() {
		return GameMode.CREATIVE;
	}
}