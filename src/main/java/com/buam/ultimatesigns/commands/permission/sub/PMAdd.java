package com.buam.ultimatesigns.commands.permission.sub;

import com.buam.ultimatesigns.Constants;
import com.buam.ultimatesigns.SignManager;
import com.buam.ultimatesigns.UltimateSigns;
import com.buam.ultimatesigns.commands.ChatStates;
import com.buam.ultimatesigns.commands.ParticleHelper;
import com.buam.ultimatesigns.commands.SignState;
import com.buam.ultimatesigns.config.Messages;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PMAdd implements Listener {

    public void onCommand(Player player) {
        Block target = player.getTargetBlockExact(40);

        // If it is not a sign, don't do anything
        if(!Constants.isSign(target.getType())) {
            player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("look-at-sign-message"));
            return;
        }

        // If the sign is not yet registered, do so
        if(SignManager.i.isUltimateSign(target.getLocation())) SignManager.i.addSign(target.getLocation(), player.getUniqueId());

        // Set state
        UltimateSigns.command.states.put(player, new SignState(target, ChatStates.ADD_PERMISSION));

        player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("add-permission-message"));
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if(UltimateSigns.command.states.containsKey(player)) {
            SignState signState = UltimateSigns.command.states.get(player);
            if(signState.state == ChatStates.ADD_PERMISSION) {
                // This player is adding a command, receive it
                // Now do stuff
                String message = e.getMessage().trim();

                if(message.equals("0")) {
                    // Abort
                    player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("abort-message"));
                } else {
                    // Add permission
                    SignManager.i.addPermission(signState.block.getLocation(), message);
                    player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("added-permission-message") + message);

                    // Play a cool particle effect!
                    ParticleHelper.p(signState.block.getLocation());
                }

                UltimateSigns.command.states.remove(player);

                e.setCancelled(true);
            }
        }
    }

}
