package com.buam.ultimatesigns;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class USign {

    private Location l;
    private List<String> commands;
    private List<String> permissions;

    public USign(Block block, List<String> commands) {
        this.l = block.getLocation();
        this.commands = commands;
        this.permissions = new ArrayList<>();
    }

    public USign(Location location, List<String> commands) {
        this.l = location;
        this.commands = commands;
        this.permissions = new ArrayList<>();
    }

    public USign(Location location, List<String> commands, List<String> permissions) {
        this.l = location;
        this.commands = commands;
        this.permissions = permissions;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void addPermission(String s) {
        permissions.add(s);
    }

    public void removePermission(int index) {
        permissions.remove(index);
    }

    public boolean hasAllPermissions(Player p) {
        for(String s : permissions) {
            if(!p.hasPermission(s)) return false;
        }
        return true;
    }

    public void editPermission(int index, String newPermission) {
        permissions.set(index, newPermission);
    }

    public Location getLocation() {
        return l;
    }

    public Block getBlock() {
        return l.getBlock();
    }

    public List<String> getCommands() {
        return commands;
    }

    public void addCommand(String cmd) {
        commands.add(cmd);
    }

    public void removeCommand(int index) {
        commands.remove(index);
    }

    public void setCommand(int index, String newCmd) {
        commands.set(index, newCmd);
    }

    public void setCommands(List<String> newCommands) { commands = newCommands; }

    @Override
    public boolean equals(Object other) {
        if(other instanceof USign) {
            if(((USign) other).getLocation().equals(getLocation())) {
                return true;
            }
        }
        return false;
    }
}
