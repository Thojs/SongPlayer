package com.github.hhhzzzsss.songplayer.commands;

import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.playing.SongHandler;
import com.github.hhhzzzsss.songplayer.song.Song;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

class QueueCommand extends Command {
    @Override
    public String getName() {
        return "queue";
    }

    @Override
    public String getDescription() {
        return "Shows the current song queue";
    }

    @Override
    public void buildNode(LiteralArgumentBuilder<FabricClientCommandSource> node) {
        SongHandler instance = SongHandler.instance;

        node.executes(context -> {
            if (instance.getSongQueue().isEmpty()) {
                SongPlayer.addChatMessage("§6No song is currently playing");
                return 1;
            }

            SongPlayer.addChatMessage("§6------------------------------");
            SongPlayer.addChatMessage("§6Current song: §3" + instance.loadedSong.name);

            int index = 0;
            for (Song song : instance.getSongQueue().getQueue()) {
                index++;
                SongPlayer.addChatMessage(String.format("§6%d. §3%s", index, song.name));
            }
            SongPlayer.addChatMessage("§6------------------------------");

            return 1;
        });
    }
}