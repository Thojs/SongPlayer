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

    public NotePlayer(SongHandler handler) {
        this.handler = handler;
    }

    // Runs every frame
    void handlePlaying(boolean tick) {
        if (tick) setPlayProgressDisplay();

        StageBuilder stageBuilder = handler.stageBuilder;
        Song currentSong = handler.loadedSong;

        if (SongPlayer.MC.interactionManager.getCurrentGameMode() != GameMode.SURVIVAL) {
            currentSong.pause();
            return;
        }

        if (tick) {
            if (stageBuilder.hasBreakingModification()) {
                stageBuilder.checkBuildStatus(currentSong);
            }
            if (!stageBuilder.nothingToBuild()) { // Switch to building
                stageBuilder.isBuilding = true;
                handler.setCreativeIfNeeded();
                stageBuilder.sendMovementPacketToStagePosition();
                currentSong.pause();
                stageBuilder.buildStartDelay = 20;
                System.out.println("Total missing notes: " + stageBuilder.missingNotes.size());
                for (int note : stageBuilder.missingNotes) {
                    int pitch = note % 25;
                    int instrumentId = note / 25;
                    System.out.println("Missing note: " + Instrument.getInstrumentFromId(instrumentId).name() + ":" + pitch);
                }
                stageBuilder.getAndSaveBuildSlot();
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
                handler.attackBlock(bp);
                somethingPlayed = true;
            }
        }
        if (somethingPlayed) handler.stopAttack();


        // Done playing.
        if (currentSong.finished()) {
            SongPlayer.addChatMessage("§6Done playing §3" + currentSong.name);
            handler.songQueue.next();
            handler.loadedSong = null;
        }
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
        MutableText playlistText = Text.empty();
        ProgressDisplay.instance.setText(songText, playlistText);
    }
}