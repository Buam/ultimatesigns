package com.buam.ultimatesigns.commands.other;

import com.buam.ultimatesigns.UltimateSigns;
import org.bukkit.command.CommandSender;

public class ReloadCmd {

    public boolean onCommand(CommandSender sender, String[] args) {

        UltimateSigns.i.reload();

        sender.sendMessage(UltimateSigns.PREFIX + "Reloaded successfully");

        return true;
    }

}
