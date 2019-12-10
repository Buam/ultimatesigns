package com.buam.ultimatesigns.utils;

import com.buam.ultimatesigns.Constants;
import com.buam.ultimatesigns.config.Config;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class SignUtils {

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

}
