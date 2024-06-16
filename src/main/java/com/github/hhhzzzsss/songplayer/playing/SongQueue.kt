package com.github.hhhzzzsss.songplayer.playing

import com.github.hhhzzzsss.songplayer.SongPlayer
import com.github.hhhzzzsss.songplayer.song.Song
import com.github.hhhzzzsss.songplayer.song.SongLoaderThread
import java.io.IOException
import java.util.*

class SongQueue {
    private val songQueue = mutableListOf<Song>()

    var currentSong: Song? = null
        private set

    private var loaderThread: SongLoaderThread? = null

    // Playback
    fun forcePlay(song: Song) {
        currentSong = song
    }

    fun next() {
        currentSong = null
        checkSong()
    }

    fun clear() {
        currentSong = null
        songQueue.clear()
    }

    val queue: Array<Song>
        // Accessors
        get() = songQueue.toTypedArray()

    // Loading
    fun addSong(song: Song) {
        songQueue.add(song)
        checkSong()
    }

    @Throws(IOException::class, IllegalStateException::class)
    fun loadSong(location: String) {
        check(loaderThread == null) { "Already loading a song, cannot load another" }

        loaderThread = SongLoaderThread(location)
        loaderThread!!.start()
    }

    @Throws(IllegalStateException::class)
    fun loadSong(thread: SongLoaderThread) {
        check(loaderThread == null) { "Already loading a song, cannot load another" }

        loaderThread = thread
    }

    fun checkLoaderThread() {
        val lt = loaderThread ?: return
        if (lt.isAlive) return

        if (lt.exception != null) {
            SongPlayer.addChatMessage("§cFailed to load song: §4" + lt.exception.message)
            loaderThread = null
            return
        }

        addSong(lt.song)
        loaderThread = null
    }

    private fun checkSong() {
        if (queue.isEmpty()) return
        if (currentSong == null) currentSong = songQueue.removeFirst()
    }

    val isEmpty: Boolean
        get() = currentSong == null && songQueue.isEmpty()
}