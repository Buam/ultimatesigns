package com.buam.ultimatesigns.extras;

import com.buam.ultimatesigns.SignHelper;
import com.buam.ultimatesigns.SignManager;
import com.buam.ultimatesigns.USign;
import com.buam.ultimatesigns.UltimateSigns;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

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
        Set<Location> toRemove = new HashSet<>();
        for(USign s : SignManager.i.getAllSigns()) {
            if(!(s.getBlock().getState() instanceof Sign)) {
                toRemove.add(s.getLocation());
                continue;
            }
            SignHelper.sendSignChange(p, s.getBlock(), SignHelper.translate((Sign) s.getBlock().getState(), p));
        }
        for(Location s : toRemove) {
            SignManager.i.removeSign(s);
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

    /**
     * Schedules a new Update for a block which is 2 ticks delayed so the sign update is the last packet the player receives
     * @param block The sign to update
     */
    public static void scheduleSignUpdate(Block block) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateSigns.i, () -> handleSignUpdate(block), 2);
    }

    /**
     * Updates one sign for one player
     * @param player The player
     * @param block The sign
     */
    public static void handleSignUpdateForPlayer(Player player, Block block) {
        SignHelper.sendSignChange(player, block, SignHelper.translate((Sign) block.getState(), player));
    }
}
