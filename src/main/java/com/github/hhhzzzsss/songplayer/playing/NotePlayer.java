package com.github.hhhzzzsss.songplayer.playing;

import com.github.hhhzzzsss.songplayer.Config;
import com.github.hhhzzzsss.songplayer.FakePlayerEntity;
import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.utils.Util;
import com.github.hhhzzzsss.songplayer.mixin.ClientPlayerInteractionManagerAccessor;
import com.github.hhhzzzsss.songplayer.song.*;
import net.minecraft.block.Block;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.io.IOException;
import java.util.LinkedList;

public class NotePlayer {
    public static final NotePlayer instance = new NotePlayer();

    public SongLoaderThread loaderThread = null;
    public LinkedList<Song> songQueue = new LinkedList<>();
    public Song currentSong = null;
    public StageBuilder stageBuilder = null;
    public boolean building = false;

    public boolean wasFlying = false;
    public GameMode originalGamemode = GameMode.CREATIVE;
    private ItemStack prevHeldItem = null;

    public void onUpdate(boolean tick) {
        // Check current playlist and load song from it if necessary

        // Check queue and load song from it if necessary
        if (currentSong == null && !songQueue.isEmpty()) {
            setSong(songQueue.poll());
        }

        // Check if loader thread is finished and handle accordingly
        if (loaderThread != null && !loaderThread.isAlive()) {
            if (loaderThread.exception != null) {
                SongPlayer.addChatMessage("§cFailed to load song: §4" + loaderThread.exception.getMessage());
            } else {
                if (currentSong == null) {
                    setSong(loaderThread.song);
                } else {
                    queueSong(loaderThread.song);
                }
            }
            loaderThread = null;
        }

        // Run cached command if timeout reached
        checkCommandCache();

        // Check if no song is playing and, if necessary, handle cleanup
        if (currentSong == null) {
            if (stageBuilder != null || SongPlayer.fakePlayer != null) {
                restoreStateAndCleanUp();
            } else {
                originalGamemode = SongPlayer.MC.interactionManager.getCurrentGameMode();
            }
        } else {
            // Otherwise, handle song playing
            if (stageBuilder == null) {
                stageBuilder = new StageBuilder();
                stageBuilder.movePlayerToStagePosition();
            }

            if (Config.getConfig().showFakePlayer && SongPlayer.fakePlayer == null) {
                SongPlayer.fakePlayer = new FakePlayerEntity();
                SongPlayer.fakePlayer.copyStagePosAndPlayerLook();
            }

            if (!Config.getConfig().showFakePlayer && SongPlayer.fakePlayer != null) {
                SongPlayer.removeFakePlayer();
            }

            if (SongPlayer.fakePlayer != null) {
                SongPlayer.fakePlayer.getInventory().clone(SongPlayer.MC.player.getInventory());
            }

            SongPlayer.MC.player.getAbilities().allowFlying = true;
            wasFlying = SongPlayer.MC.player.getAbilities().flying;

            if (building) {
                if (tick) handleBuilding();
            } else {
                handlePlaying(tick);
            }
        }
    }

    public void loadSong(String location) {
        if (loaderThread != null) {
            SongPlayer.addChatMessage("§cAlready loading a song, cannot load another");
            return;
        }

        try {
            loaderThread = new SongLoaderThread(location);
            SongPlayer.addChatMessage("§6Loading §3" + location);
            loaderThread.start();
        } catch (IOException e) {
            SongPlayer.addChatMessage("§cFailed to load song: §4" + e.getMessage());
        }
    }

    public void loadSong(SongLoaderThread thread) {
        if (loaderThread != null) {
            SongPlayer.addChatMessage("§cAlready loading a song, cannot load another");
            return;
        }

        loaderThread = thread;
    }

    public void setSong(Song song) {
        currentSong = song;
        building = true;
        setCreativeIfNeeded();
        if (Config.getConfig().doAnnouncement) {
            sendMessage(Config.getConfig().announcementMessage.replaceAll("\\[name]", song.name));
        }

        if (stageBuilder == null) {
            stageBuilder = new StageBuilder();
            stageBuilder.movePlayerToStagePosition();
        } else {
            stageBuilder.sendMovementPacketToStagePosition();
        }

        getAndSaveBuildSlot();
        SongPlayer.addChatMessage("§6Building noteblocks");
    }

    private void queueSong(Song song) {
        songQueue.add(song);
        SongPlayer.addChatMessage("§6Added song to queue: §3" + song.name);
    }

