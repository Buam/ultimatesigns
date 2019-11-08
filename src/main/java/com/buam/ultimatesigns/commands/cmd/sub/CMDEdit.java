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

public class CMDEdit implements Listener {

    public boolean onCommand(Player player) {
        Block target = player.getTargetBlock(null, 40);

        // If it is not a sign, don't do anything
        if(!Constants.isSign(target.getType())) {
            player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("look-at-sign-message"));
            return true;
        }

        // If the sign is not yet registered, do so
        if(SignManager.i.isUltimateSign(target.getLocation())) SignManager.i.addSign(target.getLocation());

        // Does this sign have commands? listCommands() handles error
        if(CMDBase.listCommands(player, target)) {
            // Set state
            UltimateSigns.command.states.put(player, new SignState(target, ChatStates.EDIT_COMMAND_INDEX));

            player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("edit-command-message"));
        }

        return true;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        Player player = e.getPlayer();

        if(UltimateSigns.command.states.containsKey(player)) {
            SignState state = UltimateSigns.command.states.get(player);
            String message = e.getMessage().trim();
            if (message.equals("0")) {
                // Abort
                player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("abort-message"));

                UltimateSigns.command.states.remove(player);
            } else {
                if (state.state == ChatStates.EDIT_COMMAND_INDEX) {
                    // This player typed in the index of the command to edit

                    // Parse integer and go on with the new command (new state)
                    try {
                        int index = Integer.parseInt(message) - 1;

                        if (SignManager.i.hasCommand(state.block.getLocation(), index)) {
                            // There is a command with that index, new state with index
                            state.state = ChatStates.EDIT_COMMAND;
                            state.index = index;
                            UltimateSigns.command.states.put(player, state);

                            player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("enter-new-command-message"));
                        } else {
                            player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("no-command-message"));
                        }

                    } catch (NumberFormatException exc) {
                        player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("valid-number-message"));
                    }
                } else if (state.state == ChatStates.EDIT_COMMAND) {

                    // This player typed in the new command, the state now has an index assigned
                    SignManager.i.editCommand(state.block.getLocation(), state.index, message);

                    player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("changed-command-message"));

                    // Remove the player from the states list, he's done
                    UltimateSigns.command.states.remove(player);

                    // Play a cool particle effect!
                    ParticleHelper.p(state.block.getLocation());
                }
            }

            e.setCancelled(true);
        }

    }

}
