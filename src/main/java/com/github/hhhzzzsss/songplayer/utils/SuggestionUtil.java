package com.github.hhhzzzsss.songplayer.utils;

import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.conversion.SongParserRegistry;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import org.apache.commons.compress.utils.FileNameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static com.github.hhhzzzsss.songplayer.utils.Util.resolveWithIOException;

public class SuggestionUtil {
    public interface Suggestion {
        CompletableFuture<Suggestions> giveSuggestions(SuggestionsBuilder builder);
    }

    public static <S> SuggestionProvider<S> safeSuggestions(Suggestion suggestion) {
        return (context, builder) -> {
            CompletableFuture<Suggestions> suggestions = suggestion.giveSuggestions(builder);
            if (suggestions == null) {
                return builder.buildFuture();
            } else {
                return suggestions;
            }
        };
    }

    public static CompletableFuture<Suggestions> givePlaylistSuggestions(SuggestionsBuilder builder) {
        if (!Files.exists(SongPlayer.PLAYLISTS_DIR)) return null;
        try {
            return CommandSource.suggestMatching(
                    Files.list(SongPlayer.PLAYLISTS_DIR)
                            .filter(Files::isDirectory)
                            .map(Path::getFileName)
                            .map(Path::toString),
                    builder
            );
        } catch (IOException e) {
            return null;
        }
    }

    public static CompletableFuture<Suggestions> giveSongDirectorySuggestions(SuggestionsBuilder builder) {
        String arg = builder.getRemaining();

        int lastSlash = arg.lastIndexOf("/");
        String dirString;
        Path dir = SongPlayer.SONG_DIR;
        if (lastSlash >= 0) {
            dirString = arg.substring(0, lastSlash+1);
            try {
                dir = resolveWithIOException(dir, dirString);
            } catch (IOException e) {
                return null;
            }
        } else {
            dirString = "";
        }

        Stream<Path> songFiles;
        try {
            songFiles = Files.list(dir);
        } catch (IOException e) {
            return null;
        }

        int clipStart;
        if (arg.contains(" ")) {
            clipStart = arg.lastIndexOf(" ") + 1;
        } else {
            clipStart = 0;
        }

        Stream<String> suggestions = songFiles
                .filter(Files::isDirectory)
                .map(path -> dirString + path.getFileName().toString() + "/")
                .filter(str -> str.startsWith(arg))
                .map(str -> str.substring(clipStart));

        return CommandSource.suggestMatching(suggestions, builder);
    }

    //TODO: doesn't work for directories with spaces in their name.
    public static CompletableFuture<Suggestions> giveSongSuggestions(SuggestionsBuilder suggestionsBuilder) {
        String arg = suggestionsBuilder.getRemaining();

        int lastSlash = arg.lastIndexOf("/");
        String dirString = "";
        Path dir = SongPlayer.SONG_DIR;
        if (lastSlash >= 0) {
            dirString = arg.substring(0, lastSlash+1);
            try {
                dir = resolveWithIOException(dir, dirString);
            } catch (IOException e) {
                return null;
            }
        }

        Stream<Path> songFiles;
        try {
            songFiles = Files.list(dir);
        } catch (IOException e) {
            return null;
        }

        int clipStart;
        if (arg.contains(" ")) {
            clipStart = arg.lastIndexOf(" ") + 1;
        } else {
            clipStart = 0;
        }

        ArrayList<String> suggestionsList = new ArrayList<>();
        for (Path path : songFiles.toList()) {
            if (Files.isRegularFile(path)) {
                if (!SongParserRegistry.instance.getSupportedFileExtensions().contains(FileNameUtils.getExtension(path))) continue;
                suggestionsList.add(dirString + path.getFileName().toString());
            } else if (Files.isDirectory(path)) {
                suggestionsList.add(dirString + path.getFileName().toString() + "/");
            }
        }

        Stream<String> suggestions = suggestionsList.stream()
                .filter(str -> str.startsWith(arg))
                .map(str -> str.substring(clipStart));

        return CommandSource.suggestMatching(suggestions, suggestionsBuilder);
    }
}
