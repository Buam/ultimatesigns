package com.buam.ultimatesigns.variables;

public class VariableHelper {

    public static int parseInt(String in) {
        return Integer.parseInt(in);
    }

    public static double parseDouble(String in) {
        return Double.parseDouble(in);
    }

    public static VariableType parseType(Object in) {
        if(in instanceof Integer) return VariableType.INTEGER;
        if(in instanceof Double) return VariableType.DOUBLE;
        return VariableType.TEXT;
    }

    private static boolean isInteger(String in) {
        try {
            Integer.parseInt(in);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isDouble(String in) {
        try {
            Double.parseDouble(in);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
