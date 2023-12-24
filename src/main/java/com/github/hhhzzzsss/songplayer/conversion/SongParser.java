package com.github.hhhzzzsss.songplayer.conversion;

import com.github.hhhzzzsss.songplayer.song.Song;

import java.util.Collection;

public interface SongParser {
    Collection<String> getMIMETypes();

    Collection<String> getFileExtensions();

    Song parse(byte[] bytes, String title);
}
