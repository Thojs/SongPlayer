package com.github.hhhzzzsss.songplayer.commands;

import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.playing.SongHandler;
import com.github.hhhzzzsss.songplayer.song.Song;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

class LoopCommand extends Command {
    @Override
    public String getName() {
        return "loop";
    }

    @Override
    public String getDescription() {
        return "Toggles song looping";
    }

    @Override
    public void buildNode(LiteralArgumentBuilder<FabricClientCommandSource> node) {
        node.executes(context -> {
            if (SongHandler.instance.currentSong == null) {
                SongPlayer.addChatMessage("§6No song is currently playing");
                return 1;
            }

            Song currentSong = SongHandler.instance.currentSong;
            boolean looping = currentSong.looping;
            currentSong.looping ^= true;
            currentSong.loopCount = 0;

            if (looping) {
                SongPlayer.addChatMessage("§6Disabled looping");
            } else {
                SongPlayer.addChatMessage("§6Enabled looping");
            }

            return 1;
        });
    }
}