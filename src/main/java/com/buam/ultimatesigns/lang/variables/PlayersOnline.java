package com.buam.ultimatesigns.lang.variables;

import com.buam.ultimatesigns.lang.types.Number;
import org.bukkit.Bukkit;

public class PlayersOnline extends Number {

    @Override
    public int get(Object... args) {
        return Bukkit.getServer().getOnlinePlayers().size();
    }

    @Override
    public String a() {
        return "[online]";
    }

}
