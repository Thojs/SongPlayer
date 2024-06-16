package com.github.hhhzzzsss.songplayer.commands

import com.github.hhhzzzsss.songplayer.SongPlayer
import com.github.hhhzzzsss.songplayer.playing.SongHandler
import com.github.hhhzzzsss.songplayer.utils.SuggestionUtil
import com.github.hhhzzzsss.songplayer.utils.SuggestionUtil.safeSuggestions
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import java.io.IOException

internal class PlayCommand : Command {
    override val name = "play"

    override val description = "Plays a song"

    override fun buildNode(node: LiteralArgumentBuilder<FabricClientCommandSource>) {
        node.then(ClientCommandManager.argument("song", StringArgumentType.greedyString())
            .suggests(safeSuggestions(SuggestionUtil::giveSongSuggestions))
            .executes { context ->
                val songLocation = context.getArgument("song", String::class.java)
                try {
                    SongHandler.instance.songQueue.loadSong(songLocation)
                    SongPlayer.addChatMessage("§6Loading §3$songLocation")
                } catch (e: IOException) {
                    SongPlayer.addChatMessage("§cFailed to load song: §4" + e.message)
                }

                1
            }
        )
    }
}