    // Runs every tick
    private int buildStartDelay = 0;
    private int buildEndDelay = 0;
    private int buildSlot = -1;
    private void handleBuilding() {
        stageBuilder.setBuildProgressDisplay();
        if (buildStartDelay > 0) {
            buildStartDelay--;
            return;
        }

        ClientWorld world = SongPlayer.MC.world;
        if (SongPlayer.MC.interactionManager.getCurrentGameMode() != GameMode.CREATIVE) {
            return;
        }

        if (stageBuilder.nothingToBuild()) {
            if (buildEndDelay > 0) {
                buildEndDelay--;
                return;
            } else {
                stageBuilder.checkBuildStatus(currentSong);
                stageBuilder.sendMovementPacketToStagePosition();
            }
        }

        if (!stageBuilder.requiredBreaks.isEmpty()) {
            for (int i=0; i<5; i++) {
                if (stageBuilder.requiredBreaks.isEmpty()) break;
                BlockPos bp = stageBuilder.requiredBreaks.poll();
                attackBlock(bp);
            }
            buildEndDelay = 20;
        } else if (!stageBuilder.missingNotes.isEmpty()) {
            int desiredNoteId = stageBuilder.missingNotes.pollFirst();
            BlockPos bp = stageBuilder.noteblockPositions.get(desiredNoteId);
            if (bp == null) {
                return;
            }
            int blockId = Block.getRawIdFromState(world.getBlockState(bp));
            int currentNoteId = (blockId-SongPlayer.NOTEBLOCK_BASE_ID)/2;
            if (currentNoteId != desiredNoteId) {
                holdNoteblock(desiredNoteId, buildSlot);
                if (blockId != 0) {
                    attackBlock(bp);
                }
                placeBlock(bp);
            }
            buildEndDelay = 20;
        } else { // Switch to playing
            restoreBuildSlot();
            building = false;
            setSurvivalIfNeeded();
            stageBuilder.sendMovementPacketToStagePosition();
            SongPlayer.addChatMessage("§6Now playing §3" + currentSong.name);
        }
    }

    // Runs every frame
    void handlePlaying(boolean tick) {
        if (tick) setPlayProgressDisplay();

        if (SongPlayer.MC.interactionManager.getCurrentGameMode() != GameMode.SURVIVAL) {
            currentSong.pause();
            return;
        }

        if (tick) {
            if (stageBuilder.hasBreakingModification()) {
                stageBuilder.checkBuildStatus(currentSong);
            }
            if (!stageBuilder.nothingToBuild()) { // Switch to building
                building = true;
                setCreativeIfNeeded();
                stageBuilder.sendMovementPacketToStagePosition();
                currentSong.pause();
                buildStartDelay = 20;
                System.out.println("Total missing notes: " + stageBuilder.missingNotes.size());
                for (int note : stageBuilder.missingNotes) {
                    int pitch = note % 25;
                    int instrumentId = note / 25;
                    System.out.println("Missing note: " + Instrument.getInstrumentFromId(instrumentId).name() + ":" + pitch);
                }
                getAndSaveBuildSlot();
                SongPlayer.addChatMessage("§6Stage was altered. Rebuilding!");
                return;
            }
        }

        currentSong.play();

        boolean somethingPlayed = false;
        currentSong.advanceTime();
        while (currentSong.reachedNextNote()) {
            Note note = currentSong.getNextNote();
            BlockPos bp = stageBuilder.noteblockPositions.get(note.noteId);
            if (bp != null) {
                attackBlock(bp);
                somethingPlayed = true;
            }
        }
        if (somethingPlayed) {
            stopAttack();
        }

        if (currentSong.finished()) {
            SongPlayer.addChatMessage("§6Done playing §3" + currentSong.name);
            currentSong = null;
        }
    }

    public void setPlayProgressDisplay() {
        long currentTime = Math.min(currentSong.time, currentSong.length);
        long totalTime = currentSong.length;
        MutableText songText = Text.empty()
                .append(Text.literal("Now playing: ").formatted(Formatting.GOLD))
                .append(Text.literal(currentSong.name).formatted(Formatting.BLUE))
                .append(Text.literal(" | ").formatted(Formatting.GOLD))
                .append(Text.literal(String.format("%s/%s", Util.formatTime(currentTime), Util.formatTime(totalTime))).formatted(Formatting.DARK_AQUA));
        if (currentSong.looping) {
            if (currentSong.loopCount > 0) {
                songText.append(Text.literal(String.format(" | Loop (%d/%d)", currentSong.currentLoop, currentSong.loopCount)).formatted(Formatting.GOLD));
            } else {
                songText.append(Text.literal(" | Looping enabled").formatted(Formatting.GOLD));
            }
        }
        MutableText playlistText = Text.empty();
        ProgressDisplay.instance.setText(songText, playlistText);
    }

    public void cleanup() {
        currentSong = null;
        songQueue.clear();
        stageBuilder = null;
        buildSlot = -1;
        SongPlayer.removeFakePlayer();
    }

    public void restoreStateAndCleanUp() {
        if (stageBuilder != null) stageBuilder.movePlayerToStagePosition();
        if (originalGamemode != SongPlayer.MC.interactionManager.getCurrentGameMode()) {
            if (originalGamemode == GameMode.CREATIVE) {
                setCreativeIfNeeded();
            }
            else if (originalGamemode == GameMode.SURVIVAL) {
                setSurvivalIfNeeded();
            }
        }
        restoreBuildSlot();
        cleanup();
    }

