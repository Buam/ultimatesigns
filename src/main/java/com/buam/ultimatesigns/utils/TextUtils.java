package com.buam.ultimatesigns.utils;

import com.buam.ultimatesigns.SignManager;
import com.buam.ultimatesigns.config.Config;
import com.buam.ultimatesigns.lang.TypeManager;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class TextUtils {

    /**
     * Translate a string with all variables
     * @param sign The sign
     * @param p The player
     * @return A translated string
     */
    public static String[] translate(Sign sign, Player p) {
        if(!SignManager.i.isUltimateSign(sign.getLocation())) SignManager.i.addSign(sign.getLocation(), p.getUniqueId());
        try {
            String[] lines = new String[4];
            for (int i = 0; i < 4; i++) {
                // First translate variables, then translate colors
                lines[i] = TypeManager.translate(sign.getLine(i), p, SignManager.i.signAt(sign.getLocation()));
                lines[i] = translateColors(lines[i]);
            }
            return lines;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return sign.getLines();
    }

    /**
     * Translate basic color codes with the color char specified in the config
     * @param in The string to translate
     * @return A translated string
     */
    public static String translateColors(String in) {
        return ChatColor.translateAlternateColorCodes(Config.i.colorchar, in);
    }

}
