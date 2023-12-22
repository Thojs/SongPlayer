package com.github.hhhzzzsss.songplayer.commands;

import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.utils.Util;
import com.github.hhhzzzsss.songplayer.playing.SongHandler;
import com.mojang.brigadier.arguments.StringArgumentType;
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
        node.then(ClientCommandManager.argument("time", StringArgumentType.word()).executes(context -> {
            if (SongHandler.getInstance().currentSong == null) {
                SongPlayer.addChatMessage("§6No song is currently playing");
                return 1;
            }

            String timeString = context.getArgument("time", String.class); //todo: convert to argument?

            try {
                long time = Util.parseTime(timeString);
                SongHandler.getInstance().currentSong.setTime(time);
                SongPlayer.addChatMessage("§6Set song time to §3" + Util.formatTime(time));
            } catch (IOException e) {
                SongPlayer.addChatMessage("§cNot a valid time stamp");
            }

            return 1;
        }));
    }

//    public boolean processCommand(String args) {
//        if (SongHandler.getInstance().currentSong == null) {
//            SongPlayer.addChatMessage("§6No song is currently playing");
//            return true;
//        }
//
//        if (args.isEmpty()) return false;
//
//        try {
//            long time = Util.parseTime(args);
//            SongHandler.getInstance().currentSong.setTime(time);
//            SongPlayer.addChatMessage("§6Set song time to §3" + Util.formatTime(time));
//            return true;
//        } catch (IOException e) {
//            SongPlayer.addChatMessage("§cNot a valid time stamp");
//            return false;
//        }
//    }
}
