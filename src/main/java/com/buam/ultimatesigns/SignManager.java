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

import java.util.*;

public class SignManager {
    public static SignManager i;

    private Set<USign> signs = new HashSet<>();
    private Set<SignTime> lastUsed = new HashSet<SignTime>();

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


    public boolean isUltimateSign(Location l) {
        return containsSignAt(l);
    }

    public void saveSigns() {
        CSVFile.write(data_path, signs);
    }

    public void loadSigns() {
        signs.addAll(CSVFile.read(data_path));
    }

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
