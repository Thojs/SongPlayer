package com.github.hhhzzzsss.songplayer.conversion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class SongParserRegistry {
    public static final SongParserRegistry instance = new SongParserRegistry();
    private SongParserRegistry() {}

    private final List<SongParser> converters = new ArrayList<>();
    private List<SongParser> sortedMIMEConverterList = new ArrayList<>();
    private List<SongParser> sortedFileConverterList = new ArrayList<>();

    public void registerConverters(SongParser... converters) {
        this.converters.addAll(List.of(converters));
        updateSortedFileConverterList();
        updateSortedMIMEConverterList();
    }

    public List<SongParser> getMIMEConverter(String mime) {
        for (SongParser converter : converters) {
            Collection<String> mimeTypes = converter.getMIMETypes();
            if (mimeTypes == null) continue;
            if (mimeTypes.contains(mime)) return List.of(converter);
        }

        return sortedMIMEConverterList;
    }

    public List<SongParser> getExtensionConverter(String extension) {
        for (SongParser converter : converters) {
            Collection<String> fileExtensions = converter.getFileExtensions();
            if (fileExtensions == null) continue;
            if (fileExtensions.contains(extension)) return List.of(converter);
        }

        return sortedFileConverterList;
    }

    private void updateSortedFileConverterList() {
        List<SongParser> sorted = new ArrayList<>(converters);
        sorted.sort(Comparator.comparingInt(a -> {
            Collection<String> extensions = a.getFileExtensions();
            if (extensions == null) return 0;
            return extensions.size();
        }));
        sortedFileConverterList = sorted;
    }

    private void updateSortedMIMEConverterList() {
        List<SongParser> sorted = new ArrayList<>(converters);
        sorted.sort(Comparator.comparingInt(a -> {
            Collection<String> mimeTypes = a.getMIMETypes();
            if (mimeTypes == null) return 0;
            return mimeTypes.size();
        }));
        sortedMIMEConverterList = sorted;
    }
}
