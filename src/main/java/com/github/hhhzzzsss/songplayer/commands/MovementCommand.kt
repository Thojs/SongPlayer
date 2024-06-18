package com.github.hhhzzzsss.songplayer.commands

import com.github.hhhzzzsss.songplayer.Config
import com.github.hhhzzzsss.songplayer.SongPlayer
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

internal class MovementCommand : Command {
    override val name = "movement"
    override val description = "Toggles different types of movements"

    override fun buildNode(node: LiteralArgumentBuilder<FabricClientCommandSource>) {
        node.then(ClientCommandManager.literal("swing").executes {
            Config.getConfig().swing = !Config.getConfig().swing
            if (Config.getConfig().swing) {
                SongPlayer.addChatMessage("§6Enabled arm swinging")
            } else {
                SongPlayer.addChatMessage("§6Disabled arm swinging")
            }
            Config.saveConfigWithErrorHandling()
            1
        })

        node.then(ClientCommandManager.literal("rotation").executes {
            Config.getConfig().rotate = !Config.getConfig().rotate
            if (Config.getConfig().rotate) {
                SongPlayer.addChatMessage("§6Enabled player rotation")
            } else {
                SongPlayer.addChatMessage("§6Disabled player rotation")
            }
            Config.saveConfigWithErrorHandling()
            1
        })
    }
}