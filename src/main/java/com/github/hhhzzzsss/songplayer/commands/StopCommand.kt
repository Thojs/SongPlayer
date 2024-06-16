package com.github.hhhzzzsss.songplayer.commands

import com.github.hhhzzzsss.songplayer.SongPlayer
import com.github.hhhzzzsss.songplayer.playing.SongHandler
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

internal class StopCommand : Command {
    override val name = "stop"

    override val description = "Stops playing"

    override fun buildNode(node: LiteralArgumentBuilder<FabricClientCommandSource>) {
        val instance = SongHandler.instance

        node.executes {
            if (instance.songQueue.isEmpty) {
                SongPlayer.addChatMessage("§6No song is currently playing")
                return@executes 1
            }
            instance.restoreStateAndCleanUp()
            SongPlayer.addChatMessage("§6Stopped playing")

            1
        }
    }
}