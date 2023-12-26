package com.github.hhhzzzsss.songplayer.commands;

import com.github.hhhzzzsss.songplayer.playing.SongPlayer;
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
        SongPlayer instance = SongPlayer.instance;

        node.executes(context -> {
            if (instance.currentSong == null && instance.songQueue.isEmpty()) {
                com.github.hhhzzzsss.songplayer.SongPlayer.addChatMessage("§6No song is currently playing");
                return 1;
            }

            com.github.hhhzzzsss.songplayer.SongPlayer.addChatMessage("§6------------------------------");
            com.github.hhhzzzsss.songplayer.SongPlayer.addChatMessage("§6Current song: §3" + instance.currentSong.name);

            int index = 0;
            for (Song song : instance.songQueue) {
                index++;
                com.github.hhhzzzsss.songplayer.SongPlayer.addChatMessage(String.format("§6%d. §3%s", index, song.name));
            }
            com.github.hhhzzzsss.songplayer.SongPlayer.addChatMessage("§6------------------------------");

            return 1;
        });
    }
}