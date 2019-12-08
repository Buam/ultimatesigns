package com.buam.ultimatesigns;

import com.buam.ultimatesigns.config.Config;
import com.buam.ultimatesigns.lang.TypeManager;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class SignHelper {

    /**
     * Sends a sign change to a player
     * This will not change the sign text!
     * @param p The player
     * @param b The sign
     * @param lines The text to update
     */
    public static void sendSignChange(Player p, Block b, String[] lines) {
        if(!Config.i.b(Constants.ENABLE_UPDATE_PERMISSION) || p.hasPermission(Constants.UPDATE_PERMISSION)) {
            p.sendSignChange(b.getLocation(), lines);
        }
    }

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
     * Translate basic color codes with '&'
     * @param in The string to translate
     * @return A translated string
     */
    public static String translateColors(String in) {
        return ChatColor.translateAlternateColorCodes(Config.i.colorchar, in);
    }
}
