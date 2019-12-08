package com.buam.ultimatesigns.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Aliases {

    public static Aliases i;

    private FileConfiguration config;

    public Aliases(final JavaPlugin plugin) {
        i = this;
        createConfig(plugin);
    }

    /**
     * Create the configuration file and save it if it doesn't exist.
     * @param plugin The plugin
     */
    private void createConfig(final JavaPlugin plugin) {
        File configFile = new File(plugin.getDataFolder(), "aliases.yml");
        if(!configFile.exists()) {
            if(!configFile.getParentFile().mkdirs())
                System.out.println(ChatColor.RED + "[UltimateSigns] failed to create aliases.yml");
            plugin.saveResource("aliases.yml", false);
        }

        try {
            config = new YamlConfiguration();
            config.load(configFile);
        } catch(IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all aliases for a specific variable
     * @param original The variable to get the aliases of
     * @return A set of Strings which represent the aliases
     */
    public Set<String> getAliases(final String original) {
        Set<String> out = new HashSet<>();

        if(config == null) {
            return out;
        }

        ConfigurationSection mainSection = config.getConfigurationSection("aliases");

        for(String key : mainSection.getKeys(false)) {
            if(key.equals(original.replace("[", "").replace("]", "").trim())) {
                out.addAll(mainSection.getStringList(key));
            }
        }

        return out;
    }

}
