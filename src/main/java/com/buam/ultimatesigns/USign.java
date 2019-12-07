package com.buam.ultimatesigns;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class USign {

    /**
     * The location of this sign
     */
    private Location l;

    /**
     * An ordered list of all the commands that are added to the sign
     */
    private List<String> commands;

    /**
     * An unordered list of all permissions that a player requires to use this sign
     */
    private List<String> permissions;

    /**
     * The unique Identifier of the Player who owns this sign
     */
    private UUID owner;

    public USign(Block block, List<String> commands) {
        this.l = block.getLocation();
        this.commands = commands;
        this.permissions = new ArrayList<>();
    }

    public USign(Location location, List<String> commands, List<String> permissions) {
        this.l = location;
        this.commands = commands;
        this.permissions = permissions;
    }

    public USign(Location location, List<String> commands, List<String> permissions, UUID owner) {
        this.l = location;
        this.commands = commands;
        this.permissions = permissions;
        this.owner = owner;
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

    /**
     * Checks if a player has all permissions that this sign requires to use it
     * @param p The player to check permissions of
     * @return true if the player has all permissions needed, false otherwise
     */
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

    public UUID getOwner() {
        return owner;
    }

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
