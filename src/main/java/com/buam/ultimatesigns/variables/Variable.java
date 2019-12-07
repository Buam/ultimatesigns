package com.buam.ultimatesigns.variables;

public class Variable {

    private String name;
    private VariableType type;
    private Object value;

    public Variable(String name, VariableType type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public VariableType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public int getIntValue() {
        return (int) value;
    }

    public double getDoubleValue() {
        return (double) value;
    }

    public String getTextValue() {
        return (String) value;
    }
}
