package com.github.hhhzzzsss.songplayer;

import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private static Config config = null;

    private static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve(SongPlayer.MOD_ID + ".json");
    private static final Gson gson = new Gson();

    // Values
    public String creativeCommand = "gamemode creative";
    public String survivalCommand = "gamemode survival";

    public boolean showFakePlayer = true;

    public String stageType = "default";

    public boolean swing = false;
    public boolean rotate = false;

    public boolean doAnnouncement = false;
    public String announcementMessage = "&6Now playing: &3[name]";

    public static Config getConfig() {
        if (config == null) {
            config = new Config();
            try {
                if (Files.exists(CONFIG_FILE)) {
                    loadConfig();
                } else {
                    saveConfig();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return config;
    }

    public static void loadConfig() throws IOException {
        BufferedReader reader = Files.newBufferedReader(CONFIG_FILE);
        config = gson.fromJson(reader, Config.class);
        reader.close();
    }

    public static void saveConfig() throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(CONFIG_FILE);
        writer.write(gson.toJson(config));
        writer.close();
    }

    public static void saveConfigWithErrorHandling() {
        try {
            Config.saveConfig();
        } catch (IOException e) {
            if (SongPlayer.MC.world != null) {
                SongPlayer.addChatMessage("§cFailed to save config file");
            }
            e.printStackTrace();
        }
    }
}
