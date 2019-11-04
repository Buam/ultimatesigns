package com.buam.ultimatesigns.files;

import com.buam.ultimatesigns.Constants;
import com.buam.ultimatesigns.USign;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.*;
import java.util.*;

public class CSVFile {

    /**
     * Saves a set of Signs to a file in the CSV format without a header
     * The file gets created if it doesn't exist yet
     * @param path The path of the file to save to
     * @param blocks The set of signs to save
     */
    public static void write(String path, Set<USign> blocks) {
        try {
            File f = new File(path);
            if(!f.exists()) {
                f.createNewFile();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));

            for(USign s : blocks) {
                StringBuilder line = new StringBuilder();
                Location l = s.getBlock().getLocation();
                line.append(l.getWorld().getUID()).append(",, ");
                line.append(l.getBlockX()).append(",, ");
                line.append(l.getBlockY()).append(",, ");
                line.append(l.getBlockZ()).append(",, ");
                line.append(s.getOwner().toString()).append(",, ");
                for(String cmd : s.getCommands()) {
                    line.append(cmd).append(",, ");
                }
                line = new StringBuilder(line.substring(0, line.length() - 3));

                line.append(" // ");
                for(String per : s.getPermissions()) {
                    line.append(per).append(",, ");
                }
                // Remove ,,%20
                line = new StringBuilder(line.substring(0, line.length() - 3));

                writer.write(line.toString());
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads all signs from a CSV file without a header
     * Returns an empty set if the file doesn't exist
     * @param path The path of the file to get
     * @return The parsed set of signs
     */
    public static Set<USign> read(String path) {
        Set<USign> out = new HashSet<>();

        File f = new File(path);
        if(!f.exists()) return out;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));

            String line;

            while((line = reader.readLine()) != null) {
                String[] pSplit = line.split("//");
                String[] split = pSplit[0].split(",,");
                if(split.length < 5) continue;

                Location l = new Location(Bukkit.getWorld(UUID.fromString(split[0].trim())), Integer.parseInt(split[1].trim()), Integer.parseInt(split[2].trim()), Integer.parseInt(split[3].trim()));
                UUID owner = UUID.fromString(split[4].trim());

                List<String> commands = new ArrayList<>();
                if(split.length > 5) {
                    for (int i = 5; i < split.length; i++) {
                        commands.add(split[i].trim());
                    }
                }

                List<String> permissions = new ArrayList<>();
                if(pSplit.length > 1) {
                    String[] permissionSplit = pSplit[1].split(",,");
                    if (permissionSplit.length != 0) {
                        for (int i = 0; i < permissionSplit.length; i++) {
                            permissions.add(permissionSplit[i].trim());
                        }
                    }
                }

                if(l.getWorld().getBlockAt(l) != null) if(Constants.isSign(l.getWorld().getBlockAt(l).getType())) out.add(new USign(l, commands, permissions, owner));
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out;
    }

}
