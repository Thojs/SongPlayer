package com.github.hhhzzzsss.songplayer.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimestampArgumentType implements ArgumentType<Long> {
    private static final Pattern timePattern = Pattern.compile("(?:(\\d+):)?(\\d+):(\\d+)");

    public static TimestampArgumentType timestamp() {
        return new TimestampArgumentType();
    }

    private boolean isAllowedCharacter(char character) {
        return Character.isDigit(character) || character == ':';
    }

    @Override
    public Long parse(StringReader reader) throws CommandSyntaxException {
        StringBuilder str = new StringBuilder();

        while (reader.canRead() && isAllowedCharacter(reader.peek())) {
            str.append(reader.read());
        }

        Matcher matcher = timePattern.matcher(str.toString());
        if (matcher.matches()) {
            long time = 0;
            String hourString = matcher.group(1);
            String minuteString = matcher.group(2);
            String secondString = matcher.group(3);
            if (hourString != null) {
                time += (long) Integer.parseInt(hourString) * 60 * 60 * 1000;
            }
            time += (long) Integer.parseInt(minuteString) * 60 * 1000;
            time += (long) (Double.parseDouble(secondString) * 1000.0);
            return time;
        } else {
            throw new CommandSyntaxException(new SimpleCommandExceptionType(Text.literal("a")), Text.literal("Not a valid time stamp"));
        }
    }
}