package com.buam.ultimatesigns;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SignTime {

    private long millisTime;
    private USign sign;
    private UUID player;

    public SignTime(Player p, USign sign) {
        millisTime = System.currentTimeMillis();
        this.sign = sign;
        this.player = p.getUniqueId();
    }

    public SignTime(Player p, USign sign, long time) {
        millisTime = time;
        this.sign = sign;
        this.player = p.getUniqueId();
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
            if(((SignTime) other).getSign().getLocation().equals(getSign().getLocation()) && ((SignTime) other).getPlayerID().equals(getPlayerID())) {
                return true;
            }
        }
        return false;
    }
}
