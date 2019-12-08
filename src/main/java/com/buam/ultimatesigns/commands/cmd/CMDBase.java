package com.buam.ultimatesigns.commands.cmd;

import com.buam.ultimatesigns.Constants;
import com.buam.ultimatesigns.SignManager;
import com.buam.ultimatesigns.UltimateSigns;
import com.buam.ultimatesigns.config.Messages;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/*
/ultimatesigns cmd
Includes all arguments, gets created and called when args[0] = "cmd"
 */
public class CMDBase {

    public void onCommand(CommandSender sender, String[] args) {
        // Not a sub command for the console so:
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if(!player.hasPermission(Constants.COMMAND_PERMISSION)) {
                player.sendMessage(Messages.i.s("no-permission-message"));
                return;
            }

            if (args.length == 1) {
                // No sub sub commands

                Block block = player.getTargetBlock(null, 40);
                // Only if it is a sign. If it is not yet registered, register it
                if(Constants.isSign(block.getType())) {
                    if(!SignManager.i.isUltimateSign(block.getLocation())) SignManager.i.addSign(block.getLocation(), player.getUniqueId());
                    listCommands(player, block);
                } else {
                    player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("look-at-sign-message"));
                }
            } else if(args.length >= 2) {
                switch(args[1]) {
                    case "add":
                        UltimateSigns.command.cmdAdd.onCommand(player);
                        return;
                    case "remove":
                        UltimateSigns.command.cmdRemove.onCommand(player);
                        return;
                    case "edit":
                        UltimateSigns.command.cmdEdit.onCommand(player);
                        return;
                    case "file":
                        UltimateSigns.command.cmdFile.onCommand(player, args);
                }
            } else {
                usage(player);
            }
        } else {
            sender.sendMessage(Messages.i.s("only-players-message"));
        }

    }


    /**
     * Lists all commands from a sign to a player
     * @param player The player which this function will list the commands to
     * @param block The sign block
     * @return false if the sign has no commands
     */
    public static boolean listCommands(Player player, Block block) {
        List<String> commands = SignManager.i.signAt(block.getLocation()).getCommands();

        if(commands.size() == 0) {
            // Sign has no commands
            player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("sign-no-commands-message"));
            return false;
        }

        player.sendMessage(""); // Empty line for readability
        player.sendMessage(Messages.i.s("sign-commands-message"));

        for(int i = 0; i < commands.size(); i++) {
            player.sendMessage(ChatColor.GREEN + Integer.toString(i + 1) + ": " + ChatColor.BLUE + commands.get(i));
        }

        return true;
    }

    /**
     * Sends a usage message on how to use this command
     * @param player The player to send the message to
     */
    private static void usage(Player player) {
        player.sendMessage(ChatColor.RED + "Usage: /ultimatesigns [subcommand]");
    }

}
