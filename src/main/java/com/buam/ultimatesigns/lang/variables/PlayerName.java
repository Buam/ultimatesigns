package com.buam.ultimatesigns.lang.variables;

import com.buam.ultimatesigns.lang.types.Text;
import org.bukkit.entity.Player;

public class PlayerName extends Text {
    @Override
    public String get(Object... args) {
        return ((Player) args[0]).getName();
    }

    @Override
    public String a() {
        return "[player]";
    }
}
