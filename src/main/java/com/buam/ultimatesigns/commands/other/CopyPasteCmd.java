package com.buam.ultimatesigns.commands.other;

import com.buam.ultimatesigns.Constants;
import com.buam.ultimatesigns.SignManager;
import com.buam.ultimatesigns.UltimateSigns;
import com.buam.ultimatesigns.config.Messages;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CopyPasteCmd {

    final Map<Player, Block> copied = new HashMap<>();

    public void onCommand(final CommandSender sender, final String[] args) {
        // Get player
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if(!player.hasPermission(Constants.COPY_PERMISSION)) {
                player.sendMessage(Messages.i.s("no-permission-message"));
                return;
            }

            // See if it is a valid sign
            Block target = player.getTargetBlockExact(40);

            // If it is not a sign, don't do anything
            if(!Constants.isSign(target.getType())) {
                player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("look-at-sign-message"));
                return;
            }

            // If the sign is not yet registered, do so
            if(SignManager.i.isUltimateSign(target.getLocation())) SignManager.i.addSign(target.getLocation(), player.getUniqueId());

            if(args[0].equals("copy")) {
                // Copy the sign
                copied.put(player, target);
                player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("copied-sign-message"));
            } else {
                // Paste the sign
                Block original = copied.get(player);

                if(original == null) {
                    // Player hasn't copied yet
                    player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("copy-first-message"));
                    return;
                }

                if(original.getLocation().equals(target.getLocation())) {
                    // The blocks are the same, cancel
                    player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("same-sign-message"));
                    return;
                }

                // Copy the sign
                SignManager.i.setSign(target, original);
                player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("pasted-sign-message"));
            }
        } else {
            sender.sendMessage(Messages.i.s("only-players-message"));
        }

    }

}
