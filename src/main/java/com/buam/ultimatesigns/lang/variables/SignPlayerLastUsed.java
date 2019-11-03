package com.buam.ultimatesigns.lang.variables;

import com.buam.ultimatesigns.SignManager;
import com.buam.ultimatesigns.USign;
import com.buam.ultimatesigns.lang.types.Number;
import org.bukkit.entity.Player;

public class SignPlayerLastUsed extends Number {

    @Override
    public int get(Object... args) {
        return (int) SignManager.i.getLastUsed((Player) args[0], (USign) args[1]).difference();
    }

    @Override
    public String a() {
        return "[lastused]";
    }
}
