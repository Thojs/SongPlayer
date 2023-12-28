package com.github.hhhzzzsss.songplayer.playing;

import com.github.hhhzzzsss.songplayer.Config;
import com.github.hhhzzzsss.songplayer.FakePlayerEntity;
import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.song.Song;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

public class SongHandler {
    public static final SongHandler instance = new SongHandler();
    private SongHandler() {}

    public final StageBuilder stageBuilder = new StageBuilder(this);
    public final NotePlayer notePlayer = new NotePlayer(this);
    private final SongQueue songQueue = new SongQueue();

    public static FakePlayerEntity fakePlayer;

    private boolean isActive = false;

    private Song loadedSong = null;

    private GameMode originalGamemode = GameMode.CREATIVE;
    public boolean wasFlying = false;

    public void onUpdate(boolean tick) {
        // Check if loader thread is finished and handle accordingly
        songQueue.checkLoaderThread();

        // Check if current song finished playing
        if (loadedSong != null && loadedSong.finished()) {
            SongPlayer.addChatMessage("§6Done playing §3" + loadedSong.name);
            songQueue.next();
            loadedSong = null;
        }

        // Check queue and load song from it if necessary
        if (loadedSong != songQueue.getCurrentSong()) {
            setSong(songQueue.getCurrentSong());
        }

        // Run cached command if timeout reached
        checkCommandCache();

        // Check if no song is playing and, if necessary, handle cleanup
        if (loadedSong == null) {
            if (fakePlayer != null) {
                restoreStateAndCleanUp();
            } else {
                originalGamemode = getGameMode();
            }
            return;
        }

        // Set stage position if none is set.
        if (stageBuilder.position == null) {
            stageBuilder.position = getPlayer().getBlockPos();
            movePlayerToStagePosition();
        }

        // Fake player synchronization
        synchronizeFakePlayer();

        // Fly
        getAbilities().allowFlying = true;
        wasFlying = getAbilities().flying;

        // Check if stage needs to be modified.
        stageBuilder.needsToBuild(false);

        // Execute building / playing.
        if (stageBuilder.isBuilding()) {
            stageBuilder.handleBuilding(tick);
        } else {
            notePlayer.handlePlaying(tick);
        }
    }

    // Fake player
    private void synchronizeFakePlayer() {
        if (Config.getConfig().showFakePlayer && fakePlayer == null) {
            fakePlayer = new FakePlayerEntity();
            fakePlayer.copyStagePosAndPlayerLook();
        }

        if (!Config.getConfig().showFakePlayer && fakePlayer != null) {
            removeFakePlayer();
        }

        if (fakePlayer != null) fakePlayer.syncWithPlayer();
    }

    public static void removeFakePlayer() {
        if (fakePlayer != null) {
            fakePlayer.remove(Entity.RemovalReason.DISCARDED);
            fakePlayer = null;
        }
    }

