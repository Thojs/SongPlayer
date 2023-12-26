package com.github.hhhzzzsss.songplayer.commands;

import com.github.hhhzzzsss.songplayer.playing.NotePlayer;
import com.github.hhhzzzsss.songplayer.song.Note;
import com.github.hhhzzzsss.songplayer.song.Song;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

class TestSongCommand extends Command {
    @Override
    public String getName() {
        return "testSong";
    }

    @Override
    public String getDescription() {
        return "Creates a song for testing";
    }

    @Override
    public void buildNode(LiteralArgumentBuilder<FabricClientCommandSource> node) {
        node.executes(context -> {
            Song song = new Song("test_song");
            for (int i=0; i<400; i++) {
                song.add(new Note(i, i*50));
            }
            song.length = 400*50;
            NotePlayer.instance.setSong(song);

            return 1;
        });
    }
}