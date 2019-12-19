package com.buam.ultimatesigns.events;

import com.buam.ultimatesigns.SharedConstants;
import com.buam.ultimatesigns.SignManager;
import com.buam.ultimatesigns.USign;
import com.buam.ultimatesigns.UltimateSigns;
import com.buam.ultimatesigns.config.Config;
import com.buam.ultimatesigns.config.Messages;
import com.buam.ultimatesigns.extras.SignUpdater;
import com.buam.ultimatesigns.lang.Language;
import com.buam.ultimatesigns.lang.exceptions.InvalidArgumentsException;
import com.buam.ultimatesigns.utils.SignUtils;
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

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

public class SignListener implements Listener {

    /**
     * Gets called when a block breaks. Checks if that block is related to a registered sign and checks
     * if the player who broke it is even allowed to break that block
     * @param e The event
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(!Config.i.b("protect-signs")) return;

        // Remove the sign from the list if it's the owner
        Block b = e.getBlock();

        if(SharedConstants.isSign(b.getType())) {
            USign s = SignManager.i.isRelative(b.getLocation());

            if(s != null) {
                Player player = e.getPlayer();

                // It is a sign, or the block that is going to break has a sign attached to it
                if(s.getOwner().equals(player.getUniqueId()) || player.hasPermission(SharedConstants.BREAK_PERMISSION)) {
                    // The sign was rightfully broken by its owner
                    SignManager.i.removeSign(s.getLocation());
                } else {
                    // Someone else tried to remove this sign, even though it was not his
                    player.sendMessage(Messages.i.s("not-your-sign-message"));
                    e.setCancelled(true);
                }
            }
        }
    }

    /**
     * Gets called when the text of a sign changes
     * Updates that sign for all players directly
     * @param e The event
     */
    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        // Add the sign if it isn't registered yet
        if(!SignManager.i.isUltimateSign(e.getBlock().getLocation())) SignManager.i.addSign(e.getBlock().getLocation(), e.getPlayer().getUniqueId());

        // Update everything and save
        SignUpdater.scheduleSignUpdate(e.getBlock());
        // Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateSigns.i, () -> SignUpdater.handleSignUpdate(e.getBlock()), 2);
        // Save Signs
        // Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateSigns.i, () -> SignManager.i.saveSigns(), 10);
    }

    /**
     * Gets called when a player joins the server
     * Updates all signs after 2 ticks
     * @param e The event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        // Update all signs for the player who joined
        Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateSigns.i, () -> {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                SignUpdater.handleSignsForPlayer(e.getPlayer());
            }
        }, 2);

        // Say something when the dev joined (cause I'm so awesome)
        // Of course it is configurable
        if(Config.i.b("dev-join-notify") && e.getPlayer().getUniqueId().equals(UUID.fromString("576a9d57-abea-4e86-bd0e-5971d2b4bb77"))) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(p.hasPermission("ultimatesigns.admin")) {
                    p.sendMessage(UltimateSigns.PREFIX + "A developer (" + e.getPlayer().getName() + ") has just joined the server!");
                }
            }
        }
    }

    /**
     * Gets called when a player changes the world
     * Updates all signs in that world for that player
     * @param e The event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e) {
        // Update signs when a player changes world (needed for [world])
        Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateSigns.i, () -> SignUpdater.handleSignsInWorldForPlayer(e.getPlayer().getWorld(), e.getPlayer()), 2);
    }

    /**
     * Gets called when the player left or right clicks on a sign
     * Handles commands as well as sign editing
     * @param e The event
     * @throws IllegalAccessException Gets thrown by the language
     * @throws InstantiationException Gets thrown by the language
     * @throws InvalidArgumentsException Gets thrown by the language
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) throws IllegalAccessException, InstantiationException, InvalidArgumentsException, NoSuchMethodException, InvocationTargetException {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.LEFT_CLICK_BLOCK) return;
        //if (SharedConstants.isSign(e.getClickedBlock().getType())) {
        boolean isSign = SharedConstants.isSign(e.getClickedBlock().getType());
        if(isSign) SignUpdater.handleSignUpdateForPlayer(e.getPlayer(), e.getClickedBlock());

        if (isSign && e.getPlayer().isSneaking() && e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && UltimateSigns.command.inEditMode.contains(e.getPlayer())) {

            // Edit the sign
            Block block = e.getClickedBlock();

            if (block != null) {
                // It's a sign
                SignUpdater.sendOriginalSignText(e.getPlayer(), block);

                // Open sign editor
                SignUtils.openSignEditor((Sign) block.getState(), e.getPlayer());

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
                        if (s.contains("(left)")) {
                            hasLeft = true;
                            break;
                        }
                    }
                    if(!e.getPlayer().isSneaking() || !left) {
                        // Save the time of this use
                        SignManager.i.saveSignTime(e.getPlayer(), e.getClickedBlock().getLocation());

                        // Increase click counter
                        SignManager.i.saveUse(e.getPlayer(), e.getClickedBlock().getLocation());

                        // Start language engine
                        Language l = new Language(commands.stream().map(String::new).toArray(String[]::new), left);
                        l.executeAll(e.getPlayer(), SignManager.i.signAt(e.getClickedBlock().getLocation()));

                        // Cancel event if left clicked so the sign won't be destroyed
                        if(left && hasLeft) e.setCancelled(true);
                    }
                } else {
                    e.getPlayer().sendMessage(Messages.i.s("no-permission-sign-message"));
                }
            }
        }
        //}
    }



}
