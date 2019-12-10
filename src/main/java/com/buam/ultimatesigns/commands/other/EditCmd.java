package com.buam.ultimatesigns.commands.other;

import com.buam.ultimatesigns.SharedConstants;
import com.buam.ultimatesigns.UltimateSigns;
import com.buam.ultimatesigns.config.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
/ultimatesigns edit
Includes all arguments, gets created and called when args[0] = "edit"
 */
public class EditCmd {

    public void onCommand(CommandSender sender) {
        // Not a sub command for the console so:
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if(!player.hasPermission(SharedConstants.EDIT_PERMISSION)) {
                player.sendMessage(Messages.i.s("no-permission-message"));
                return;
            }

            if(UltimateSigns.command.inEditMode.contains(player)) {
                UltimateSigns.command.inEditMode.remove(player);
                player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("sign-editor-disabled-message"));
            } else {
                UltimateSigns.command.inEditMode.add(player);
                player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("sign-editor-enabled-message"));
            }
        } else {
            sender.sendMessage(Messages.i.s("only-players-message"));
        }

    }

}
