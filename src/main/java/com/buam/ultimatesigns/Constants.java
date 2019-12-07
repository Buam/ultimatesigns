package com.buam.ultimatesigns;

import org.bukkit.Bukkit;
import org.bukkit.Material;

public class Constants {

    public static final String DATA_FILE = "/data.csv";

    // (ticks)
    public static final long SIGN_USES_RESET_CHECK_INTERVAL = 200;

    // CONFIG
    public static final String COMMANDS_SUBFOLDER = "commands-folder";
    public static final String ENABLE_UPDATE_PERMISSION = "enable-update-permission";
    public static final String SIGN_UPDATE_TIME = "sign-update-interval";
    public static final String SIGN_USES_RESET_TIME = "sign-uses-reset-interval";
    ////////////

    // PERMISSIONS
    public static final String UPDATE_PERMISSION = "ultimatesigns.update";
    public static final String COMMAND_PERMISSION = "ultimatesigns.cmd.commands";
    public static final String RELOAD_PERMISSION = "ultimatesigns.cmd.reload";
    public static final String EDIT_PERMISSION = "ultimatesigns.cmd.edit";
    public static final String PERMISSION_PERMISSION = "ultimatesigns.cmd.permission";
    public static final String COPY_PERMISSION = "ultimatesigns.cmd.copy";
    public static final String BREAK_PERMISSION = "ultimatesigns.break";
    ////////////

    public static boolean isSign(Material m) {
        return m.toString().contains("SIGN");
    }

    public static Material getMaterial(String mat) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        if(!version.contains("v1_13") && !version.contains("v1_14")) {
            if(mat.equals("LEGACY_SIGN_POST")) {
                return Material.getMaterial("SIGN_POST");
            }
        }

        return Material.getMaterial(mat);
    }



}
