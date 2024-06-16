package com.github.hhhzzzsss.songplayer.commands

import com.github.hhhzzzsss.songplayer.Config
import com.github.hhhzzzsss.songplayer.SongPlayer
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

internal class AnnouncementCommand : Command {
    override val name = "announcement"

    override val description: String = "Set an announcement message that is sent when you start playing a song. With setMessage, write [name] where the song name should go."

    override fun buildNode(node: LiteralArgumentBuilder<FabricClientCommandSource>) {
        node.then(ClientCommandManager.literal("enable").executes {
            Config.getConfig().doAnnouncement = true
            SongPlayer.addChatMessage("§6Enabled song announcements")
            Config.saveConfigWithErrorHandling()
            1
        })

        node.then(ClientCommandManager.literal("disable").executes {
            Config.getConfig().doAnnouncement = false
            SongPlayer.addChatMessage("§6Disabled song announcements")
            Config.saveConfigWithErrorHandling()
            1
        })

        node.then(ClientCommandManager.literal("get").executes {
            SongPlayer.addChatMessage("§6Current announcement message is §r" + Config.getConfig().announcementMessage)
            1
        })

        node.then(ClientCommandManager.literal("set").then(
            ClientCommandManager.argument("message", StringArgumentType.greedyString())
                .executes { context ->
                    val message = context.getArgument("message", String::class.java)
                    Config.getConfig().announcementMessage = message
                    SongPlayer.addChatMessage("§6Set announcement message to §r$message")
                    Config.saveConfigWithErrorHandling()
                    1
                }
        ))
    }
}