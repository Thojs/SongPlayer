package com.github.hhhzzzsss.songplayer.song;

import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.conversion.SongParser;
import com.github.hhhzzzsss.songplayer.conversion.ConverterRegistry;
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
			byte[] bytes;
			List<SongParser> converters;

			if (songUrl != null) {
				DownloadResponse res = DownloadUtils.downloadToByteArray(songUrl, 10*1024*1024);
                bytes = res.content;
				filename = Paths.get(songUrl.toURI().getPath()).getFileName().toString();

				converters = ConverterRegistry.instance.getMIMEConverter(res.mimeType);
			} else {
				bytes = Files.readAllBytes(songPath);
				filename = songPath.getFileName().toString();

				String extension = FileNameUtils.getExtension(songPath);
				converters = ConverterRegistry.instance.getExtensionConverter(extension);
			}

			// Parse
			for (SongParser converter : converters) {
				song = converter.parse(bytes, filename);
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