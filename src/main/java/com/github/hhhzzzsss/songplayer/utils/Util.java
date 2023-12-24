package com.github.hhhzzzsss.songplayer.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    public static void createDirectoriesSilently(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException ignored) {}
    }

    public static Path resolveWithIOException(Path path, String other) throws IOException {
        try {
            return path.resolve(other);
        }
        catch (InvalidPathException e) {
            throw new IOException(e.getMessage());
        }
    }

    public static class LimitedSizeInputStream extends InputStream {
        private final InputStream original;
        private final long maxSize;
        private long total;

        public LimitedSizeInputStream(InputStream original, long maxSize) {
            this.original = original;
            this.maxSize = maxSize;
        }

        @Override
        public int read() throws IOException {
            int i = original.read();
            if (i>=0) incrementCounter(1);
            return i;
        }

        @Override
        public int read(byte[] b) throws IOException {
            return read(b, 0, b.length);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int i = original.read(b, off, len);
            if (i>=0) incrementCounter(i);
            return i;
        }

        private void incrementCounter(int size) throws IOException {
            total += size;
            if (total>maxSize) throw new IOException("Input stream exceeded maximum size of " + maxSize + " bytes");
        }
    }

    public static String formatTime(long milliseconds) {
        long temp = Math.abs(milliseconds);
        temp /= 1000;
        long seconds = temp % 60;
        temp /= 60;
        long minutes = temp % 60;
        temp /= 60;
        long hours = temp;
        StringBuilder sb = new StringBuilder();
        if (milliseconds < 0) {
            sb.append("-");
        }
        if (hours > 0) {
            sb.append(String.format("%d:", hours));
            sb.append(String.format("%02d:", minutes));
        } else {
            sb.append(String.format("%d:", minutes));
        }
        sb.append(String.format("%02d", seconds));
        return sb.toString();
    }

    public static MutableText getStyledText(String str, Style style) {
        MutableText text = MutableText.of(PlainTextContent.of(str));
        text.setStyle(style);
        return text;
    }

    public static void setItemName(ItemStack stack, Text text) {
        stack.getOrCreateSubNbt(ItemStack.DISPLAY_KEY).putString(ItemStack.NAME_KEY, Text.Serialization.toJsonString(text));
    }

    public static void setItemLore(ItemStack stack, Text... loreLines) {
        NbtList lore = new NbtList();
        for (Text line : loreLines) {
            lore.add(NbtString.of(Text.Serialization.toJsonString(line)));
        }
        stack.getOrCreateSubNbt(ItemStack.DISPLAY_KEY).put(ItemStack.LORE_KEY, lore);
    }
}
