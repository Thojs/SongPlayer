package com.github.hhhzzzsss.songplayer.commands

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.text.Text
import java.util.regex.Pattern

class TimestampArgumentType : ArgumentType<Long> {
    private fun isAllowedCharacter(character: Char): Boolean {
        return Character.isDigit(character) || character == ':'
    }

    @Throws(CommandSyntaxException::class)
    override fun parse(reader: StringReader): Long {
        val str = StringBuilder()

        while (reader.canRead() && isAllowedCharacter(reader.peek())) {
            str.append(reader.read())
        }

        val matcher = timePattern.matcher(str.toString())
        if (matcher.matches()) {
            var time: Long = 0
            val hourString = matcher.group(1)
            val minuteString = matcher.group(2)
            val secondString = matcher.group(3)
            if (hourString != null) {
                time += hourString.toInt().toLong() * 60 * 60 * 1000
            }
            time += minuteString.toInt().toLong() * 60 * 1000
            time += (secondString.toDouble() * 1000.0).toLong()
            return time
        }

        throw CommandSyntaxException(
            SimpleCommandExceptionType(Text.literal("Invalid timestamp provided")),
            Text.literal("Not a valid time stamp")
        )
    }

    companion object {
        private val timePattern: Pattern = Pattern.compile("(?:(\\d+):)?(\\d+):(\\d+)")

        @JvmStatic
        fun timestamp(): TimestampArgumentType {
            return TimestampArgumentType()
        }
    }
}