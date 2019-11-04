package com.buam.ultimatesigns.lang.types;

public abstract class Boolean {

    public abstract boolean get(Object... args);

    public boolean isOfType(String s) {
        return s.equals(a());
    }

    public abstract String a();

}
