package com.buam.ultimatesigns.commands;

import com.google.common.collect.Lists;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class USTabCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(command.getName().equalsIgnoreCase("ultimatesigns")) {
            if(sender instanceof Player) {

                switch (args.length) {
                    case 1:
                        return Lists.newArrayList("help", "cmd", "permission", "edit", "copy", "paste");
                    case 2:
                        switch (args[0].toLowerCase()) {
                            case "cmd":
                                return Lists.newArrayList("add", "remove", "edit", "file");
                            case "permission":
                                return Lists.newArrayList("add", "remove", "edit");
                        }
                }

            }
        }

        return Lists.newArrayList();
    }
}
