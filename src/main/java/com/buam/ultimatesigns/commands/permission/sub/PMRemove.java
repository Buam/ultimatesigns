package com.buam.ultimatesigns.commands.permission.sub;

import com.buam.ultimatesigns.SharedConstants;
import com.buam.ultimatesigns.SignManager;
import com.buam.ultimatesigns.UltimateSigns;
import com.buam.ultimatesigns.commands.ChatStates;
import com.buam.ultimatesigns.commands.ParticleHelper;
import com.buam.ultimatesigns.commands.SignState;
import com.buam.ultimatesigns.commands.permission.PMBase;
import com.buam.ultimatesigns.config.Messages;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PMRemove implements Listener {

    public void onCommand(Player player) {
        Block target = player.getTargetBlockExact(40);

        // If it is not a sign, don't do anything
        if(!SharedConstants.isSign(target.getType())) {
            player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("look-at-sign-message"));
            return;
        }

        // If the sign is not yet registered, do so
        if(SignManager.i.isUltimateSign(target.getLocation())) SignManager.i.addSign(target.getLocation(), player.getUniqueId());

        // Does this sign have permissions? listPermissions() handles error
        if(PMBase.listPermissions(player, target)) {
            // Set state
            UltimateSigns.command.states.put(player, new SignState(target, ChatStates.REMOVE_PERMISSION));

            player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("remove-permission-message"));
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if(UltimateSigns.command.states.containsKey(player)) {
            SignState signState = UltimateSigns.command.states.get(player);
            if(signState.state == ChatStates.REMOVE_PERMISSION) {
                // This player is removing a permission, receive it
                // Now do stuff
                String message = e.getMessage().trim();

                if(message.equals("0")) {
                    // Abort
                    player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("abort-message"));

                    UltimateSigns.command.states.remove(player);
                } else {
                    // Parse integer and remove permission
                    try {
                        int index = Integer.parseInt(message) - 1;

                        if(SignManager.i.hasPermission(signState.block.getLocation(), index)) {

                            SignManager.i.removePermission(signState.block.getLocation(), index);
                            player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("removed-permission-message", message));

                            UltimateSigns.command.states.remove(player);

                            // Play a cool particle effect!
                            ParticleHelper.p(signState.block.getLocation());
                        } else {
                            player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("sign-no-permission-message", index));
                        }

                    } catch (NumberFormatException exc) {
                        player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("valid-number-message"));
                    }
                }

                e.setCancelled(true);
            }
        }
    }

}
