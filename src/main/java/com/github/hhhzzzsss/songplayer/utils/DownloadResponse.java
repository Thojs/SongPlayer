package com.github.hhhzzzsss.songplayer.utils;

public class DownloadResponse {
    public final byte[] content;
    public final String mimeType;

    public DownloadResponse(byte[] content, String mimeType) {
        this.content = content;
        this.mimeType = mimeType;
    }
}
