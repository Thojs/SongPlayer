package com.github.hhhzzzsss.songplayer.commands

import com.github.hhhzzzsss.songplayer.Config
import com.github.hhhzzzsss.songplayer.SongPlayer
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

class SetCommandsCommand : Command {
    override val name = "setCommands"
    override val description = "Modifies the commands used to switch gamemode"

    override fun buildNode(node: LiteralArgumentBuilder<FabricClientCommandSource>) {
        node.then(ClientCommandManager.literal("use")
            .then(ClientCommandManager.literal("essentials")
                .executes {
                    Config.getConfig().creativeCommand = "gmc"
                    Config.getConfig().survivalCommand = "gms"
                    SongPlayer.addChatMessage("§6Now using essentials gamemode commands")
                    1
                })
            .then(ClientCommandManager.literal("vanilla")
                .executes {
                    Config.getConfig().creativeCommand = "gamemode creative"
                    Config.getConfig().survivalCommand = "gamemode survival"
                    SongPlayer.addChatMessage("§6Now using vanilla gamemode commands")
                    1
                })
        )

        node.then(ClientCommandManager.literal("creative")
            .then(ClientCommandManager.argument("command", StringArgumentType.greedyString())
                .executes { context: CommandContext<FabricClientCommandSource?> ->
                    val command = getCommand(context)
                    Config.getConfig().creativeCommand = command
                    SongPlayer.addChatMessage("§6Set creative command to §3/$command")
                    1
                })
        )

        node.then(ClientCommandManager.literal("survival")
            .then(ClientCommandManager.argument("command", StringArgumentType.greedyString())
                .executes { context: CommandContext<FabricClientCommandSource?> ->
                    val command = getCommand(context)
                    Config.getConfig().survivalCommand = command
                    SongPlayer.addChatMessage("§6Set survival command to §3/$command")
                    1
                })
        )
    }

    fun getCommand(ctx: CommandContext<FabricClientCommandSource?>): String {
        var command = ctx.getArgument("command", String::class.java)
        if (command.startsWith("/")) {
            command = command.substring(1)
        }

        return command
    }
}