package com.buam.ultimatesigns.config;

import com.buam.ultimatesigns.lang.TypeManager;
import com.buam.ultimatesigns.utils.TextUtils;
import com.buam.ultimatesigns.variables.Variable;
import com.buam.ultimatesigns.variables.VariableType;
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
    private final FileConfiguration config;

    /**
     * A set of all custom variables defined
     */
    private final Set<Variable> variables;

    public final char colorchar;

    public Config(final FileConfiguration config) {
        i = this;
        this.config = config;
        variables = getVariables();
        config.options().copyDefaults(true);
        colorchar = config.getString("color-char", "&").charAt(0);
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
    private Set<Variable> getVariables(final ConfigurationSection variableSection) {
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

    public Variable getVariable(final String name, final Object value) {
        return new Variable(name, (value instanceof Integer ? VariableType.INTEGER : (value instanceof Double ? VariableType.DOUBLE : VariableType.TEXT)), value);
    }

    /**
     * Translates a string from the configuration with colors.
     * @param key The key to get
     * @return The translated string
     */
    public String s(final String key) {
        return TextUtils.translateColors(config.getString(key));
    }

    public String s(final String key, final String standard) {
        return TextUtils.translateColors(config.getString(key, standard));
    }

    /**
     * Gets a boolean from the configuration
     * @param key The key to get
     * @return The boolean
     */
    public boolean b(final String key) {
        return config.getBoolean(key);
    }

    /**
     * Gets a long from the configuration
     * @param key The key to get
     * @return The long (integer, number)
     */
    public long i(final String key) {
        return config.getLong(key);
    }
}
