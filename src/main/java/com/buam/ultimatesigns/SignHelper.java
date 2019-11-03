package com.buam.ultimatesigns;

import com.buam.ultimatesigns.config.Config;
import com.buam.ultimatesigns.lang.TypeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class SignHelper {

    public static void sendSignChange(Player p, Block b, String[] lines) {
        if(!Config.i.b(Constants.ENABLE_UPDATE_PERMISSION) || p.hasPermission(Constants.UPDATE_PERMISSION)) {
            p.sendSignChange(b.getLocation(), lines);
        }
    }

    public static String[] translate(Sign sign, Player p) {
        try {
            String[] lines = new String[4];
            for (int i = 0; i < 4; i++) {
                lines[i] = TypeManager.translate(sign.getLine(i), p, SignManager.i.signAt(sign.getLocation()));
                lines[i] = translateColors(lines[i]);
            }
            return lines;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return sign.getLines();
    }

    public static String translateColors(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }

    public static String translate(String in, Player p) {

        in = in.replace(Config.i.s("player-key"), p.getName());
        in = in.replace(Config.i.s("displayname-key"), p.getDisplayName());
        in = in.replace(Config.i.s("player-online-global-key"), Integer.toString(Bukkit.getServer().getOnlinePlayers().size()));
        in = in.replace(Config.i.s("player-online-world-key"), Integer.toString(p.getWorld().getPlayers().size()));
        in = in.replace(Config.i.s("current-world-key"), p.getWorld().getName());
        in = translateColors(in);

        return in;

    }
}
