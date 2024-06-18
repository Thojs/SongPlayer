package com.github.hhhzzzsss.songplayer.commands

import com.github.hhhzzzsss.songplayer.SongPlayer
import com.github.hhhzzzsss.songplayer.playing.SongHandler
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

internal class LoopCommand : Command {
    override val name = "loop"
    override val description = "Toggles song looping"

    override fun buildNode(node: LiteralArgumentBuilder<FabricClientCommandSource>) {
        node.executes {
            if (SongHandler.instance.songQueue.isEmpty) {
                SongPlayer.addChatMessage("§6No song is currently playing")
                return@executes 1
            }
            val currentSong = SongHandler.instance.loadedSong
            val looping = currentSong.looping
            currentSong.looping = !looping
            currentSong.loopCount = 0

            if (looping) {
                SongPlayer.addChatMessage("§6Disabled looping")
            } else {
                SongPlayer.addChatMessage("§6Enabled looping")
            }
            1
        }
    }
}