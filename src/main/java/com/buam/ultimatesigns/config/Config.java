package com.buam.ultimatesigns.config;

import com.buam.ultimatesigns.SignHelper;
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
    public static Config i;

    private FileConfiguration config;

    private Set<Variable> variables;

    public Config(FileConfiguration config) {
        i = this;
        this.config = config;
        variables = getVariables();
    }

    public Set<Variable> getVariables() {
        if(variables != null) return variables;
        if(!config.getKeys(false).contains("variables")) return new HashSet<>();
        return getVariables(config.getConfigurationSection("variables"));
    }

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

    public String s(String key) {
        return SignHelper.translateColors(config.getString(key));
    }

    public boolean b(String key) {
        return config.getBoolean(key);
    }

    public long i(String key) {
        return config.getLong(key);
    }
}
