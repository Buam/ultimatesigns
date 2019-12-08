package com.buam.ultimatesigns.lang;

import com.buam.ultimatesigns.USign;
import com.buam.ultimatesigns.UltimateSigns;
import com.buam.ultimatesigns.config.Aliases;
import com.buam.ultimatesigns.config.Config;
import com.buam.ultimatesigns.lang.exceptions.InvalidArgumentsException;
import com.buam.ultimatesigns.lang.variables.*;
import com.buam.ultimatesigns.lang.types.Number;
import com.buam.ultimatesigns.lang.types.Text;
import com.buam.ultimatesigns.variables.Variable;
import com.buam.ultimatesigns.variables.VariableType;
import com.google.common.collect.Sets;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unchecked")
public class TypeManager {

    private static final HashSet<Class<? extends Number>> numberTypes = Sets.newHashSet(PlayersOnline.class, SignPlayerLastUsed.class, SignPlayerUses.class, PlayerLevel.class);
    private static final HashSet<Class<? extends Text>> textTypes = Sets.newHashSet(PlayerName.class, PlayerDisplayName.class, PlayerUUID.class, PlayerWorldName.class);
    private static final HashSet<Class<? extends Number>> requiresVault = Sets.newHashSet(PlayerBalance.class);

    public static boolean isNumber(String s) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        for(Class<?> t : numberTypes) {
            if(((Number) t.getDeclaredConstructor().newInstance()).isOfType(s)) {
                return true;
            }
        }
        if(UltimateSigns.economy != null) {
            for(Class<?> t : requiresVault) {
                if(((Number) t.getDeclaredConstructor().newInstance()).isOfType(s)) {
                    return true;
                }
            }
        }
        for(Variable v : Config.i.getVariables()) {
            if(s.contains("[" + v.getName() + "]") && v.getType() == VariableType.INTEGER) {
                return true;
            }
        }

        return false;
    }

    public static boolean isUnique(String s, Set<Variable> other) {
        s = "[" + s +  "]";
        try {
            for (Class<?> t : textTypes) {
                Text instance = (Text) t.getDeclaredConstructor().newInstance();
                if (instance.a().equals(s)) {
                    return false;
                }
            }

            for (Class<?> t : numberTypes) {
                Number instance = (Number) t.getDeclaredConstructor().newInstance();
                if (instance.a().equals(s)) {
                    return false;
                }
            }

            if (UltimateSigns.economy != null) {
                for (Class<?> t : requiresVault) {
                    Number instance = (Number) t.getDeclaredConstructor().newInstance();
                    if (instance.a().equals(s)) {
                        return false;
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        for(Variable v : other) {
            if(v.getName().equals(s)) {
                return false;
            }
        }

        return true;
    }

    public static boolean isText(String s) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        for(Class<?> t : textTypes) {
            if(((Text) t.getDeclaredConstructor().newInstance()).isOfType(s)) {
                return true;
            }
        }
        for(Variable v : Config.i.getVariables()) {
            if(s.contains("[" + v.getName() + "]") && v.getType() == VariableType.TEXT) {
                return true;
            }
        }
        return false;
    }

    public static int getNumber(String s, Player p, USign sign) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        for(Class<?> t : numberTypes) {
            Number n = (Number) t.getDeclaredConstructor().newInstance();
            if(n.isOfType(s)) {
                return n.get(p, sign);
            }
        }

        for(Variable v : Config.i.getVariables()) {
            if(isNumber(s)) {
                return v.getIntValue();
            }
        }
        return 0;
    }

    public static String getText(String s, Player p) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        for(Class<?> c : textTypes) {
            Text t = (Text) c.getDeclaredConstructor().newInstance();
            if(t.isOfType(s)) {
                return t.get(p);
            }
        }
        for(Variable v : Config.i.getVariables()) {
            if(isText(s)) {
                return v.getTextValue();
            }
        }
        return "";
    }

    public static boolean getIf(String cmd, Player p, USign s) throws InvalidArgumentsException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        return new IfNumberStatement().get(cmd, p, s);
    }

    public static String translate(String s, Player p, USign sign) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        for(Class<?> t : numberTypes) {
            Number n = (Number) t.getDeclaredConstructor().newInstance();
            s = s.replace(n.a(), Integer.toString(n.get(p, sign)));
            for(String alias : Aliases.i.getAliases(n.a())) {
                s = s.replace("[" + alias + "]", Integer.toString(n.get(p, sign)));
            }
        }
        if(UltimateSigns.economy != null) {
            for(Class<?> t : requiresVault) {
                Number n = (Number) t.getDeclaredConstructor().newInstance();
                s = s.replace(n.a(), Integer.toString(n.get(p, sign)));
                for(String alias : Aliases.i.getAliases(n.a())) {
                    s = s.replace("[" + alias + "]", Integer.toString(n.get(p, sign)));
                }
            }
        }
        for(Class<?> t : textTypes) {
            Text n = (Text) t.getDeclaredConstructor().newInstance();
            s = s.replace(n.a(), n.get(p, sign));
            for(String alias : Aliases.i.getAliases(n.a())) {
                s = s.replace("[" + alias + "]", n.get(p, sign));
            }
        }
        for(Variable v : Config.i.getVariables()) {
            s = s.replace("[" + v.getName() + "]", v.getType() == VariableType.INTEGER ? Integer.toString(v.getIntValue()) : v.getTextValue());
            for(String alias : Aliases.i.getAliases(v.getName())) {
                s = s.replace("[" + alias + "]", v.getType() == VariableType.INTEGER ? Integer.toString(v.getIntValue()) : v.getTextValue());
            }
        }
        return s;
    }

    public static String replaceNumbers(String in) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        char[] chars = in.toCharArray();

        for(Class<?> t : numberTypes) {
            String a = ((Number) t.getDeclaredConstructor().newInstance()).a();

            if(in.contains(a)) {
                int start = in.indexOf(a);
                for(int i = start; i < start + a.length(); i++) {
                    if(chars[i] == '<') {
                        chars[i] = '[';
                    } else if(chars[i] == '>') {
                        chars[i] = ']';
                    }
                }
            }
        }

        return new String(chars);

    }
}
