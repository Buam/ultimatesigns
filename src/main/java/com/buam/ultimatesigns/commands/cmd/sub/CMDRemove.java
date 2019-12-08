package com.buam.ultimatesigns.commands.cmd.sub;

import com.buam.ultimatesigns.Constants;
import com.buam.ultimatesigns.SignManager;
import com.buam.ultimatesigns.UltimateSigns;
import com.buam.ultimatesigns.commands.ChatStates;
import com.buam.ultimatesigns.commands.ParticleHelper;
import com.buam.ultimatesigns.commands.SignState;
import com.buam.ultimatesigns.commands.cmd.CMDBase;
import com.buam.ultimatesigns.config.Messages;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class CMDRemove implements Listener {

    public void onCommand(final Player player) {
        Block target = player.getTargetBlockExact(40);

        // If it is not a sign, don't do anything
        if(!Constants.isSign(target.getType())) {
            player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("look-at-sign-message"));
            return;
        }

        // If the sign is not yet registered, do so
        if(SignManager.i.isUltimateSign(target.getLocation())) SignManager.i.addSign(target.getLocation(), player.getUniqueId());

        // Does this sign have commands? listCommands() handles error
        if(CMDBase.listCommands(player, target)) {
            // Set state
            UltimateSigns.command.states.put(player, new SignState(target, ChatStates.REMOVE_COMMAND));

            player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("remove-command-message"));
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if(UltimateSigns.command.states.containsKey(player)) {
            SignState signState = UltimateSigns.command.states.get(player);
            if(signState.state == ChatStates.REMOVE_COMMAND) {
                // This player is removing a command, receive it
                // Now do stuff
                String message = e.getMessage().trim();

                if(message.equals("0")) {
                    // Abort
                    player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("abort-message"));

                    UltimateSigns.command.states.remove(player);
                } else {
                    // Parse integer and remove command
                    try {
                        int index = Integer.parseInt(message) - 1;

                        if(SignManager.i.hasCommand(signState.block.getLocation(), index)) {

                            SignManager.i.removeCommand(signState.block.getLocation(), index);
                            player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("removed-command-message", message));

                            UltimateSigns.command.states.remove(player);

                            // Play a cool particle effect!
                            ParticleHelper.p(signState.block.getLocation());
                        } else {
                            player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("sign-no-command-message", index));
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
