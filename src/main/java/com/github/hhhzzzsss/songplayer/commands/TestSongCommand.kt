package com.github.hhhzzzsss.songplayer.commands

import com.github.hhhzzzsss.songplayer.playing.SongHandler
import com.github.hhhzzzsss.songplayer.song.Note
import com.github.hhhzzzsss.songplayer.song.Song
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

internal class TestSongCommand : Command {
    override val name = "testSong"
    override val description = "Creates a song for testing"

    override fun buildNode(node: LiteralArgumentBuilder<FabricClientCommandSource>) {
        node.executes {
            val song = Song("Test song")
            for (i in 0..399) {
                song.add(Note(i, (i * 50).toLong()))
            }
            song.length = (400 * 50).toLong()
            SongHandler.instance.songQueue.forcePlay(song)
            1
        }
    }
}