package com.github.hhhzzzsss.songplayer.commands;

import com.github.hhhzzzsss.songplayer.utils.SuggestionUtil;
import com.github.hhhzzzsss.songplayer.playing.SongHandler;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

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
                SongHandler.instance.loadSong(songLocation);

                return 1;
            })
        );
    }
}