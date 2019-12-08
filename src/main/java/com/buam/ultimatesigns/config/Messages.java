package com.buam.ultimatesigns.config;

import com.buam.ultimatesigns.SignHelper;
import org.bukkit.ChatColor;
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
        return SignHelper.translateColors(config.getString(key));
    }

    private void createConfig(JavaPlugin plugin) {
        configFile = new File(plugin.getDataFolder(), "messages.yml");
        if(!configFile.exists()) {
            if(!configFile.getParentFile().mkdirs()) System.out.println(ChatColor.RED + "[UltimateSigns] failed to create messages.yml");
            plugin.saveResource("messages.yml", false);
        }

        config = new YamlConfiguration();
        try {
            config.load(configFile);
            addNewDefaults();
        } catch(IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void addNewDefaults() throws IOException {
        if(!config.isSet("no-permission-add-cmd-message")) config.addDefault("no-permission-add-cmd-message", "&cYou don't have permission to add that command");
        config.options().copyDefaults(true);
        config.save(configFile);
    }

}
