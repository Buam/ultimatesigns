package com.buam.ultimatesigns;

import com.buam.ultimatesigns.config.Config;
import com.buam.ultimatesigns.lang.TypeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

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
        return ChatColor.translateAlternateColorCodes('&', in);
    }

    /**
     * Gets the block a Sign is attached to (if a sign is given)
     * @param block Must be a sign
     * @return The block the sign is attached to
     */
    public static Block getAttachedBlock(Block block) {
        if (block != null && block.getState() instanceof Sign) {
            String version = Bukkit.getVersion();
            // For newer versions, use the BlockData API
            if (version.contains("1.14") || version.contains("1.13")) {
                BlockData data = block.getBlockData();
                if (data instanceof Directional) {
                    Directional directional = (Directional) data;
                    return block.getRelative(directional.getFacing().getOppositeFace());
                }
                // For older versions, use Magic Numbers with MaterialData (should also work on newer versions)
            } else {
                org.bukkit.material.Sign s = (org.bukkit.material.Sign) block.getState().getData();
                return block.getRelative(s.getAttachedFace());
            }
        }
        return null;
    }
}
