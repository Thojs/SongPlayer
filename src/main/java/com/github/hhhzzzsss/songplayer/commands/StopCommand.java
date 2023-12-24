package com.github.hhhzzzsss.songplayer.commands;

import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.playing.SongHandler;
import com.github.hhhzzzsss.songplayer.song.Song;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

class StopCommand extends Command {
    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return "Stops playing";
    }

    @Override
    public void buildNode(LiteralArgumentBuilder<FabricClientCommandSource> node) {
        SongHandler instance = SongHandler.instance;

        node.executes(context -> {
            if (instance.currentSong == null && instance.songQueue.isEmpty()) {
                SongPlayer.addChatMessage("§6No song is currently playing");
                return 1;
            }

            if (instance.stageBuilder != null) {
                instance.stageBuilder.movePlayerToStagePosition();
            }

            instance.restoreStateAndCleanUp();
            SongPlayer.addChatMessage("§6Stopped playing");

            return 1;
        });
    }
}