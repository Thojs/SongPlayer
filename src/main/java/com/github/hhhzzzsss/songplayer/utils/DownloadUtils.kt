package com.github.hhhzzzsss.songplayer.utils

import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URL
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object DownloadUtils {
    @JvmStatic
    @Throws(IOException::class, KeyManagementException::class, NoSuchAlgorithmException::class)
    fun downloadToByteArray(url: URL, maxSize: Int): DownloadResponse? {
        val ctx = SSLContext.getInstance("TLS")
        ctx.init(arrayOfNulls(0), arrayOf<TrustManager>(DefaultTrustManager()), SecureRandom())
        SSLContext.setDefault(ctx)

        val conn = url.openConnection()
        conn.connectTimeout = 5000
        conn.readTimeout = 10000
        conn.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:86.0) Gecko/20100101 Firefox/86.0"
        )

        val contentType = conn.getHeaderField("Content-Type")

        BufferedInputStream(conn.getInputStream()).use { downloadStream ->
            val byteArrayStream = ByteArrayOutputStream()
            val buf = ByteArray(1024)
            var n: Int
            var tot = 0
            while ((downloadStream.read(buf).also { n = it }) > 0) {
                byteArrayStream.write(buf, 0, n)
                tot += n
                if (tot > maxSize) {
                    throw IOException("File is too large")
                }
                if (Thread.interrupted()) {
                    return null
                }
            }
            return DownloadResponse(byteArrayStream.toByteArray(), contentType)
        }
    }

    private class DefaultTrustManager : X509TrustManager {
        override fun checkClientTrusted(arg0: Array<X509Certificate>, arg1: String) {}

        override fun checkServerTrusted(arg0: Array<X509Certificate>, arg1: String) {}

        override fun getAcceptedIssuers(): Array<X509Certificate>? {
            return null
        }
    }
}
