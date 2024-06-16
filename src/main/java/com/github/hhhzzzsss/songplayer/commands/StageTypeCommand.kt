package com.github.hhhzzzsss.songplayer.commands

import com.github.hhhzzzsss.songplayer.Config
import com.github.hhhzzzsss.songplayer.SongPlayer
import com.github.hhhzzzsss.songplayer.stage.StageTypeRegistry
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

internal class StageTypeCommand : Command {
    override val name = "stageType"

    override val description = "Sets the type of noteblock stage to build"

    override fun buildNode(node: LiteralArgumentBuilder<FabricClientCommandSource>) {
        node.executes {
            val stageType = Config.getConfig().stageType
            SongPlayer.addChatMessage("§6Current stage type is §3$stageType")

            1
        }

        node.then(ClientCommandManager.argument("type", StringArgumentType.word())
            .suggests { _: CommandContext<FabricClientCommandSource?>, builder: SuggestionsBuilder ->
                for (identifier in StageTypeRegistry.instance.identifiers) {
                    builder.suggest(identifier)
                }
                builder.buildFuture()
            }
            .executes { context: CommandContext<FabricClientCommandSource?> ->
                val typeString = context.getArgument("type", String::class.java)
                val type = StageTypeRegistry.instance.getType(typeString)

                if (type == null) {
                    SongPlayer.addChatMessage("§cInvalid stage type")
                    return@executes 0
                }

                // set stage type
                Config.getConfig().stageType = type.identifier
                SongPlayer.addChatMessage("§6Set stage type to §3" + type.identifier)
                Config.saveConfigWithErrorHandling()
                1
            }
        )
    }
}