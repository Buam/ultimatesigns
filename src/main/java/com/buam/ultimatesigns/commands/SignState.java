package com.buam.ultimatesigns.commands;

import org.bukkit.block.Block;

public class SignState {

    public final Block block;
    public ChatStates state;
    public int index;

    public SignState(Block block, ChatStates state) {
        this.block = block;
        this.state = state;
    }

}
