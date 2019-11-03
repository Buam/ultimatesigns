package com.buam.ultimatesigns.lang.variables;

import com.buam.ultimatesigns.lang.types.Number;
import org.bukkit.entity.Player;

public class PlayerLevel extends Number {
    @Override
    public int get(Object... args) {
        return ((Player) args[0]).getLevel();
    }

    @Override
    public String a() {
        return "[level]";
    }
}
