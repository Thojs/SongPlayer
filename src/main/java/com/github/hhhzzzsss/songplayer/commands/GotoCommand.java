package com.github.hhhzzzsss.songplayer.commands;

import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.utils.Util;
import com.github.hhhzzzsss.songplayer.playing.SongHandler;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.io.IOException;

class GotoCommand extends Command {
    @Override
    public String getName() {
        return "goto";
    }

    @Override
    public String getDescription() {
        return "Goes to a specific time in the song";
    }

    @Override
    public void buildNode(LiteralArgumentBuilder<FabricClientCommandSource> node) {
        node.then(ClientCommandManager.argument("time", TimestampArgumentType.timestamp()).executes(context -> {
            if (SongHandler.instance.currentSong == null) {
                SongPlayer.addChatMessage("§6No song is currently playing");
                return 1;
            }

            long time = context.getArgument("time", Long.class);
            SongHandler.instance.currentSong.setTime(time);
            SongPlayer.addChatMessage("§6Set song time to §3" + Util.formatTime(time));

            return 1;
        }));
    }
}