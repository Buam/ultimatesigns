package com.buam.ultimatesigns.files;

import com.buam.ultimatesigns.*;
import com.buam.ultimatesigns.lang.exceptions.InvalidDataFileException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
                if(!f.createNewFile()) System.out.println(ChatColor.RED + "[UltimateSigns] failed to create data file");
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));

            writer.write(Long.toString(UltimateSigns.i.lastReset));
            writer.newLine();

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

            writer.write("----");
            writer.newLine();

            for(SignTime st : SignManager.i.getAllSignTimes()) {
                StringBuilder line = new StringBuilder();
                Location l = st.getLocation();
                line.append(st.getPlayerID().toString()).append(",, ");
                line.append(l.getWorld().getUID()).append(",, ");
                line.append(l.getBlockX()).append(",, ");
                line.append(l.getBlockY()).append(",, ");
                line.append(l.getBlockZ()).append(",, ");
                line.append(st.getMillisTime());

                writer.write(line.toString());
                writer.newLine();
            }

            writer.write("----");
            writer.newLine();

            for(SignUses su : SignManager.i.getAllSignUses()) {
                StringBuilder line = new StringBuilder();
                Location l = su.getSign();
                line.append(su.getPlayer().toString()).append(",, ");
                line.append(l.getWorld().getUID()).append(",, ");
                line.append(l.getBlockX()).append(",, ");
                line.append(l.getBlockY()).append(",, ");
                line.append(l.getBlockZ()).append(",, ");
                line.append(su.getUses());

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
     * @return The parsed sign data
     */
    public static SignData read(String path) {
        Set<USign> signs = new HashSet<>();

        SignData out = new SignData();

        File f = new File(path);
        if(!f.exists()) return out;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));

            String line;

            // 0 -> Reading Signs
            // 1 -> Reading Sign Times
            // 2 -> Reading Sign Uses

            int mod = 0;

            out.times = new HashSet<>();
            out.uses = new HashSet<>();

            try {
                UltimateSigns.i.lastReset = Long.parseLong(reader.readLine());
            } catch(NumberFormatException e) {
                UltimateSigns.i.lastReset = System.currentTimeMillis(); // Fix update
            }

            while((line = reader.readLine()) != null) {
                if(line.isEmpty()) continue;

                if(line.equals("----")) {
                    mod++;
                    continue;
                }
                switch(mod) {
                    case 0:
                        String[] pSplit = line.split("//");
                        String[] split = pSplit[0].split(",,");
                        if (split.length < 5) continue;

                        Location l = new Location(Bukkit.getWorld(UUID.fromString(split[0].trim())), Integer.parseInt(split[1].trim()), Integer.parseInt(split[2].trim()), Integer.parseInt(split[3].trim()));
                        UUID owner = UUID.fromString(split[4].trim());

                        List<String> commands = new ArrayList<>();
                        if (split.length > 5) {
                            for (int i = 5; i < split.length; i++) {
                                commands.add(split[i].trim());
                            }
                        }

                        List<String> permissions = new ArrayList<>();
                        if (pSplit.length > 1) {
                            String[] permissionSplit = pSplit[1].split(",,");
                            if (permissionSplit.length != 0) {
                                for (String s : permissionSplit) {
                                    permissions.add(s.trim());
                                }
                            }
                        }

                        if (l.getWorld().getBlockAt(l) != null) if (Constants.isSign(l.getWorld().getBlockAt(l).getType()))
                            signs.add(new USign(l, commands, permissions, owner));
                        break;
                    case 1:
                        String[] split1 = line.split(",,");

                        if(split1.length != 6) {
                            new InvalidDataFileException().printStackTrace();
                            break;
                        }
                        Location loc = new Location(Bukkit.getWorld(UUID.fromString(split1[1].trim())), Integer.parseInt(split1[2].trim()), Integer.parseInt(split1[3].trim()), Integer.parseInt(split1[4].trim()));
                        UUID player = UUID.fromString(split1[0].trim());

                        long t = Long.parseLong(split1[5].trim());

                        out.times.add(new SignTime(player, loc, t));

                        break;
                    case 2:
                        split1 = line.split(",,");

                        if(split1.length != 6) {
                            new InvalidDataFileException().printStackTrace();
                            break;
                        }
                        loc = new Location(Bukkit.getWorld(UUID.fromString(split1[1].trim())), Integer.parseInt(split1[2].trim()), Integer.parseInt(split1[3].trim()), Integer.parseInt(split1[4].trim()));
                        player = UUID.fromString(split1[0].trim());

                        t = Long.parseLong(split1[5].trim());

                        out.uses.add(new SignUses(player, loc, (int) t));
                        break;
                }
            }

            out.signs = signs;

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out;
    }

}
