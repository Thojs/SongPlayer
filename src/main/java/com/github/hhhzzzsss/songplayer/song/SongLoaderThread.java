package com.github.hhhzzzsss.songplayer.song;

import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.conversion.SongParser;
import com.github.hhhzzzsss.songplayer.conversion.SongParserRegistry;
import com.github.hhhzzzsss.songplayer.utils.DownloadResponse;
import com.github.hhhzzzsss.songplayer.utils.DownloadUtils;
import com.github.hhhzzzsss.songplayer.utils.Util;
import org.apache.commons.compress.utils.FileNameUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SongLoaderThread extends Thread {
    private Path songPath;
	private URL songUrl;

	public Exception exception;
	public Song song;
	public String filename;

	protected SongLoaderThread() {}

	public SongLoaderThread(String location) throws IOException {
        if (location.startsWith("http://") || location.startsWith("https://")) {
			songUrl = new URL(location);
		} else {
			songPath = getSongFile(location);
		}
	}

	public SongLoaderThread(Path file) {
		this.songPath = file;
	}
	
	public void run() {
		try {
			byte[] content;
			List<SongParser> parsers;

			// Get content from file/url & retrieve parsers.
			if (songUrl != null) {
				DownloadResponse res = DownloadUtils.downloadToByteArray(songUrl, 10*1024*1024);
                content = res.content;
				filename = Paths.get(songUrl.toURI().getPath()).getFileName().toString();

				parsers = SongParserRegistry.instance.getMIMEParser(res.mimeType);
			} else {
				content = Files.readAllBytes(songPath);
				filename = songPath.getFileName().toString();

				String extension = FileNameUtils.getExtension(songPath);
				parsers = SongParserRegistry.instance.getExtensionParser(extension);
			}

			// Parse content
			for (SongParser converter : parsers) {
				song = converter.parse(content, filename);
				if (song != null) break;
			}

			if (song == null) throw new IOException("Invalid song format");
		} catch (Exception e) {
			exception = e;
		}
	}

	private Path getSongFile(String name) throws IOException {
		return Util.resolveWithIOException(SongPlayer.SONG_DIR, name);
	}
}