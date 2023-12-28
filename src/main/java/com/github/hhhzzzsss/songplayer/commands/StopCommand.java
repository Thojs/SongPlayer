package com.github.hhhzzzsss.songplayer.commands;

import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.playing.SongHandler;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

class StopCommand extends Command {
    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return "Stops playing";
    }

    @Override
    public void buildNode(LiteralArgumentBuilder<FabricClientCommandSource> node) {
        SongHandler instance = SongHandler.instance;

        node.executes(context -> {
            if (instance.getSongQueue().isEmpty()) {
                SongPlayer.addChatMessage("§6No song is currently playing");
                return 1;
            }

            instance.restoreStateAndCleanUp();
            SongPlayer.addChatMessage("§6Stopped playing");

            return 1;
        });
    }
}