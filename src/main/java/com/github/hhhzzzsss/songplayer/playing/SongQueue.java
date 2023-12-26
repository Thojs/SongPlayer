package com.github.hhhzzzsss.songplayer.playing;

import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.song.Song;
import com.github.hhhzzzsss.songplayer.song.SongLoaderThread;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SongQueue {
    public LinkedList<Song> songQueue = new LinkedList<>();
    public Song currentSong = null;

    public SongLoaderThread loaderThread = null;

    // Playback
    public void next() {
        currentSong = null;
        checkSong();
    }

    public void clear() {
        currentSong = null;
        songQueue.clear();
    }

    public List<Song> getQueue() {
        return songQueue;
    }

    // Loading
    public void addSong(Song song) {
        songQueue.add(song);
        checkSong();
    }

    public void loadSong(String location) {
        if (loaderThread != null) {
            SongPlayer.addChatMessage("§cAlready loading a song, cannot load another");
            return;
        }

        try {
            loaderThread = new SongLoaderThread(location);
            SongPlayer.addChatMessage("§6Loading §3" + location);
            loaderThread.start();
        } catch (IOException e) {
            SongPlayer.addChatMessage("§cFailed to load song: §4" + e.getMessage());
        }
    }

    public void loadSong(SongLoaderThread thread) {
        if (loaderThread != null) {
            SongPlayer.addChatMessage("§cAlready loading a song, cannot load another");
            return;
        }

        loaderThread = thread;
    }

    public void checkLoaderThread() {
        if (loaderThread == null || loaderThread.isAlive()) return;

        if (loaderThread.exception != null) {
            SongPlayer.addChatMessage("§cFailed to load song: §4" + loaderThread.exception.getMessage());
        } else {
            addSong(loaderThread.song);
        }
        loaderThread = null;
    }

    private void checkSong() {
        if (currentSong == null) currentSong = songQueue.pollFirst();
    }

    // Other
    public boolean isEmpty() {
        return currentSong == null && songQueue.isEmpty();
    }
}
