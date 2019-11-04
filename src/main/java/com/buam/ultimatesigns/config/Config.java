package com.buam.ultimatesigns.config;

import com.buam.ultimatesigns.SignHelper;
import com.buam.ultimatesigns.UltimateSigns;
import com.buam.ultimatesigns.lang.TypeManager;
import com.buam.ultimatesigns.variables.Variable;
import com.buam.ultimatesigns.variables.VariableHelper;
import com.buam.ultimatesigns.variables.VariableType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Set;

public class Config {
    /**
     * Static reference to the configuration
     */
    public static Config i;

    /**
     * FileConfiguration object which can be used to read and write values
     */
    private FileConfiguration config;

    /**
     * A set of all custom variables defined
     */
    private Set<Variable> variables;

    public Config(FileConfiguration config) {
        i = this;
        this.config = config;
        variables = getVariables();
        config.options().copyDefaults(true);
    }

    /**
     * Get all variables from the config file
     * @return A set of all variables
     */
    public Set<Variable> getVariables() {
        if(variables != null) return variables;
        if(!config.getKeys(false).contains("variables")) return new HashSet<>();
        return getVariables(config.getConfigurationSection("variables"));
    }

    /**
     * Actually parses the variables from the configuration sections
     * @param variableSection The 'variable' configuration sections
     * @return A set of all variables
     */
    private Set<Variable> getVariables(ConfigurationSection variableSection) {
        Set<Variable> out = new HashSet<>();
        for(String s : variableSection.getKeys(false)) {
            if(TypeManager.isUnique(s, out)) {
                out.add(getVariable(s, variableSection.getConfigurationSection(s).get("value")));
            } else {
                System.out.println(ChatColor.RED + "Variable '" + s + "' can't be used. It already exists");
            }
        }
        return out;
    }

    public Variable getVariable(String name, Object value) {
        return new Variable(name, (value instanceof Integer ? VariableType.INTEGER : (value instanceof Double ? VariableType.DOUBLE : VariableType.TEXT)), value);
    }

    /**
     * Translates a string from the configuration with colors
     * @param key The key to get
     * @return The translated string
     */
    public String s(String key) {
        return SignHelper.translateColors(config.getString(key));
    }

    /**
     * Gets a boolean from the configuration
     * @param key The key to get
     * @return The boolean
     */
    public boolean b(String key) {
        return config.getBoolean(key);
    }

    /**
     * Gets a long from the configuration
     * @param key The key to get
     * @return The long (integer, number)
     */
    public long i(String key) {
        return config.getLong(key);
    }
}
