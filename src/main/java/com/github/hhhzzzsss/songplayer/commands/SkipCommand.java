package com.github.hhhzzzsss.songplayer.commands;

import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.playing.SongHandler;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

class SkipCommand extends Command {
    @Override
    public String getName() {
        return "skip";
    }

    @Override
    public String getDescription() {
        return "Skips current song";
    }

    @Override
    public void buildNode(LiteralArgumentBuilder<FabricClientCommandSource> node) {
        node.executes(context -> {
            if (SongHandler.instance.loadedSong == null) {
                SongPlayer.addChatMessage("§6No song is currently playing");
                return 1;
            }

            SongPlayer.addChatMessage("§6Skipped the current song.");
            SongHandler.instance.loadedSong = null;

            return 1;
        });
    }
}