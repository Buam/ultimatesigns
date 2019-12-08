package com.buam.ultimatesigns.commands.cmd.sub;

import com.buam.ultimatesigns.Constants;
import com.buam.ultimatesigns.SignManager;
import com.buam.ultimatesigns.UltimateSigns;
import com.buam.ultimatesigns.commands.ParticleHelper;
import com.buam.ultimatesigns.config.Config;
import com.buam.ultimatesigns.config.Messages;
import com.buam.ultimatesigns.files.CommandFileReader;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.IOException;

public class CMDFile {

    public void onCommand(Player player, String[] args) {

        if(args.length >= 3) {
            Block target = player.getTargetBlockExact(40);

            // If it is not a sign, don't do anything
            if(!Constants.isSign(target.getType())) {
                player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("look-at-sign-message"));
                return;
            }

            // If the sign is not yet registered, do so
            if(SignManager.i.isUltimateSign(target.getLocation())) SignManager.i.addSign(target.getLocation(), player.getUniqueId());

            // Get file name
            StringBuilder sb = new StringBuilder();
            for(int i = 2; i<args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            String file = sb.toString().trim();

            // Load file, if it doesn't work, send the error message
            try {
                SignManager.i.setCommands(target.getLocation(), CommandFileReader.read(UltimateSigns.i.getDataFolder() + Config.i.s(Constants.COMMANDS_SUBFOLDER) + file));
                player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("loaded-commands-message"));

                // Play a cool particle effect!
                ParticleHelper.p(target.getLocation());
            } catch(IOException e) {
                player.sendMessage(UltimateSigns.PREFIX + Messages.i.s("loading-error-message"));
                player.sendMessage(ChatColor.RED + e.getMessage());
            }

        } else {
            player.sendMessage(UltimateSigns.PREFIX + "Usage: /ultimatesigns cmd file <filename>");
        }
    }

}
