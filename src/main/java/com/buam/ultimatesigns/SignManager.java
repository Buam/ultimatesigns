package com.buam.ultimatesigns;

import com.buam.ultimatesigns.extras.SignUpdater;
import com.buam.ultimatesigns.files.CSVFile;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SignManager {
    /**
     * Static reference to the sign manager
     */
    public static SignManager i;

    /**
     * A set of signs across the whole server (and all its worlds)
     */
    private Set<USign> signs = new HashSet<>();

    /**
     * A set of times when a sign was last used by a player
     * currently unused
     */
    private Set<SignTime> lastUsed = new HashSet<SignTime>();

    /**
     * The path were the data.csv file lies
     */
    private String data_path;

    public SignManager(String data_file_path) {
        i = this;
        data_path = data_file_path;
        loadSigns();
    }

    public Set<USign> getAllSigns() {
        return signs;
    }

    public void addSign(Location l) {
        if(Constants.isSign(l.getBlock().getType()) && !containsSignAt(l)) {
            signs.add(new USign(l.getBlock(), new ArrayList<>()));
        }
    }

    public void addSign(Location l, UUID owner) {
        if(Constants.isSign(l.getBlock().getType()) && !containsSignAt(l)) {
            signs.add(new USign(l.getBlock().getLocation(), new ArrayList<>(), new ArrayList<>(), owner));
        }
    }

    public void addCommand(Location l, String cmd) {
        if(containsSignAt(l)) {
            signAt(l).addCommand(cmd);
        }
        saveSigns();
    }

    public void removeCommand(Location l, int index) {
        if(containsSignAt(l)) {
            signAt(l).removeCommand(index);
        }
        saveSigns();
    }

    public void editCommand(Location l, int index, String newCmd) {
        if(containsSignAt(l)) {
            signAt(l).setCommand(index, newCmd);
        }
        saveSigns();
    }

    public void addPermission(Location l, String permission) {
        if(containsSignAt(l)) {
            signAt(l).addPermission(permission);
        }
        saveSigns();
    }

    public void removePermission(Location l, int index) {
        if(containsSignAt(l)) {
            signAt(l).removePermission(index);
        }
        saveSigns();
    }

    public void editPermission(Location l, int index, String newPermission) {
        if(containsSignAt(l)) {
            signAt(l).editPermission(index, newPermission);
        }
        saveSigns();
    }

    public USign signAt(Location l) {
        for(USign s : signs) {
            if(s.getBlock().getLocation().equals(l)) return s;
        }
        return null;
    }

    private boolean containsSignAt(Location l) {
        for(USign s : signs) {
            if(s.getBlock().getLocation().equals(l)) return true;
        }
        return false;
    }

    public void saveSignTime(Player p, Location l) {
        lastUsed.add(new SignTime(p, signAt(l), 1000000000L));
    }

    private void removeSignTime(Player p, Location l) {
        lastUsed.remove(new SignTime(p, signAt(l), 1000000000L));
    }

    private void removeSignAt(Location l) {
        signs.removeIf(sign -> sign.getBlock().getLocation().equals(l));
    }

    public void removeSign(Location l) {
        removeSignAt(l);
    }

    /**
     * Checks if a sign was registered before and is in the list of signs
     * @param l The location of the block to check
     * @return true if a sign was registered before
     */
    public boolean isUltimateSign(Location l) {
        return containsSignAt(l);
    }

    /**
     * Saves all signs into the data.csv file asynchronously
     */
    public void saveSignsAsync() {
        new BukkitRunnable(){
            public void run() {
                CSVFile.write(data_path, signs);
            }
        }.runTask(UltimateSigns.i);
    }

    public void saveSigns() {
        CSVFile.write(data_path, signs);
    }

    /**
     * Checks if a block is a sign or is the block a sign is attached to
     * @param l The location of the block to check
     * @return The sign if one was found that fits the criteria
     */
    public USign isRelative(Location l) {
        for(USign s : signs) {
            // Returns s if l is the location of a block that is a sign or a block that a sign is attached to
            if(s.getLocation().equals(l)) return s;
            if(s.getLocation().equals(SignHelper.getAttachedBlock(s.getLocation().getBlock()).getLocation())) return s;
        }
        return null;
    }

    /**
     * Loads all signs from the data.csv file and populates the sign set with this data
     */
    public void loadSigns() {
        signs.addAll(CSVFile.read(data_path));
    }

    /**
     * Get all signs in a specific world
     * @param world The world to get all signs from
     * @return A set of all signs in that world
     */
    public Set<USign> getSignsInWorld(World world) {

        Set<USign> out = new HashSet<>();

        for(USign s : signs) {
            if(s.getLocation().getWorld().equals(world)) {
                out.add(s);
            }
        }

        return out;

    }

    public SignTime getLastUsed(Player arg, USign arg1) {
        for(SignTime st : lastUsed) {
            if(st.equals(new SignTime(arg, arg1))) {
                return st;
            }
        }
        return new SignTime(arg, arg1, 1000000000L);
    }

    public boolean hasPermission(Location l, int i) {
        return signAt(l).getPermissions().size() > i;
    }

    public boolean hasCommand(Location location, int i) {
        return signAt(location).getCommands().size() > i;
    }

    public void setCommands(Location location, List<String> commands) {
        signAt(location).setCommands(commands);
    }

    private void setSign(Location toSet, Location original) {
        signs.remove(signAt(toSet));
        signs.add(new USign(toSet, new ArrayList<>(signAt(original).getCommands()), new ArrayList<>(signAt(original).getPermissions())));
    }

    public void setSign(Block b, Block original) {

        USign us = signAt(original.getLocation());
        USign newUSign = signAt(b.getLocation());

        Sign newSign = (Sign) b.getState();
        Sign originalSign = (Sign) original.getState();

        newSign.setLine(0, originalSign.getLine(0));
        newSign.setLine(1, originalSign.getLine(1));
        newSign.setLine(2, originalSign.getLine(2));
        newSign.setLine(3, originalSign.getLine(3));
        newSign.update();

        setSign(newUSign.getLocation(), us.getLocation());

        Bukkit.getScheduler().scheduleSyncDelayedTask(UltimateSigns.i, () -> SignUpdater.handleSignUpdate(b), 10);

    }
}
