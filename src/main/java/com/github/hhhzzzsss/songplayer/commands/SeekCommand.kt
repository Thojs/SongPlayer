package com.github.hhhzzzsss.songplayer.commands

import com.github.hhhzzzsss.songplayer.SongPlayer
import com.github.hhhzzzsss.songplayer.playing.SongHandler
import com.github.hhhzzzsss.songplayer.utils.Util
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

internal class SeekCommand : Command {
    override val name = "seek"

    override val description = "Seek to a specific time in the song"

    override fun buildNode(node: LiteralArgumentBuilder<FabricClientCommandSource>) {
        node.then(
            ClientCommandManager.argument("time", TimestampArgumentType.timestamp())
                .executes { context: CommandContext<FabricClientCommandSource?> ->
                    if (SongHandler.instance.songQueue.isEmpty) {
                        SongPlayer.addChatMessage("§6No song is currently playing")
                        return@executes 1
                    }
                    val time = context.getArgument("time", Long::class.java)
                    SongHandler.instance.loadedSong.setTime(time)
                    SongPlayer.addChatMessage("§6Set song time to §3" + Util.formatTime(time))
                    1
                })
    }
}