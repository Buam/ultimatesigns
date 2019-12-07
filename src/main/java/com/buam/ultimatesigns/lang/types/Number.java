package com.buam.ultimatesigns.lang.types;

import com.buam.ultimatesigns.config.Aliases;

public abstract class Number {

    public abstract int get(Object... args);

    public boolean isOfType(String s) {
        // Check for aliases first
        for(String alias : Aliases.i.getAliases(a())) {
            if(s.contains(alias)) return true;
        }
        // Return if it contains the actual variable name
        return s.contains(a());
    }

    public abstract String a();

}
