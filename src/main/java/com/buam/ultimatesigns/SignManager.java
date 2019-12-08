package com.buam.ultimatesigns;

import com.buam.ultimatesigns.config.Config;
import com.buam.ultimatesigns.extras.SignUpdater;
import com.buam.ultimatesigns.files.CSVFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.*;

public class SignManager {
    /**
     * Static reference to the sign manager
     */
    public static SignManager i;

    /**
     * A set of signs across the whole server (and all its worlds)
     */
    private final Set<USign> signs;

    /**
     * A set of times when a sign was last used by a player
     * currently unused
     */
    private final Set<SignTime> times;

    /**
     * A set of times a sign was used. Will be resetted after a configurable amount of time
     */
    private final Set<SignUses> uses;

    /**
     * The path were the data.csv file lies
     */
    private final String data_path;

    public SignManager(String data_file_path) {
        i = this;
        data_path = data_file_path;

        signs = new HashSet<>();
        uses = new HashSet<>();
        times = new HashSet<>();

        loadSigns();
    }

    public Set<USign> getAllSigns() {
        return signs;
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
        // times.removeIf(sign -> sign.getPlayerID().equals(p.getUniqueId()) && sign.getLocation().equals(l));
        // times.add(new SignTime(p.getUniqueId(), l));
        for(SignTime st : times) {
            if(st.equals(new SignTime(p.getUniqueId(), l))) {
                st.now();
                return;
            }
        }

        times.add(new SignTime(p.getUniqueId(), l));
    }

    private void removeSignAt(Location l) {
        signs.removeIf(sign -> sign.getBlock().getLocation().equals(l));
        times.removeIf(use -> use.getLocation().equals(l));
        uses.removeIf(use -> use.getSign().equals(l));
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
     * Saves all signs into the data.csv file
     */
    public void saveSigns() {
        CSVFile.write(data_path, signs);
    }

    /**
     * Checks if a block is a sign or is the block a sign is attached to
     * @param l The location of the block to check
     * @return The sign if one was found that fits the criteria
     */
    @SuppressWarnings("deprecation")
    public USign isRelative(Location l) {
        for(USign s : signs) {
            try {
                // Returns s if l is the location of a block that is a sign or a block that a sign is attached to
                if (s.getLocation().equals(l)) return s;
                org.bukkit.material.Sign sign = (org.bukkit.material.Sign) s.getBlock().getState().getData();
                if (s.getLocation().equals(s.getBlock().getRelative(sign.getAttachedFace()).getLocation())) return s;
            } catch(ClassCastException e) {
                // It failed, why? I have no idea
                // So do nothing lol
            }
        }
        return null;
    }

    /**
     * Loads data from the data.csv file, clears all sets and populates the sign set, the times set and the uses set with this data
     */
    public void loadSigns() {
        SignData data = CSVFile.read(data_path);

        signs.clear();
        uses.clear();
        times.clear();

        signs.addAll(data.signs);
        uses.addAll(data.uses);
        times.addAll(data.times);
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

    public SignTime getLastUsed(Player player, USign sign) {
        for(SignTime st : times) {
            if(st.equals(new SignTime(player.getUniqueId(), sign.getLocation()))) {
                return st;
            }
        }
        if(!isUltimateSign(sign.getLocation())) {
            signs.add(sign);
        }
        return new SignTime(player.getUniqueId(), sign.getLocation(), Config.i.i("not-used-yet-time"));
    }

    public Set<SignTime> getAllSignTimes() {
        return times;
    }

    public Set<SignUses> getAllSignUses() {
        return uses;
    }

    public void saveUse(Player player, Location location) {
        for(SignUses u : uses) {
            if(u.getPlayer().equals(player.getUniqueId()) && u.getSign().equals(location)) {
                u.inc();
                return;
            }
        }
        uses.add(new SignUses(player.getUniqueId(), location, 1));
    }

    public void resetSignUses() {
        long currTime = System.currentTimeMillis();
        long resetTime = Config.i.i(Constants.SIGN_USES_RESET_TIME) * 1000;
        if(currTime - UltimateSigns.i.lastReset >= resetTime) {
            for(SignUses u : uses) {
                u.reset();
            }
            System.out.println("[UltimateSigns] Reset all sign uses");

            UltimateSigns.i.lastReset = UltimateSigns.i.lastReset + resetTime;
        }
    }

    public int getUses(Player player, Location location) {
        for(SignUses u : uses) {
            if(u.getPlayer().equals(player.getUniqueId()) && u.getSign().equals(location)) {
                return u.getUses();
            }
        }
        return 0;
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
        UUID owner = signAt(toSet).getOwner();
        signs.remove(signAt(toSet));
        signs.add(new USign(toSet, new ArrayList<>(signAt(original).getCommands()), new ArrayList<>(signAt(original).getPermissions()), owner));
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
