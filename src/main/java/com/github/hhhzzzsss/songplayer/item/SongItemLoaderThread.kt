package com.github.hhhzzzsss.songplayer.item

import com.github.hhhzzzsss.songplayer.item.SongItemUtils.getData
import com.github.hhhzzzsss.songplayer.song.SongLoaderThread
import net.minecraft.item.ItemStack
import kotlin.math.max

class SongItemLoaderThread(private val stack: ItemStack) : SongLoaderThread() {
    @JvmField
    var data: SongItemData? = null
    @JvmField
    var maxNotesPerSecond = 0
    @JvmField
    var avgNotesPerSecond = 0.0

    override fun run() {
        try {
            data = getData(stack)
            song = data!!.song

            val song = song ?: return
            song.sort()

            var j = 0
            var notesInSecond = 0
            for (currNote in song.notes) {
                notesInSecond++
                while (song.notes[j].time + 1000 < currNote.time) {
                    j++
                    notesInSecond--
                }
                maxNotesPerSecond = max(notesInSecond.toDouble(), maxNotesPerSecond.toDouble()).toInt()
            }
            avgNotesPerSecond = song.notes.size * 1000.0 / song.length
        } catch (e: Exception) {
            exception = e
            e.printStackTrace()
        }
    }
}