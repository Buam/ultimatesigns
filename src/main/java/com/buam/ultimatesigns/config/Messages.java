package com.buam.ultimatesigns.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Messages {

    public static Messages i;

    private File configFile;
    private FileConfiguration config;

    public Messages(JavaPlugin plugin) {
        i = this;
        createConfig(plugin);
    }

    public String s(String key) {
        return config.getString(key);
    }

    private void createConfig(JavaPlugin plugin) {
        configFile = new File(plugin.getDataFolder(), "messages.yml");
        if(!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource("messages.yml", false);
        }

        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch(IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

}
