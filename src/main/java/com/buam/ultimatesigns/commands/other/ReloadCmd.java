package com.buam.ultimatesigns.commands.other;

import com.buam.ultimatesigns.Constants;
import com.buam.ultimatesigns.UltimateSigns;
import com.buam.ultimatesigns.config.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCmd {

    public boolean onCommand(CommandSender sender, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(!player.hasPermission(Constants.RELOAD_PERMISSION)) {
                player.sendMessage(Messages.i.s("no-permission-message"));
                return true;
            }
        }

        UltimateSigns.i.reload();

        sender.sendMessage(UltimateSigns.PREFIX + "Reloaded successfully");

        return true;
    }

}
