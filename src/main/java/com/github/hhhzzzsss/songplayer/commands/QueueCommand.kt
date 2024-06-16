package com.github.hhhzzzsss.songplayer.commands

import com.github.hhhzzzsss.songplayer.SongPlayer
import com.github.hhhzzzsss.songplayer.playing.SongHandler
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

internal class QueueCommand : Command {
    override val name = "queue"

    override val description = "Shows the current song queue"

    override fun buildNode(node: LiteralArgumentBuilder<FabricClientCommandSource>) {
        val instance = SongHandler.instance

        node.executes {
            if (instance.songQueue.isEmpty) {
                SongPlayer.addChatMessage("§6No song is currently playing")
                return@executes 1
            }

            SongPlayer.addChatMessage("§6------------------------------")
            SongPlayer.addChatMessage("§6Current song: §3" + instance.loadedSong.name)

            var index = 0
            for (song in instance.songQueue.queue) {
                index++
                SongPlayer.addChatMessage(String.format("§6%d. §3%s", index, song.name))
            }
            SongPlayer.addChatMessage("§6------------------------------")

            1
        }
    }
}