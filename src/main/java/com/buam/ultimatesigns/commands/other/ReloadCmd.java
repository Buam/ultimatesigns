package com.buam.ultimatesigns.commands.other;

import com.buam.ultimatesigns.SharedConstants;
import com.buam.ultimatesigns.UltimateSigns;
import com.buam.ultimatesigns.config.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCmd {

    public void onCommand(CommandSender sender) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(!player.hasPermission(SharedConstants.RELOAD_PERMISSION)) {
                player.sendMessage(Messages.i.s("no-permission-message"));
                return;
            }
        }

        UltimateSigns.i.reload();

        sender.sendMessage(UltimateSigns.PREFIX + "Reloaded successfully");

    }

}
