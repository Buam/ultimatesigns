package com.buam.ultimatesigns;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SignTime {

    private long millisTime;
    private Location sign;
    private UUID player;

    public SignTime(UUID p, Location sign) {
        millisTime = System.currentTimeMillis();
        this.sign = sign;
        this.player = p;
    }

    public SignTime(UUID p, Location sign, long time) {
        millisTime = time;
        this.sign = sign;
        this.player = p;
    }

    public void now() {
        millisTime = System.currentTimeMillis();
    }

    public long difference(long time) {
        return Math.abs(time - millisTime) / 1000;
    }

    public long difference() {
        return difference(System.currentTimeMillis());
    }

    public USign getSign() {
        return SignManager.i.signAt(sign);
    }

    public Location getLocation() {
        return sign;
    }

    public long getMillisTime() {
        return millisTime;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(player);
    }

    public UUID getPlayerID() {
        return player;
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof SignTime) {
            if(((SignTime) other).getLocation().equals(getLocation()) && ((SignTime) other).getPlayerID().equals(getPlayerID())) {
                return true;
            }
        }
        return false;
    }
}
