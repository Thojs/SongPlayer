package com.github.hhhzzzsss.songplayer.playing;

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

    void handlePlaying(boolean tick) {
        if (tick) setPlayProgressDisplay();

        StageBuilder stageBuilder = handler.stageBuilder;
        Song currentSong = handler.getLoadedSong();

        if (handler.getGameMode() != GameMode.SURVIVAL) {
            currentSong.pause();
            return;
        }

        currentSong.play();

        // Play note blocks
        boolean somethingPlayed = false;
        currentSong.advanceTime();
        while (currentSong.reachedNextNote()) {
            Note note = currentSong.getNextNote();
            BlockPos bp = stageBuilder.noteblockPositions.get(note.noteId);
            if (bp == null) continue;

            handler.attackBlock(bp);
            somethingPlayed = true;
        }
        if (somethingPlayed) handler.stopAttack();
    }

    private void setPlayProgressDisplay() {
        Song currentSong = handler.getLoadedSong();
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

        ProgressDisplay.instance.setText(songText, handler.getGameMode() != GameMode.SURVIVAL ? Text.literal("Waiting for survival mode").formatted(Formatting.RED) : Text.empty());
    }
}