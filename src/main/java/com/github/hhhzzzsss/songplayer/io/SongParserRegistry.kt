package com.github.hhhzzzsss.songplayer.io

object SongParserRegistry {
    private val parsers = mutableListOf<SongParser>()
    private val supportedFileExtensions = mutableListOf<String>()
    private var sortedMIMEParserList = listOf<SongParser>()
    private var sortedFileParserList = listOf<SongParser>()

    fun registerParsers(vararg parsers: SongParser) {
        this.parsers.addAll(listOf(*parsers))

        updateSortedFileParserList()
        updateSortedMIMEParserList()

        for (parser in parsers) {
            val extensions = parser.fileExtensions ?: continue
            supportedFileExtensions.addAll(extensions)
        }
    }

    fun getMIMEParser(mime: String): List<SongParser> {
        for (parser in parsers) {
            val mimeTypes = parser.mimeTypes ?: continue
            if (mimeTypes.contains(mime)) return listOf(parser)
        }

        return sortedMIMEParserList
    }

    fun getExtensionParser(extension: String): List<SongParser> {
        for (parser in parsers) {
            val fileExtensions = parser.fileExtensions ?: continue
            if (fileExtensions.contains(extension)) return listOf(parser)
        }

        return sortedFileParserList
    }

    private fun updateSortedFileParserList() {
        val sorted = parsers.toSortedSet(Comparator.comparingInt { a ->
            val fileExtensions = a.fileExtensions ?: return@comparingInt 0
            fileExtensions.size
        }).toList()
        sortedFileParserList = sorted
    }

    private fun updateSortedMIMEParserList() {
        val sorted = parsers.toSortedSet(Comparator.comparingInt { a ->
            val mimeTypes = a.mimeTypes ?: return@comparingInt 0
            mimeTypes.size
        }).toList()
        sortedMIMEParserList = sorted
    }

    fun supportsExtension(extension: String): Boolean {
        return supportedFileExtensions.contains(extension)
    }
}
