package com.buam.ultimatesigns;

import org.bukkit.Location;

import java.util.UUID;

public class SignUses {

    private final UUID player;
    private Integer uses;
    private final Location location;

    public SignUses(UUID player, Location l, Integer i) {
        this.player = player;
        this.location = l;
        this.uses = i;
    }

    public void inc() {
        uses++;
    }

    public void reset() {
        uses = 0;
    }

    public int getUses() {
        return uses;
    }

    public UUID getPlayer() {
        return player;
    }

    public Location getSign() {
        return location;
    }

}
