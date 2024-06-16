package com.github.hhhzzzsss.songplayer.item

import com.github.hhhzzzsss.songplayer.utils.Util.getStyledText
import com.github.hhhzzzsss.songplayer.utils.Util.setItemLore
import com.github.hhhzzzsss.songplayer.utils.Util.setItemName
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.NbtComponent
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtElement
import net.minecraft.text.Style
import net.minecraft.util.Formatting

object SongItemUtils {
    private const val SONG_ITEM_KEY = "SongItemData"

    @JvmStatic
    fun createSongItem(stack: ItemStack, songData: ByteArray, filename: String, displayName: String): ItemStack {
        val data = SongItemData(displayName, filename, songData)

        setData(stack, data)
        setItemDisplayData(stack)

        return stack
    }

    @JvmStatic
    fun getData(stack: ItemStack): SongItemData? {
        val nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt()

        if (!nbt.contains(SONG_ITEM_KEY, NbtElement.COMPOUND_TYPE.toInt())) {
            return null
        }

        return SongItemData.fromNbt(nbt.getCompound(SONG_ITEM_KEY))
    }

    @JvmStatic
    fun setData(stack: ItemStack, data: SongItemData) {
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack) { nbt ->
            nbt.put(SONG_ITEM_KEY, data.toNbt())
        }
    }

    @JvmStatic
    fun setItemDisplayData(stack: ItemStack) {
        val data = getData(stack) ?: return

        var name = data.displayName
        if (name.isEmpty()) name = data.fileName
        if (name.isEmpty()) name = "unnamed"

        val nameText = getStyledText(name, Style.EMPTY.withColor(Formatting.DARK_AQUA).withItalic(false))

        setItemName(stack, nameText)
        setItemLore(stack,
            getStyledText("Song Item", Style.EMPTY.withColor(Formatting.YELLOW).withItalic(false)),
            getStyledText("Right click to play", Style.EMPTY.withColor(Formatting.AQUA).withItalic(false)),
            getStyledText("Requires SongPlayer 3.0+", Style.EMPTY.withColor(Formatting.GOLD).withItalic(false)),
            getStyledText(
                "https://github.com/hhhzzzsss/SongPlayer",
                Style.EMPTY.withColor(Formatting.GRAY).withItalic(false)
            )
        )
    }

    @JvmStatic
    fun isSongItem(stack: ItemStack): Boolean {
        return getData(stack) != null
    }
}
