package com.github.hhhzzzsss.songplayer.utils

import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.LoreComponent
import net.minecraft.item.ItemStack
import net.minecraft.text.MutableText
import net.minecraft.text.PlainTextContent
import net.minecraft.text.Style
import net.minecraft.text.Text
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.text.CharacterIterator
import java.text.StringCharacterIterator
import kotlin.math.abs

object Util {
    @JvmStatic
    fun createDirectoriesSilently(path: Path) {
        try {
            Files.createDirectories(path)
        } catch (ignored: IOException) {
        }
    }

    @JvmStatic
    @Throws(IOException::class)
    fun resolveWithIOException(path: Path, other: String): Path {
        try {
            return path.resolve(other)
        } catch (e: InvalidPathException) {
            throw IOException(e.message)
        }
    }

    @JvmStatic
    fun formatTime(milliseconds: Long): String {
        var temp = abs(milliseconds.toDouble()).toLong()
        temp /= 1000
        val seconds = temp % 60
        temp /= 60
        val minutes = temp % 60
        temp /= 60
        val hours = temp
        val sb = StringBuilder()
        if (milliseconds < 0) {
            sb.append("-")
        }
        if (hours > 0) {
            sb.append(String.format("%d:", hours))
            sb.append(String.format("%02d:", minutes))
        } else {
            sb.append(String.format("%d:", minutes))
        }
        sb.append(String.format("%02d", seconds))
        return sb.toString()
    }

    @JvmStatic
    fun getStyledText(str: String, style: Style): MutableText {
        val text = MutableText.of(PlainTextContent.of(str))
        text.setStyle(style)
        return text
    }

    @JvmStatic
    fun setItemName(stack: ItemStack, text: Text) {
        stack.set(DataComponentTypes.ITEM_NAME, text)
    }

    @JvmStatic
    fun setItemLore(stack: ItemStack, vararg loreLines: Text?) {
        stack.set(DataComponentTypes.LORE, LoreComponent(listOf(*loreLines)))
    }

    @JvmStatic
    fun humanReadableByteCountSI(bytes: Long): String {
        var bytes = bytes
        if (-1000 < bytes && bytes < 1000) {
            return "$bytes B"
        }
        val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
        while (bytes <= -999950 || bytes >= 999950) {
            bytes /= 1000
            ci.next()
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current())
    }

    class LimitedSizeInputStream(private val original: InputStream, private val maxSize: Long) : InputStream() {
        private var total: Long = 0

        @Throws(IOException::class)
        override fun read(): Int {
            val i = original.read()
            if (i >= 0) incrementCounter(1)
            return i
        }

        @Throws(IOException::class)
        override fun read(b: ByteArray): Int {
            return read(b, 0, b.size)
        }

        @Throws(IOException::class)
        override fun read(b: ByteArray, off: Int, len: Int): Int {
            val i = original.read(b, off, len)
            if (i >= 0) incrementCounter(i)
            return i
        }

        @Throws(IOException::class)
        private fun incrementCounter(size: Int) {
            total += size.toLong()
            if (total > maxSize) throw IOException("Input stream exceeded maximum size of $maxSize bytes")
        }
    }
}
