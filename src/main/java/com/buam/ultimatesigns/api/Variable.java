package com.buam.ultimatesigns.api;

import com.buam.ultimatesigns.lang.TypeManager;

public abstract class Variable {

    public abstract String name();

    public abstract VariableType type();

    public abstract String get();

    public static void register(Variable v) {
        switch (v.type()) {
            case NUMBER:
                //TypeManager.addNumber(v);
                break;
            case TEXT:
                //TypeManager.addText(v);
                break;
        }
    }
}
