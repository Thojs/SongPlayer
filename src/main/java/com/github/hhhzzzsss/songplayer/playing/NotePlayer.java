package com.github.hhhzzzsss.songplayer.playing;

import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.utils.Util;
import com.github.hhhzzzsss.songplayer.song.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

public class NotePlayer {
    private final SongHandler handler;

    NotePlayer(SongHandler handler) {
        this.handler = handler;
    }

    // Runs every frame
    void handlePlaying(boolean tick) {
        if (tick) setPlayProgressDisplay();

        StageBuilder stageBuilder = handler.stageBuilder;
        Song currentSong = handler.loadedSong;

        if (handler.getGameMode() != GameMode.SURVIVAL) {
            currentSong.pause();
            return;
        }

        //todo move this to song handler
        if (tick) {
            if (stageBuilder.hasBreakingModification()) {
                stageBuilder.checkBuildStatus(currentSong);
            }

            if (!stageBuilder.nothingToBuild()) { // Switch to building
                stageBuilder.isBuilding = true;
                handler.setCreativeIfNeeded();
                handler.sendMovementPacketToStagePosition();
                currentSong.pause();
                stageBuilder.buildStartDelay = 20;
                SongPlayer.addChatMessage("§6Stage was altered. Rebuilding!");
                return;
            }
        }

        currentSong.play();

        // Play note blocks
        boolean somethingPlayed = false;
        currentSong.advanceTime();
        while (currentSong.reachedNextNote()) {
            Note note = currentSong.getNextNote();
            BlockPos bp = stageBuilder.noteblockPositions.get(note.noteId);
            if (bp != null) {
                handler.attackBlock(bp);
                somethingPlayed = true;
            }
        }
        if (somethingPlayed) handler.stopAttack();
    }

    private void setPlayProgressDisplay() {
        Song currentSong = handler.loadedSong;
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

        ProgressDisplay.instance.setText(songText, Text.empty());
    }
}