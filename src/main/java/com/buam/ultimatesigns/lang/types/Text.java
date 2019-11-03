package com.buam.ultimatesigns.lang.types;

public abstract class Text {

    public abstract String get(Object... args);

    public boolean isOfType(String s) {
        return s.contains(a());
    }

    public abstract String a();

}