    private long lastCommandTime = System.currentTimeMillis();
    private String cachedCommand = null;
    private String cachedMessage = null;

    private void sendGamemodeCommand(String command) {
        cachedCommand = command;
    }
    private void sendMessage(String message) {
        cachedMessage = message;
    }
    private void checkCommandCache() {
        long currentTime = System.currentTimeMillis();
        if (currentTime >= lastCommandTime + 1500 && cachedCommand != null) {
            SongPlayer.MC.getNetworkHandler().sendCommand(cachedCommand);
            cachedCommand = null;
            lastCommandTime = currentTime;
        }
        else if (currentTime >= lastCommandTime + 500 && cachedMessage != null) {
            if (cachedMessage.startsWith("/")) {
                SongPlayer.MC.getNetworkHandler().sendCommand(cachedMessage.substring(1));
            }
            else {
                SongPlayer.MC.getNetworkHandler().sendChatMessage(cachedMessage);
            }
            cachedMessage = null;
            lastCommandTime = currentTime;
        }
    }

    private void setCreativeIfNeeded() {
        if (SongPlayer.MC.interactionManager.getCurrentGameMode() != GameMode.CREATIVE) {
            sendGamemodeCommand(Config.getConfig().creativeCommand);
        }
    }
    private void setSurvivalIfNeeded() {
        if (SongPlayer.MC.interactionManager.getCurrentGameMode() != GameMode.SURVIVAL) {
            sendGamemodeCommand(Config.getConfig().survivalCommand);
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

    private void placeBlock(BlockPos bp) {
        double fx = Math.max(0.0, Math.min(1.0, (stageBuilder.position.getX() + 0.5 - bp.getX())));
        double fy = Math.max(0.0, Math.min(1.0, (stageBuilder.position.getY() + 0.0 - bp.getY())));
        double fz = Math.max(0.0, Math.min(1.0, (stageBuilder.position.getZ() + 0.5 - bp.getZ())));
        fx += bp.getX();
        fy += bp.getY();
        fz += bp.getZ();
        SongPlayer.MC.interactionManager.interactBlock(SongPlayer.MC.player, Hand.MAIN_HAND, new BlockHitResult(new Vec3d(fx, fy, fz), Direction.UP, bp, false));
        doMovements(fx, fy, fz);
    }

    private void attackBlock(BlockPos bp) {
        SongPlayer.MC.interactionManager.attackBlock(bp, Direction.UP);
        doMovements(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5);
    }

    private void stopAttack() {
        SongPlayer.MC.interactionManager.cancelBlockBreaking();
    }

    private void doMovements(double lookX, double lookY, double lookZ) {
        if (Config.getConfig().swing) {
            SongPlayer.MC.player.swingHand(Hand.MAIN_HAND);
            if (SongPlayer.fakePlayer != null) {
                SongPlayer.fakePlayer.swingHand(Hand.MAIN_HAND);
            }
        }
        if (Config.getConfig().rotate) {
            double d = lookX - (stageBuilder.position.getX() + 0.5);
            double e = lookY - (stageBuilder.position.getY() + SongPlayer.MC.player.getStandingEyeHeight());
            double f = lookZ - (stageBuilder.position.getZ() + 0.5);
            double g = Math.sqrt(d * d + f * f);
            float pitch = MathHelper.wrapDegrees((float) (-(MathHelper.atan2(e, g) * 57.2957763671875)));
            float yaw = MathHelper.wrapDegrees((float) (MathHelper.atan2(f, d) * 57.2957763671875) - 90.0f);
            if (SongPlayer.fakePlayer != null) {
                SongPlayer.fakePlayer.setPitch(pitch);
                SongPlayer.fakePlayer.setYaw(yaw);
                SongPlayer.fakePlayer.setHeadYaw(yaw);
            }
            SongPlayer.MC.player.networkHandler.getConnection().send(new PlayerMoveC2SPacket.Full(
                    stageBuilder.position.getX() + 0.5, stageBuilder.position.getY(), stageBuilder.position.getZ() + 0.5,
                    yaw, pitch,
                    true));
        }
    }

    private void getAndSaveBuildSlot() {
        buildSlot = SongPlayer.MC.player.getInventory().getSwappableHotbarSlot();
        prevHeldItem = SongPlayer.MC.player.getInventory().getStack(buildSlot);
    }
    private void restoreBuildSlot() {
        if (buildSlot != -1) {
            SongPlayer.MC.player.getInventory().setStack(buildSlot, prevHeldItem);
            SongPlayer.MC.interactionManager.clickCreativeStack(prevHeldItem, 36 + buildSlot);
            buildSlot = -1;
        }
    }
}