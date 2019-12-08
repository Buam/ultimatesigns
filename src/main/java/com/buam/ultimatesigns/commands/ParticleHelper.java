package com.buam.ultimatesigns.commands;

import org.bukkit.Effect;
import org.bukkit.Location;

public class ParticleHelper {

    public static void p(Location l) {
        l.getWorld().playEffect(l, Effect.SMOKE, 1);
    }

}
