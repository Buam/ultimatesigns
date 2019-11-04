package com.buam.ultimatesigns.events;

import com.buam.ultimatesigns.Constants;
import com.buam.ultimatesigns.SignManager;
import com.buam.ultimatesigns.USign;
import com.buam.ultimatesigns.UltimateSigns;
import com.buam.ultimatesigns.config.Config;
import com.buam.ultimatesigns.config.Messages;
import com.buam.ultimatesigns.extras.SignUpdater;
import com.buam.ultimatesigns.lang.Language;
import com.buam.ultimatesigns.lang.exceptions.InvalidArgumentsException;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class SignListener implements Listener {

    /**
     * A temporary array which gets used to pass data (idk why it is there, I could as well just delete it but I don't)
     */
    private String[] signEditTemp;

    /**
     * Gets called when a block breaks. Checks if that block is related to a registered sign and checks
     * if the player who broke it is even allowed to break that block
     * @param e
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        // Remove the sign from the list if it's the owner
        Block b = e.getBlock();

        USign s = SignManager.i.isRelative(b.getLocation());

        if(s != null) {
            Player player = e.getPlayer();

            // It is a sign, or the block that is going to break has a sign attached to it
            if(s.getOwner().equals(player.getUniqueId()) || player.hasPermission(Constants.BREAK_PERMISSION) || !Config.i.b("protect-signs")) {
                // The sign was rightfully broken by its owner
                SignManager.i.removeSign(b.getLocation());
            } else {
                // Someone else tried to remove this sign, even though it was not his
                player.sendMessage(Messages.i.s("not-your-sign-message"));
                e.setCancelled(true);
            }
        }
    }

    /**
     * Gets called when the text of a sign changes
     * Updates that sign for all players directly
     * @param e
     */
    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        if (signEditTemp != null) {
            for (int i = 0; i < 4; i++) {
                e.setLine(i, signEditTemp[i]);
            }
            signEditTemp = null;
        }
        // Add the sign if it isn't registered yet
        if(!SignManager.i.isUltimateSign(e.getBlock().getLocation())) SignManager.i.addSign(e.getBlock().getLocation(), e.getPlayer().getUniqueId());

        // Update everything and save
        Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateSigns.i, () -> SignUpdater.handleSignUpdate(e.getBlock()), 2);
        Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateSigns.i, () -> SignManager.i.saveSigns(), 10);
    }

    /**
     * Gets called when a player joins the server
     * Updates all signs after 2 ticks
     * @param e
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        // Update all signs for the player who joined
        Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateSigns.i, () -> {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                SignUpdater.handleSignsForPlayer(e.getPlayer());
            }
        }, 2);
    }

    /**
     * Gets called when a player changes the world
     * Updates all signs in that world for that player
     * @param e
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e) {
        // Update signs when a player changes world (needed for [world])
        Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateSigns.i, () -> {
            SignUpdater.handleSignsInWorldForPlayer(e.getPlayer().getWorld(), e.getPlayer());
        }, 2);
    }

    /**
     * Gets called when the player left or right clicks on a sign
     * Handles commands as well as sign editing
     * @param e
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvalidArgumentsException
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) throws IllegalAccessException, InstantiationException, InvalidArgumentsException {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (Constants.isSign(e.getClickedBlock().getType())) {
            if (e.getPlayer().isSneaking() && e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && UltimateSigns.command.inEditMode.contains(e.getPlayer())) {

                // Edit the sign
                Block block = e.getClickedBlock();

                if (block != null) {
                    // It's a sign
                    SignUpdater.sendOriginalSignText(e.getPlayer(), block);

                    // Open sign editor
                    openEditor((Sign) block.getState(), e.getPlayer());

                }

            } else {
                // Execute commands
                if (SignManager.i.isUltimateSign(e.getClickedBlock().getLocation())) {
                    // does the player have all permissions
                    if(SignManager.i.signAt(e.getClickedBlock().getLocation()).hasAllPermissions(e.getPlayer())) {
                        boolean left = e.getAction() == Action.LEFT_CLICK_BLOCK;
                        boolean hasLeft = false;
                        List<String> commands = SignManager.i.signAt(e.getClickedBlock().getLocation()).getCommands();
                        for(String s : commands) {
                            if(s.contains("(left)")) hasLeft = true;
                        }
                        if(!e.getPlayer().isSneaking() || !left) {
                            SignManager.i.saveSignTime(e.getPlayer(), e.getClickedBlock().getLocation());
                            Language l = new Language(commands.stream().map(String::new).toArray(String[]::new), left);
                            l.executeAll(e.getPlayer(), SignManager.i.signAt(e.getClickedBlock().getLocation()));
                            if(left && hasLeft) e.setCancelled(true);
                        }
                    } else {
                        e.getPlayer().sendMessage(Config.i.s("no-permission-sign-message"));
                    }
                }
            }
        }
    }

    /**
     * Helper method to open the sign editor using Protocol
     * @param s
     * @param p
     */
    public void openEditor(Sign s, Player p) {
        UltimateSigns.signEditor.open(p, s.getLocation(), null, (player, lines) -> {
            for(int i = 0; i<lines.length; i++) {
                s.setLine(i, lines[i]);
                s.update(true);
                Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateSigns.i, () -> SignUpdater.handleSignUpdate(s.getBlock()), 5);
            }
        });
    }

}
