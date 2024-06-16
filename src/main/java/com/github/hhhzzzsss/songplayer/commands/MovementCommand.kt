package com.github.hhhzzzsss.songplayer.commands;

import com.github.hhhzzzsss.songplayer.Config;
import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

class MovementCommand extends Command {
    @Override
    public String getName() {
        return "movement";
    }

    @Override
    public String getDescription() {
        return "Toggles different types of movements";
    }

    @Override
    public void buildNode(LiteralArgumentBuilder<FabricClientCommandSource> node) {
        node.then(ClientCommandManager.literal("swing").executes(context -> {
            Config.getConfig().swing = !Config.getConfig().swing;
            if (Config.getConfig().swing) {
                SongPlayer.addChatMessage("§6Enabled arm swinging");
            } else {
                SongPlayer.addChatMessage("§6Disabled arm swinging");
            }
            Config.saveConfigWithErrorHandling();

            return 1;
        }));
        node.then(ClientCommandManager.literal("rotation").executes(context -> {
            Config.getConfig().rotate = !Config.getConfig().rotate;
            if (Config.getConfig().rotate) {
                SongPlayer.addChatMessage("§6Enabled player rotation");
            } else {
                SongPlayer.addChatMessage("§6Disabled player rotation");
            }
            Config.saveConfigWithErrorHandling();

            return 1;
        }));
    }
}