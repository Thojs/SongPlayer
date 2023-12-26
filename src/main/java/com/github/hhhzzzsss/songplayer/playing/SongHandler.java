package com.github.hhhzzzsss.songplayer.playing;

import com.github.hhhzzzsss.songplayer.Config;
import com.github.hhhzzzsss.songplayer.FakePlayerEntity;
import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.song.Song;
import net.minecraft.client.network.ClientPlayNetworkHandler;
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

    public boolean isPlaying = false;

    public SongQueue songQueue = new SongQueue();
    public Song loadedSong = null;

    public GameMode originalGamemode = GameMode.CREATIVE;
    public boolean wasFlying = false;

    public void onUpdate(boolean tick) {
        // Check if loader thread is finished and handle accordingly
        songQueue.checkLoaderThread();

        // Check queue and load song from it if necessary
        if (loadedSong != songQueue.currentSong) {
            setSong(songQueue.currentSong);
        }

        // Run cached command if timeout reached
        checkCommandCache();

        // Check if no song is playing and, if necessary, handle cleanup
        if (loadedSong == null) {
            if (SongPlayer.fakePlayer != null) {
                restoreStateAndCleanUp();
            } else {
                originalGamemode = SongPlayer.MC.interactionManager.getCurrentGameMode();
            }
        } else {
            // Otherwise, handle song playing
            if (stageBuilder.position == null) {
                stageBuilder.position = SongPlayer.MC.player.getBlockPos();
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

            if (stageBuilder.isBuilding) {
                stageBuilder.handleBuilding(tick);
            } else {
                notePlayer.handlePlaying(tick);
            }
        }
    }

    // Movements
    public void doMovements(double lookX, double lookY, double lookZ) {
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
                    true
            ));
        }
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
        if (SongPlayer.MC.interactionManager.getCurrentGameMode() != GameMode.CREATIVE) {
            sendGamemodeCommand(Config.getConfig().creativeCommand);
        }
    }
    public void setSurvivalIfNeeded() {
        if (SongPlayer.MC.interactionManager.getCurrentGameMode() != GameMode.SURVIVAL) {
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
        SongPlayer.MC.interactionManager.interactBlock(SongPlayer.MC.player, Hand.MAIN_HAND, new BlockHitResult(new Vec3d(fx, fy, fz), Direction.UP, bp, false));
        doMovements(fx, fy, fz);
    }

    public void attackBlock(BlockPos bp) {
        SongPlayer.MC.interactionManager.attackBlock(bp, Direction.UP);
        doMovements(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5);
    }

    public void stopAttack() {
        SongPlayer.MC.interactionManager.cancelBlockBreaking();
    }

    // Cleanup
    public void cleanup() {
        isPlaying = false;
        loadedSong = null;
        songQueue.clear();
        stageBuilder.position = null;
        SongPlayer.removeFakePlayer();
    }

    public void restoreStateAndCleanUp() {
        stageBuilder.movePlayerToStagePosition();
        stageBuilder.restoreBuildSlot();

        if (originalGamemode != SongPlayer.MC.interactionManager.getCurrentGameMode()) {
            if (originalGamemode == GameMode.CREATIVE) {
                setCreativeIfNeeded();
            } else if (originalGamemode == GameMode.SURVIVAL) {
                setSurvivalIfNeeded();
            }
        }

        cleanup();
    }

    // Other
    public void setSong(Song song) {
        loadedSong = song;

        // Restore after finishing queue.
        if (loadedSong == null) {
            restoreStateAndCleanUp();
            return;
        }

        isPlaying = true;

        stageBuilder.isBuilding = true;
        setCreativeIfNeeded();

        if (Config.getConfig().doAnnouncement) {
            sendMessage(Config.getConfig().announcementMessage.replaceAll("\\[name]", song.name));
        }

        if (stageBuilder.position == null) {
            stageBuilder.position = SongPlayer.MC.player.getBlockPos();
            stageBuilder.movePlayerToStagePosition();
        } else {
            stageBuilder.sendMovementPacketToStagePosition();
        }

        stageBuilder.getAndSaveBuildSlot();
        SongPlayer.addChatMessage("§6Building noteblocks");
    }
}