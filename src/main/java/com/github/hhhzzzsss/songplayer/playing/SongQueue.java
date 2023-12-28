package com.github.hhhzzzsss.songplayer.playing;

import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.song.Song;
import com.github.hhhzzzsss.songplayer.song.SongLoaderThread;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SongQueue {
    private final LinkedList<Song> songQueue = new LinkedList<>();
    private Song currentSong = null;

    private SongLoaderThread loaderThread = null;

    // Playback
    public void next() {
        currentSong = null;
        checkSong();
    }

    public void clear() {
        currentSong = null;
        clearQueue();
    }

    public void clearQueue() {
        songQueue.clear();
    }

    // Accessors
    public List<Song> getQueue() {
        return List.copyOf(songQueue);
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    // Loading
    public void addSong(Song song) {
        songQueue.add(song);
        checkSong();
    }

    public void loadSong(String location) throws IOException, IllegalStateException {
        if (loaderThread != null) {
            throw new IllegalStateException("Already loading a song, cannot load another");
        }

        loaderThread = new SongLoaderThread(location);
        loaderThread.start();
    }

    public void loadSong(SongLoaderThread thread) throws IllegalStateException {
        if (loaderThread != null) {
            throw new IllegalStateException("Already loading a song, cannot load another");
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
