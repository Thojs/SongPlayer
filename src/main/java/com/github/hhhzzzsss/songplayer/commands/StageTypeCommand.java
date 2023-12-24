package com.github.hhhzzzsss.songplayer.commands;

import com.github.hhhzzzsss.songplayer.Config;
import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.stage.StageType;
import com.github.hhhzzzsss.songplayer.stage.StageTypeRegistry;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

class StageTypeCommand extends Command {
    @Override
    public String getName() {
        return "stageType";
    }

    @Override
    public String getDescription() {
        return "Sets the type of noteblock stage to build";
    }

    @Override
    public void buildNode(LiteralArgumentBuilder<FabricClientCommandSource> node) {
        node.executes(context -> {
            String stageType = Config.getConfig().stageType;

            SongPlayer.addChatMessage("§6Current stage type is §3" + stageType);

            return 1;
        });

        node.then(ClientCommandManager.argument("type", StringArgumentType.word())
            .suggests((context, builder) -> {
                for (String identifier : StageTypeRegistry.instance.getIdentifiers()) {
                    builder.suggest(identifier);
                }

                return builder.buildFuture();
            })
            .executes(context -> {
                String typeString = context.getArgument("type", String.class);
                StageType type = StageTypeRegistry.instance.getType(typeString);

                if (type == null) {
                    SongPlayer.addChatMessage("§cInvalid stage type");
                    return 0;
                }

                // set stage type
                Config.getConfig().stageType = type.getIdentifier();
                SongPlayer.addChatMessage("§6Set stage type to §3" + type.getIdentifier());
                Config.saveConfigWithErrorHandling();

                return 1;
            })
        );
    }
}