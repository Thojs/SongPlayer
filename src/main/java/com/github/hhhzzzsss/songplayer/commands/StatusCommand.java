package com.github.hhhzzzsss.songplayer.commands;

import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.utils.Util;
import com.github.hhhzzzsss.songplayer.playing.SongHandler;
import com.github.hhhzzzsss.songplayer.song.Song;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

class StatusCommand extends Command {
    @Override
    public String getName() {
        return "status";
    }

    @Override
    public String getDescription() {
        return "Gets the status of the song that is currently playing";
    }

    @Override
    public void buildNode(LiteralArgumentBuilder<FabricClientCommandSource> node) {
        node.executes(context -> {
            if (SongHandler.getInstance().currentSong == null) {
                SongPlayer.addChatMessage("§6No song is currently playing");
                return 1;
            }

            Song currentSong = SongHandler.getInstance().currentSong;
            long currentTime = Math.min(currentSong.time, currentSong.length);
            long totalTime = currentSong.length;

            SongPlayer.addChatMessage(String.format("§6Currently playing %s §3(%s/%s)", currentSong.name, Util.formatTime(currentTime), Util.formatTime(totalTime)));

            return 1;
        });
    }
}
