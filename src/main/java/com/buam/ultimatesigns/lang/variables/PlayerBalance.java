package com.buam.ultimatesigns.lang.variables;

import com.buam.ultimatesigns.UltimateSigns;
import com.buam.ultimatesigns.lang.types.Number;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerBalance extends Number {
    @Override
    public int get(Object... args) {
        return (int) UltimateSigns.economy.getBalance(Bukkit.getOfflinePlayer(((Player)args[0]).getUniqueId()));
    }

    @Override
    public String a() {
        return UltimateSigns.economy != null ? "[balance]" : "THIS IS AN EMPTY STRING!!!!!!!";
    }
}
