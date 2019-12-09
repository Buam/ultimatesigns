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

    public Messages(final JavaPlugin plugin) {
        i = this;
        createConfig(plugin);
    }

    public String s(final String key, final Object... args) {
        String s = config.getString(key);

        for(int i = 0; i < args.length; i++) {
            s = s.replace("%" + (i + 1), args[i].toString());
        }

        return SignHelper.translateColors(s);
    }

    private void createConfig(final JavaPlugin plugin) {
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
