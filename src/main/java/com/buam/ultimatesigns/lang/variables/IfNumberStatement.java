package com.buam.ultimatesigns.lang.variables;

import com.buam.ultimatesigns.USign;
import com.buam.ultimatesigns.lang.TypeManager;
import com.buam.ultimatesigns.lang.types.Boolean;
import com.buam.ultimatesigns.lang.exceptions.InvalidArgumentsException;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class IfNumberStatement extends Boolean {

    public boolean get(Object... args) {
        switch((String) args[0]) {
            case "<":
                return (int) args[1] < (int) args[2];
            case ">":
                return (int) args[1] > (int) args[2];
            case "<=":
                return (int) args[1] <= (int) args[2];
            case ">=":
                return (int) args[1] >= (int) args[2];
            case "==":
            case "=":
                return (int) args[1] == (int) args[2];
        }
        return false;
    }

    @Override
    public boolean isOfType(String s) {
        return false;
    }

    @Override
    public String a() {
        return "THIS IS AN EMPTY STRING!!!!";
    }

    /*
    Takes in <number><operator><number>
    for example 2<3 or <online>=5
     */
    public boolean get(String in, Player p, USign s) throws InvalidArgumentsException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        in = TypeManager.replaceNumbers(in);

        String op = "=";
        if(in.contains("<=")) {
            op = "<=";
        } else if(in.contains(">=")) {
            op = ">=";
        } else if(in.contains(">")) {
            op = ">";
        } else if(in.contains("<")) {
            op = "<";
        }
        String[] split = in.split(op);

        int[] ar = new int[2];

        for(int i = 0; i<2; i++) {
            if(TypeManager.isNumber(split[i].trim())) {
                ar[i] = TypeManager.getNumber(split[i].trim(), p, s);
            } else {
                try {
                    ar[i] = Integer.parseInt(split[i].trim());
                } catch (NumberFormatException e) {
                    throw new InvalidArgumentsException("Not a Number: '" + split[i].trim() + "'");
                }
            }
        }

        return get(op, ar[0], ar[1]);
    }

}
