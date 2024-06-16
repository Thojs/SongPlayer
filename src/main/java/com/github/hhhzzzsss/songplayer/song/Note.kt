package com.github.hhhzzzsss.songplayer.song

class Note(@JvmField val noteId: Int, @JvmField val time: Long) : Comparable<Note> {
    override fun compareTo(other: Note): Int {
        return if (time < other.time) {
            -1
        } else if (time > other.time) {
            1
        } else noteId.compareTo(other.noteId)
    }
}