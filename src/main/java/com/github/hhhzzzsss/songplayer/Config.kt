package com.github.hhhzzzsss.songplayer

import com.google.gson.Gson
import net.fabricmc.loader.api.FabricLoader
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class Config {
    // Values
    @JvmField
    var creativeCommand: String = "gamemode creative"
    @JvmField
    var survivalCommand: String = "gamemode survival"

    @JvmField
    var showFakePlayer: Boolean = true

    @JvmField
    var stageType: String = "default"

    @JvmField
    var swing: Boolean = false
    @JvmField
    var rotate: Boolean = false

    @JvmField
    var doAnnouncement: Boolean = false
    @JvmField
    var announcementMessage: String = "&6Now playing: &3[name]"

    companion object {
        private var config: Config? = null

        private val CONFIG_FILE: Path = FabricLoader.getInstance().configDir.resolve(SongPlayer.MOD_ID + ".json")
        private val gson = Gson()

        @JvmStatic
        fun getConfig(): Config {
            if (config != null) return config!!

            config = Config()
            try {
                if (Files.exists(CONFIG_FILE)) {
                    loadConfig()
                } else {
                    saveConfig()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return config!!
        }

        @Throws(IOException::class)
        fun loadConfig() {
            val reader = Files.newBufferedReader(CONFIG_FILE)
            config = gson.fromJson(reader, Config::class.java)
            reader.close()
        }

        @Throws(IOException::class)
        fun saveConfig() {
            val writer = Files.newBufferedWriter(CONFIG_FILE)
            writer.write(gson.toJson(config))
            writer.close()
        }

        fun saveConfigWithErrorHandling() {
            try {
                saveConfig()
            } catch (e: IOException) {
                if (SongPlayer.MC.world != null) {
                    SongPlayer.addChatMessage("§cFailed to save config file")
                }
                e.printStackTrace()
            }
        }
    }
}
