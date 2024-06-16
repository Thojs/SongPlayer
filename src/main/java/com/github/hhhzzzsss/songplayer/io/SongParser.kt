package com.github.hhhzzzsss.songplayer.io

import com.github.hhhzzzsss.songplayer.song.Song

interface SongParser {
    val mimeTypes: List<String>?

    val fileExtensions: List<String>?

    fun parse(bytes: ByteArray, title: String): Song?
}
