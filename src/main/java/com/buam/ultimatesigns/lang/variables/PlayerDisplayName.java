package com.buam.ultimatesigns.lang.variables;

import com.buam.ultimatesigns.lang.types.Text;
import org.bukkit.entity.Player;

public class PlayerDisplayName extends Text {
    @Override
    public String get(Object... args) {
        return ((Player) args[0]).getDisplayName();
    }

    @Override
    public String a() {
        return "[displayname]";
    }
}
