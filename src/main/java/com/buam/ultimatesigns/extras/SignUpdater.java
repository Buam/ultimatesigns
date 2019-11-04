package com.buam.ultimatesigns.extras;

import com.buam.ultimatesigns.SignHelper;
import com.buam.ultimatesigns.SignManager;
import com.buam.ultimatesigns.USign;
import com.buam.ultimatesigns.UltimateSigns;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class SignUpdater {

    /**
     * Updates all signs on the server
     */
    public static void updateAllSigns() {
        for(Player p : Bukkit.getServer().getOnlinePlayers()) {
            if(UltimateSigns.command.inEditMode.contains(p)) continue;
            handleSignsForPlayer(p);
        }
    }

    /**
     * Updates a sign. An update will be send to every online player
     * @param block The sign to update
     */
    public static void handleSignUpdate(Block block) {
        if(!(block.getState() instanceof Sign) && SignManager.i.isUltimateSign(block.getLocation())) {
            SignManager.i.removeSign(block.getLocation());
        }
        for(Player p : Bukkit.getServer().getOnlinePlayers()) {
            SignHelper.sendSignChange(p, block, SignHelper.translate((Sign) block.getState(), p));
        }
    }

    /**
     * Updates all signs for one player
     * @param p The player
     */
    public static void handleSignsForPlayer(Player p) {
        for(USign s : SignManager.i.getAllSigns()) {
            if(!(s.getBlock().getState() instanceof Sign) && SignManager.i.isUltimateSign(s.getLocation())) {
                SignManager.i.removeSign(s.getLocation());
                continue;
            }
            SignHelper.sendSignChange(p, s.getBlock(), SignHelper.translate((Sign) s.getBlock().getState(), p));
        }
    }

    /**
     * Sends the original sign text to a player before he starts to edit it
     * @param p The player
     * @param b The sign block
     */
    public static void sendOriginalSignText(Player p, Block b) {
        SignHelper.sendSignChange(p, b, ((Sign) b.getState()).getLines());
    }

    /**
     * Updates all signs in a world for a player
     * @param world The world
     * @param player The player
     */
    public static void handleSignsInWorldForPlayer(World world, Player player) {
        for(USign s : SignManager.i.getSignsInWorld(world)) {
            if(!(s.getBlock().getState() instanceof Sign) && SignManager.i.isUltimateSign(s.getLocation())) {
                SignManager.i.removeSign(s.getLocation());
                continue;
            }
            SignHelper.sendSignChange(player, s.getBlock(), SignHelper.translate((Sign) s.getBlock().getState(), player));
        }
    }

}
