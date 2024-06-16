package com.github.hhhzzzsss.songplayer.item

import com.github.hhhzzzsss.songplayer.io.SPParser
import com.github.hhhzzzsss.songplayer.song.Song
import net.minecraft.nbt.NbtCompound
import java.util.*

class SongItemData(var displayName: String, var fileName: String, var songData: ByteArray) {
    val song: Song?
        get() {
            var name = fileName
            if (displayName.isNotEmpty()) name = displayName
            if (name.isEmpty()) name = "Unnamed song"

            return SPParser().parse(songData, name)
        }

    fun toNbt(): NbtCompound {
        val nbt = NbtCompound()
        nbt.putString(DISPLAY_NAME_KEY, displayName)
        nbt.putString(FILE_NAME_KEY, fileName)
        nbt.putByteArray(SONG_DATA_KEY, songData)

        return nbt
    }

    companion object {
        private const val SONG_DATA_KEY = "SongData"
        private const val FILE_NAME_KEY = "FileName"
        const val DISPLAY_NAME_KEY = "DisplayName"

        fun fromNbt(nbt: NbtCompound): SongItemData {
            val data = if (nbt.contains(SONG_DATA_KEY, NbtCompound.BYTE_ARRAY_TYPE.toInt())) {
                nbt.getByteArray(SONG_DATA_KEY)
            } else Base64.getDecoder().decode(nbt.getString(SONG_DATA_KEY)) // Previous way of decoding data, still here so it's backwards compatible with items created in previous versions of our mod

            return SongItemData(
                nbt.getString(DISPLAY_NAME_KEY),
                nbt.getString(FILE_NAME_KEY),
                data
            )
        }
    }
}