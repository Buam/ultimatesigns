package com.buam.ultimatesigns.utils;

import com.buam.ultimatesigns.SharedConstants;
import com.buam.ultimatesigns.UltimateSigns;
import com.buam.ultimatesigns.config.Config;
import com.buam.ultimatesigns.extras.SignUpdater;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
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
        if(!SharedConstants.isSign(b.getType())) return;

        if(!Config.i.b(SharedConstants.ENABLE_UPDATE_PERMISSION) || p.hasPermission(SharedConstants.UPDATE_PERMISSION)) {
            p.sendSignChange(b.getLocation(), lines);
        }
    }

    /**
     * @param block A sign
     * @return The block the sign is attached to
     */
    public static Block getAttachedBlock(Block block) {
        if(block != null && block.getState() instanceof Sign) {

            // Use the BlockData API for new Versions
            if(UltimateSigns.version >= 113) {
                BlockData data = block.getBlockData();

                if(data instanceof Directional) {
                    Directional directional = (Directional) data;
                    return block.getRelative(directional.getFacing().getOppositeFace());
                }
            }

            return getAttachedBlockLegacy(block);
        }
        return null;
    }

    // Get attached Block with magic numbers
    @SuppressWarnings("deprecation")
    private static Block getAttachedBlockLegacy(Block block) {
        org.bukkit.material.Sign sign = (org.bukkit.material.Sign) block.getState().getData();
        return block.getRelative(sign.getAttachedFace());
    }

    /**
     * Helper method to open the sign editor using Protocol
     * @param s The sign
     * @param p The player
     */
    public static void openSignEditor(Sign s, Player p) {
        UltimateSigns.signEditor.open(p, s.getLocation(), null, (player, lines) -> {
            for(int i = 0; i<lines.length; i++) {
                s.setLine(i, lines[i]);
                s.update(true);
                Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateSigns.i, () -> SignUpdater.handleSignUpdate(s.getBlock()), 5);
            }
        });
    }

}
