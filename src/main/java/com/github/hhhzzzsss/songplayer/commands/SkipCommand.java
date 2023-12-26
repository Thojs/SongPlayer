package com.github.hhhzzzsss.songplayer.commands;

import com.github.hhhzzzsss.songplayer.playing.NotePlayer;
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
            if (NotePlayer.instance.currentSong == null) {
                com.github.hhhzzzsss.songplayer.SongPlayer.addChatMessage("§6No song is currently playing");
                return 1;
            }

            com.github.hhhzzzsss.songplayer.SongPlayer.addChatMessage("§6Skipped the current song.");
            NotePlayer.instance.currentSong = null;

            return 1;
        });
    }
}