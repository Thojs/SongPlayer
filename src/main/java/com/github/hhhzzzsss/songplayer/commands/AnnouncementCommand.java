package com.github.hhhzzzsss.songplayer.commands;

import com.github.hhhzzzsss.songplayer.Config;
import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

class AnnouncementCommand extends Command {
    @Override
    public String getName() {
        return "announcement";
    }

    @Override
    public String getDescription() {
        return "Set an announcement message that is sent when you start playing a song. With setMessage, write [name] where the song name should go.";
    }

    @Override
    public void buildNode(LiteralArgumentBuilder<FabricClientCommandSource> node) {
            node.then(ClientCommandManager.literal("enable").executes(context -> {
                Config.getConfig().doAnnouncement = true;
                SongPlayer.addChatMessage("§6Enabled song announcements");
                Config.saveConfigWithErrorHandling();
                return 1;
            }));
            node.then(ClientCommandManager.literal("disable").executes(context -> {
                Config.getConfig().doAnnouncement = false;
                SongPlayer.addChatMessage("§6Disabled song announcements");
                Config.saveConfigWithErrorHandling();
                return 1;
            }));
            node.then(ClientCommandManager.literal("getmessage").executes(context -> {
                SongPlayer.addChatMessage("§6Current announcement message is §r" + Config.getConfig().announcementMessage);
                return 1;
            }));
            node.then(ClientCommandManager.literal("setmessage").then(
                ClientCommandManager.argument("message", StringArgumentType.greedyString())
                    .executes(context -> {
                        String message = context.getArgument("message", String.class);
                        Config.getConfig().announcementMessage = message;
                        SongPlayer.addChatMessage("§6Set announcement message to §r" + message);
                        Config.saveConfigWithErrorHandling();
                        return 1;
                    })
            ));
    }
}