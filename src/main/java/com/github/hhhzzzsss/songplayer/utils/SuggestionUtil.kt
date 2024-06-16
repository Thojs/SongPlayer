package com.github.hhhzzzsss.songplayer.utils

import com.github.hhhzzzsss.songplayer.SongPlayer
import com.github.hhhzzzsss.songplayer.io.SongParserRegistry.supportsExtension
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.command.CommandSource
import org.apache.commons.compress.utils.FileNameUtils
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import java.util.stream.Stream

object SuggestionUtil {
    @JvmStatic
    fun <S> safeSuggestions(suggestion: Suggestion): SuggestionProvider<S> {
        return SuggestionProvider { context: CommandContext<S>?, builder: SuggestionsBuilder ->
            val suggestions = suggestion.invoke(builder)
            if (suggestions == null) {
                return@SuggestionProvider builder.buildFuture()
            } else {
                return@SuggestionProvider suggestions
            }
        }
    }

    @JvmStatic
    fun giveSongDirectorySuggestions(builder: SuggestionsBuilder): CompletableFuture<Suggestions>? {
        val arg = builder.remaining

        val lastSlash = arg.lastIndexOf("/")
        val dirString: String
        var dir = SongPlayer.SONG_DIR
        if (lastSlash >= 0) {
            dirString = arg.substring(0, lastSlash + 1)
            try {
                dir = Util.resolveWithIOException(dir, dirString)
            } catch (e: IOException) {
                return null
            }
        } else {
            dirString = ""
        }

        val songFiles: Stream<Path>
        try {
            songFiles = Files.list(dir)
        } catch (e: IOException) {
            return null
        }
        val clipStart = if (arg.contains(" ")) {
            arg.lastIndexOf(" ") + 1
        } else {
            0
        }

        val suggestions = songFiles
            .filter { path: Path? -> Files.isDirectory(path) }
            .map { path: Path -> dirString + path.fileName.toString() + "/" }
            .filter { str: String -> str.startsWith(arg) }
            .map { str: String -> str.substring(clipStart) }

        return CommandSource.suggestMatching(suggestions, builder)
    }

    //TODO: doesn't work for directories with spaces in their path.
    @JvmStatic
    fun giveSongSuggestions(suggestionsBuilder: SuggestionsBuilder): CompletableFuture<Suggestions>? {
        val arg = suggestionsBuilder.remaining

        val lastSlash = arg.lastIndexOf("/")
        var dirString = ""
        var dir = SongPlayer.SONG_DIR
        if (lastSlash >= 0) {
            dirString = arg.substring(0, lastSlash + 1)
            try {
                dir = Util.resolveWithIOException(dir, dirString)
            } catch (e: IOException) {
                return null
            }
        }

        val songFiles: Stream<Path>
        try {
            songFiles = Files.list(dir)
        } catch (e: IOException) {
            return null
        }
        val clipStart = if (arg.contains(" ")) {
            arg.lastIndexOf(" ") + 1
        } else {
            0
        }

        val suggestionsList = ArrayList<String>()
        for (path in songFiles.toList()) {
            if (Files.isRegularFile(path)) {
                if (!supportsExtension(FileNameUtils.getExtension(path))) continue
                suggestionsList.add(dirString + path.fileName.toString())
            } else if (Files.isDirectory(path)) {
                suggestionsList.add(dirString + path.fileName.toString() + "/")
            }
        }

        val suggestions = suggestionsList.stream()
            .filter { str: String -> str.startsWith(arg) }
            .map { str: String -> str.substring(clipStart) }

        return CommandSource.suggestMatching(suggestions, suggestionsBuilder)
    }

}

typealias Suggestion = (SuggestionsBuilder) -> CompletableFuture<Suggestions>?
