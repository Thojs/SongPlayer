package com.github.hhhzzzsss.songplayer.commands;

import com.github.hhhzzzsss.songplayer.Config;
import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class SetCommandsCommand extends Command {
    @Override
    public String getName() {
        return "setCommands";
    }

    @Override
    public String getDescription() {
        return "Sets the commands used to switch gamemode";
    }

    @Override
    public void buildNode(LiteralArgumentBuilder<FabricClientCommandSource> node) {
        node.then(ClientCommandManager.literal("use")
            .then(ClientCommandManager.literal("essentials").executes(context -> {
                Config.getConfig().creativeCommand = "gmc";
                Config.getConfig().survivalCommand = "gms";
                SongPlayer.addChatMessage("§6Now using essentials gamemode commands");
                return 1;
            }))
            .then(ClientCommandManager.literal("vanilla").executes(context -> {
                Config.getConfig().creativeCommand = "gamemode creative";
                Config.getConfig().survivalCommand = "gamemode survival";
                SongPlayer.addChatMessage("§6Now using vanilla gamemode commands");
                return 1;
            }))
        );

        node.then(ClientCommandManager.literal("creative")
            .then(ClientCommandManager.argument("command", StringArgumentType.greedyString()).executes(context -> {
                String command = getCommand(context);

                Config.getConfig().creativeCommand = command;
                SongPlayer.addChatMessage("§6Set creative command to §3/" + command);

                return 1;
            }))
        );

        node.then(ClientCommandManager.literal("survival")
            .then(ClientCommandManager.argument("command", StringArgumentType.greedyString()).executes(context -> {
                String command = getCommand(context);

                Config.getConfig().survivalCommand = command;
                SongPlayer.addChatMessage("§6Set survival command to §3/" + command);

                return 1;
            }))
        );
    }

    String getCommand(CommandContext<FabricClientCommandSource> ctx) {
        String command = ctx.getArgument("command", String.class);
        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        return command;
    }
}