    // Movements
    public void doMovements(double lookX, double lookY, double lookZ) {
        if (Config.getConfig().swing) {
            getPlayer().swingHand(Hand.MAIN_HAND);
            if (fakePlayer != null) {
                fakePlayer.swingHand(Hand.MAIN_HAND);
            }
        }

        if (Config.getConfig().rotate) {
            double d = lookX - (stageBuilder.position.getX() + 0.5);
            double e = lookY - (stageBuilder.position.getY() + getPlayer().getStandingEyeHeight());
            double f = lookZ - (stageBuilder.position.getZ() + 0.5);
            double g = Math.sqrt(d * d + f * f);
            float pitch = MathHelper.wrapDegrees((float) (-(MathHelper.atan2(e, g) * 57.2957763671875)));
            float yaw = MathHelper.wrapDegrees((float) (MathHelper.atan2(f, d) * 57.2957763671875) - 90.0f);
            if (fakePlayer != null) {
                fakePlayer.setPitch(pitch);
                fakePlayer.setYaw(yaw);
                fakePlayer.setHeadYaw(yaw);
            }

            getPlayer().networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(
                    stageBuilder.position.getX() + 0.5, stageBuilder.position.getY(), stageBuilder.position.getZ() + 0.5,
                    yaw, pitch,
                    true
            ));
        }
    }

    private void sendMovementPacketToStagePosition() {
        AbstractClientPlayerEntity entity;
        if (fakePlayer != null) {
            entity = fakePlayer;
        } else {
            entity = getPlayer();
        }

        if (entity == null) return;

        getPlayer().networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(
                stageBuilder.position.getX() + 0.5, stageBuilder.position.getY(), stageBuilder.position.getZ() + 0.5,
                entity.getYaw(), entity.getPitch(),
                true));
    }

    private void movePlayerToStagePosition() {
        BlockPos position = stageBuilder.position;
        if (position == null) return;
        ClientPlayerEntity player = getPlayer();
        player.getAbilities().allowFlying = true;
        player.getAbilities().flying = true;
        player.refreshPositionAndAngles(position.getX() + 0.5, position.getY() + 0.0, position.getZ() + 0.5, player.getYaw(), player.getPitch());
        player.setVelocity(Vec3d.ZERO);
        sendMovementPacketToStagePosition();
    }

    // Commands / messages
    private long lastCommandTime = System.currentTimeMillis();
    private String cachedCommand = null;
    private String cachedMessage = null;

    public void sendGamemodeCommand(String command) {
        cachedCommand = command;
    }

    public void sendMessage(String message) {
        cachedMessage = message;
    }

    public void checkCommandCache() {
        ClientPlayNetworkHandler handler = SongPlayer.MC.getNetworkHandler();
        if (handler == null) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime >= lastCommandTime + 1500 && cachedCommand != null) {
            handler.sendCommand(cachedCommand);
            cachedCommand = null;
            lastCommandTime = currentTime;
        } else if (currentTime >= lastCommandTime + 500 && cachedMessage != null) {
            if (cachedMessage.startsWith("/")) {
                handler.sendCommand(cachedMessage.substring(1));
            } else {
                handler.sendChatMessage(cachedMessage);
            }
            cachedMessage = null;
            lastCommandTime = currentTime;
        }
    }

    public void setCreativeIfNeeded() {
        if (getGameMode() != GameMode.CREATIVE) {
            sendGamemodeCommand(Config.getConfig().creativeCommand);
        }
    }
    public void setSurvivalIfNeeded() {
        if (getGameMode() != GameMode.SURVIVAL) {
            sendGamemodeCommand(Config.getConfig().survivalCommand);
        }
    }

    // Block placement
    public void placeBlock(BlockPos bp) {
        double fx = Math.max(0.0, Math.min(1.0, (stageBuilder.position.getX() + 0.5 - bp.getX())));
        double fy = Math.max(0.0, Math.min(1.0, (stageBuilder.position.getY() + 0.0 - bp.getY())));
        double fz = Math.max(0.0, Math.min(1.0, (stageBuilder.position.getZ() + 0.5 - bp.getZ())));
        fx += bp.getX();
        fy += bp.getY();
        fz += bp.getZ();
        getInteractionManager().interactBlock(getPlayer(), Hand.MAIN_HAND, new BlockHitResult(new Vec3d(fx, fy, fz), Direction.UP, bp, false));
        doMovements(fx, fy, fz);
    }

    public void attackBlock(BlockPos bp) {
        getInteractionManager().attackBlock(bp, Direction.UP);
        doMovements(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5);
    }

    public void stopAttack() {
        getInteractionManager().cancelBlockBreaking();
    }

    // Cleanup
    public void cleanup() {
        isActive = false;
        loadedSong = null;
        songQueue.clear();
        stageBuilder.position = null;
        removeFakePlayer();
    }

    public void restoreStateAndCleanUp() {
        movePlayerToStagePosition();
        stageBuilder.restoreBuildSlot();

        if (originalGamemode != getGameMode()) {
            if (originalGamemode == GameMode.CREATIVE) {
                setCreativeIfNeeded();
            } else if (originalGamemode == GameMode.SURVIVAL) {
                setSurvivalIfNeeded();
            }
        }

        cleanup();
    }

    // Accessors
    public SongQueue getSongQueue() {
        return songQueue;
    }

    public boolean isActive() {
        return isActive;
    }

    public BlockPos getStagePosition() {
        return stageBuilder.position;
    }

    public Song getLoadedSong() {
        return loadedSong;
    }

    // Other
    public void setSong(Song song) {
        loadedSong = song;

        // Restore after finishing queue.
        if (loadedSong == null) {
            restoreStateAndCleanUp();
            return;
        }

        if (Config.getConfig().doAnnouncement) {
            sendMessage(Config.getConfig().announcementMessage.replaceAll("\\[name]", song.name));
        }

        if (stageBuilder.position == null) {
            stageBuilder.position = getPlayer().getBlockPos();
            movePlayerToStagePosition();
        } else {
            sendMovementPacketToStagePosition();
        }

        // Check if stage needs to be modified.
        stageBuilder.needsToBuild(true);

        isActive = true;
    }

    ClientPlayerInteractionManager getInteractionManager() {
        return SongPlayer.MC.interactionManager;
    }

    GameMode getGameMode() {
        return getInteractionManager().getCurrentGameMode();
    }

    ClientPlayerEntity getPlayer() {
        return SongPlayer.MC.player;
    }

    private PlayerAbilities getAbilities() {
        return getPlayer().getAbilities();
    }
}