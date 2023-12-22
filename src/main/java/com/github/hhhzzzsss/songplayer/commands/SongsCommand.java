package com.github.hhhzzzsss.songplayer.commands;

import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.utils.SuggestionUtil;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

class SongsCommand extends Command {
    @Override
    public String getName() {
        return "songs";
    }

    @Override
    public String getDescription() {
        return "Lists available songs. If an argument is provided, lists all songs in the subdirectory.";
    }

    @Override
    public void buildNode(LiteralArgumentBuilder<FabricClientCommandSource> node) {
        node.executes(context -> {
            processCommand(SongPlayer.SONG_DIR, "");

            return 1;
        });

        node.then(ClientCommandManager.argument("path", StringArgumentType.greedyString())
            .suggests(SuggestionUtil.safeSuggestions(SuggestionUtil::giveSongDirectorySuggestions))
            .executes(context -> {
                String args = context.getArgument("path", String.class);
                Path path = SongPlayer.SONG_DIR.resolve(args);
                processCommand(path, args);

                return 1;
            })
        );
    }

    void processCommand(Path path, String args) {
        List<String> subdirectories;
        List<String> songs;
        try {
            subdirectories = Files.list(path)
                    .filter(Files::isDirectory)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .map(str -> str + "/")
                    .collect(Collectors.toList());
            songs = Files.list(path)
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            SongPlayer.addChatMessage("§cError reading folder: §4" + e.getMessage());
            return;
        }

        if (subdirectories.isEmpty() && songs.isEmpty()) {
            SongPlayer.addChatMessage("§bNo songs found. You can put midi or nbs files in the §3.minecraft/songs §6folder.");
            return;
        }

        SongPlayer.addChatMessage("§6----------------------------------------");
        SongPlayer.addChatMessage("§eContents of .minecraft/SongPlayer/songs/" + args);
        if (!subdirectories.isEmpty()) {
            SongPlayer.addChatMessage("§6Subdirectories: §3" + String.join(" ", subdirectories));
        }

        if (!songs.isEmpty()) {
            SongPlayer.addChatMessage("§6Songs: §7" + String.join(", ", songs));
        }
        SongPlayer.addChatMessage("§6----------------------------------------");
    }
}