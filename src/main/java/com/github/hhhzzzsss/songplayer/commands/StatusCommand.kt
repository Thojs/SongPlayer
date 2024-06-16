package com.github.hhhzzzsss.songplayer.commands

import com.github.hhhzzzsss.songplayer.SongPlayer
import com.github.hhhzzzsss.songplayer.playing.SongHandler
import com.github.hhhzzzsss.songplayer.utils.Util
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import kotlin.math.min

internal class StatusCommand : Command {
    override val name = "status"

    override val description = "Gets the status of the song that is currently playing"

    override fun buildNode(node: LiteralArgumentBuilder<FabricClientCommandSource>) {
        node.executes {
            if (SongHandler.instance.songQueue.isEmpty) {
                SongPlayer.addChatMessage("§6No song is currently playing")
                return@executes 1
            }

            val currentSong = SongHandler.instance.loadedSong
            val currentTime = min(currentSong.time.toDouble(), currentSong.length.toDouble()).toLong()
            val totalTime = currentSong.length

            SongPlayer.addChatMessage(String.format(
                "§6Currently playing %s §3(%s/%s)",
                currentSong.name,
                Util.formatTime(currentTime),
                Util.formatTime(totalTime)
            ))

            1
        }
    }
}