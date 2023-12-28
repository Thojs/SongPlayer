package com.github.hhhzzzsss.songplayer.conversion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class SongParserRegistry {
    public static final SongParserRegistry instance = new SongParserRegistry();
    private SongParserRegistry() {}

    private final List<SongParser> parsers = new ArrayList<>();
    private final List<String> supportedFileExtensions = new ArrayList<>();
    private List<SongParser> sortedMIMEParserList = new ArrayList<>();
    private List<SongParser> sortedFileParserList = new ArrayList<>();

    public void registerParsers(SongParser... parsers) {
        this.parsers.addAll(List.of(parsers));

        updateSortedFileParserList();
        updateSortedMIMEParserList();

        for (SongParser parser : parsers) {
            Collection<String> extensions = parser.getFileExtensions();
            if (extensions == null) continue;
            supportedFileExtensions.addAll(extensions);
        }
    }

    public List<SongParser> getMIMEParser(String mime) {
        for (SongParser converter : parsers) {
            Collection<String> mimeTypes = converter.getMIMETypes();
            if (mimeTypes == null) continue;
            if (mimeTypes.contains(mime)) return List.of(converter);
        }

        return sortedMIMEParserList;
    }

    public List<SongParser> getExtensionParser(String extension) {
        for (SongParser parser : parsers) {
            Collection<String> fileExtensions = parser.getFileExtensions();
            if (fileExtensions == null) continue;
            if (fileExtensions.contains(extension)) return List.of(parser);
        }

        return sortedFileParserList;
    }

    private void updateSortedFileParserList() {
        List<SongParser> sorted = new ArrayList<>(parsers);
        sorted.sort(Comparator.comparingInt(a -> {
            Collection<String> extensions = a.getFileExtensions();
            if (extensions == null) return 0;
            return extensions.size();
        }));
        sortedFileParserList = sorted;
    }

    private void updateSortedMIMEParserList() {
        List<SongParser> sorted = new ArrayList<>(parsers);
        sorted.sort(Comparator.comparingInt(a -> {
            Collection<String> mimeTypes = a.getMIMETypes();
            if (mimeTypes == null) return 0;
            return mimeTypes.size();
        }));
        sortedMIMEParserList = sorted;
    }

    public boolean supportsExtension(String extension) {
        return supportedFileExtensions.contains(extension);
    }
}
