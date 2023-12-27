package com.github.hhhzzzsss.songplayer.commands;

import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.playing.SongHandler;
import com.github.hhhzzzsss.songplayer.utils.SuggestionUtil;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.io.IOException;

class PlayCommand extends Command {
    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Plays a song";
    }

    @Override
    public void buildNode(LiteralArgumentBuilder<FabricClientCommandSource> node) {
        node.then(ClientCommandManager.argument("song", StringArgumentType.greedyString())
            .suggests(SuggestionUtil.safeSuggestions(SuggestionUtil::giveSongSuggestions))
            .executes(context -> {
                String songLocation = context.getArgument("song", String.class);

                try {
                    SongHandler.instance.getSongQueue().loadSong(songLocation);
                    SongPlayer.addChatMessage("§6Loading §3" + songLocation);
                } catch (IOException e) {
                    SongPlayer.addChatMessage("§cFailed to load song: §4" + e.getMessage());
                }

                return 1;
            })
        );
    }
}