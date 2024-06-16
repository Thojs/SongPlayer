package com.github.hhhzzzsss.songplayer.commands

import com.github.hhhzzzsss.songplayer.SongPlayer
import com.github.hhhzzzsss.songplayer.playing.SongHandler
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

internal class SkipCommand : Command {
    override val name = "skip"

    override val description = "Skips current song"

    override fun buildNode(node: LiteralArgumentBuilder<FabricClientCommandSource>) {
        node.executes {
            if (SongHandler.instance.songQueue.isEmpty) {
                SongPlayer.addChatMessage("§6No song is currently playing")
                return@executes 1
            }
            SongPlayer.addChatMessage("§6Skipped the current song.")
            SongHandler.instance.songQueue.next()
            1
        }
    }
}