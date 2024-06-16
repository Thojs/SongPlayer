package com.github.hhhzzzsss.songplayer.playing

import com.github.hhhzzzsss.songplayer.playing.ProgressDisplay.setText
import com.github.hhhzzzsss.songplayer.utils.Util.formatTime
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.world.GameMode
import kotlin.math.min

class NotePlayer internal constructor(private val handler: SongHandler): Phase {
    override val requiredGamemode = GameMode.SURVIVAL

    fun handlePlaying(tick: Boolean) {
        if (tick) setPlayProgressDisplay()

        val stageBuilder = handler.stageBuilder
        val currentSong = handler.loadedSong

        handler.requestGameMode(this)
        if (handler.gameMode != requiredGamemode) {
            currentSong.pause()
            return
        }

        currentSong.play()

        // Play note blocks
        var somethingPlayed = false
        currentSong.advanceTime()
        while (currentSong.reachedNextNote()) {
            val note = currentSong.nextNote
            val bp = stageBuilder.noteblockPositions[note.noteId] ?: continue

            handler.attackBlock(bp)
            somethingPlayed = true
        }
        if (somethingPlayed) handler.stopAttack()
    }

    private fun setPlayProgressDisplay() {
        val currentSong = handler.loadedSong
        val currentTime = min(currentSong.time.toDouble(), currentSong.length.toDouble()).toLong()
        val totalTime = currentSong.length

        val songText = Text.empty()
            .append(Text.literal("Now playing: ").formatted(Formatting.GOLD))
            .append(Text.literal(currentSong.name).formatted(Formatting.BLUE))
            .append(Text.literal(" | ").formatted(Formatting.GOLD))
            .append(
                Text.literal(String.format("%s/%s", formatTime(currentTime), formatTime(totalTime))).formatted(
                    Formatting.DARK_AQUA
                )
            )

        if (currentSong.looping) {
            if (currentSong.loopCount > 0) {
                songText.append(
                    Text.literal(String.format(" | Loop (%d/%d)", currentSong.currentLoop, currentSong.loopCount))
                        .formatted(
                            Formatting.GOLD
                        )
                )
            } else {
                songText.append(Text.literal(" | Looping enabled").formatted(Formatting.GOLD))
            }
        }

        setText(songText, if (handler.gameMode != GameMode.SURVIVAL) Text.literal("Waiting for survival mode").formatted(Formatting.RED) else Text.empty())
    }
}