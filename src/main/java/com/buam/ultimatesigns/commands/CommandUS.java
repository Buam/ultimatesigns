package com.buam.ultimatesigns.commands;

import com.buam.ultimatesigns.Constants;
import com.buam.ultimatesigns.UltimateSigns;
import com.buam.ultimatesigns.commands.cmd.CMDBase;
import com.buam.ultimatesigns.commands.cmd.sub.CMDAdd;
import com.buam.ultimatesigns.commands.cmd.sub.CMDEdit;
import com.buam.ultimatesigns.commands.cmd.sub.CMDFile;
import com.buam.ultimatesigns.commands.cmd.sub.CMDRemove;
import com.buam.ultimatesigns.commands.other.CopyPasteCmd;
import com.buam.ultimatesigns.commands.other.EditCmd;
import com.buam.ultimatesigns.commands.other.ReloadCmd;
import com.buam.ultimatesigns.commands.permission.PMBase;
import com.buam.ultimatesigns.commands.permission.sub.PMAdd;
import com.buam.ultimatesigns.commands.permission.sub.PMEdit;
import com.buam.ultimatesigns.commands.permission.sub.PMRemove;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CommandUS implements CommandExecutor {

    public final CMDBase cmdBase;
    public final CMDAdd cmdAdd;
    public final CMDRemove cmdRemove;
    public final CMDEdit cmdEdit;
    public final CMDFile cmdFile;

    public final PMBase pmBase;
    public final PMAdd pmAdd;
    public final PMRemove pmRemove;
    public final PMEdit pmEdit;

    public final EditCmd editCmd;
    public final CopyPasteCmd copyPasteCmd;
    public final ReloadCmd reloadCmd;

    public final Map<Player, SignState> states = new HashMap<>();

    public final Set<Player> inEditMode = new HashSet<>();

    public CommandUS(JavaPlugin plugin) {
        cmdBase = new CMDBase();
        cmdAdd = new CMDAdd();
        cmdRemove = new CMDRemove();
        cmdEdit = new CMDEdit();
        cmdFile = new CMDFile();

        pmBase = new PMBase();
        pmAdd = new PMAdd();
        pmRemove = new PMRemove();
        pmEdit = new PMEdit();

        editCmd = new EditCmd();
        copyPasteCmd = new CopyPasteCmd();
        reloadCmd = new ReloadCmd();

        Bukkit.getServer().getPluginManager().registerEvents(cmdAdd, plugin);
        Bukkit.getServer().getPluginManager().registerEvents(cmdRemove, plugin);
        Bukkit.getServer().getPluginManager().registerEvents(cmdEdit, plugin);

        Bukkit.getServer().getPluginManager().registerEvents(pmAdd, plugin);
        Bukkit.getServer().getPluginManager().registerEvents(pmRemove, plugin);
        Bukkit.getServer().getPluginManager().registerEvents(pmEdit, plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        if(args.length > 0) {
            // There are arguments, Command Classes are handling the rest
            String subcommand = args[0];

            switch (subcommand) {
                case "cmd":
                    cmdBase.onCommand(sender, args);
                    break;
                case "permission":
                    pmBase.onCommand(sender, args);
                    break;
                case "edit":
                    editCmd.onCommand(sender);
                    break;
                case "copy":
                case "paste":
                    copyPasteCmd.onCommand(sender, args);
                    break;
                case "reload":
                    reloadCmd.onCommand(sender);
                    break;
                default:
                    // Unknown subcommand, show help
                    help(sender);
                    break;
            }

        } else {
            // No argument given, send version
            sender.sendMessage(UltimateSigns.PREFIX + "v" + UltimateSigns.i.getDescription().getVersion());
        }

        return true;
    }

    /**
     * Displays the help messages to a player
     * @param sender The sender to send this message to
     */
    private void help(CommandSender sender) {
        if(sender instanceof Player && !sender.hasPermission(Constants.ADMIN_PERMISSION)) return;
        sender.sendMessage(ChatColor.WHITE + "- " + ChatColor.GREEN + "Ultimate" + ChatColor.BLUE + "Signs" + ChatColor.WHITE + " Help-------------------------------------");
        sender.sendMessage(ChatColor.GREEN + "/ultimatesigns" + ChatColor.BLUE + " - main " + ChatColor.GREEN + "Ultimate" + ChatColor.GREEN + "Signs" + ChatColor.BLUE + " command");
        sender.sendMessage(ChatColor.GREEN + "/ultimatesigns reload" + ChatColor.BLUE + " - reload the plugin");
        sender.sendMessage(ChatColor.GREEN + "/ultimatesigns edit" + ChatColor.BLUE + " - toggles sign editing mode (shift + right click)");
        sender.sendMessage(ChatColor.GREEN + "/ultimatesigns cmd" + ChatColor.BLUE + " - lists all commands of a sign");
        sender.sendMessage(ChatColor.GREEN + "/ultimatesigns cmd add" + ChatColor.BLUE + " - add a command to a sign");
        sender.sendMessage(ChatColor.GREEN + "/ultimatesigns cmd remove" + ChatColor.BLUE + " - remove a command from a sign");
        sender.sendMessage(ChatColor.GREEN + "/ultimatesigns cmd edit" + ChatColor.BLUE + " - edit a command");
        sender.sendMessage(ChatColor.GREEN + "/ultimatesigns cmd file <filename>" + ChatColor.BLUE + " - commands from file to a sign");
        sender.sendMessage(ChatColor.GREEN + "/ultimatesigns permission" + ChatColor.BLUE + " - list all permissions of a sign");
        sender.sendMessage(ChatColor.GREEN + "/ultimatesigns permission add" + ChatColor.BLUE + " - add a permission to a sign");
        sender.sendMessage(ChatColor.GREEN + "/ultimatesigns permission remove" + ChatColor.BLUE + " - remove a permission from a sign");
        sender.sendMessage(ChatColor.GREEN + "/ultimatesigns permission edit" + ChatColor.BLUE + " - edit a permission");
        sender.sendMessage(ChatColor.GREEN + "/ultimatesigns copy" + ChatColor.BLUE + " - copy the sign you're looking at");
        sender.sendMessage(ChatColor.GREEN + "/ultimatesigns paste" + ChatColor.BLUE + " - paste a previously copied sign");
        sender.sendMessage("-----------------------------------------------------");
    }
}
