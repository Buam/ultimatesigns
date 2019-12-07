package com.buam.ultimatesigns.lang.variables;

import com.buam.ultimatesigns.lang.types.Boolean;
import org.bukkit.block.Block;

public class SignRedstonePowered extends Boolean {
    @Override
    public boolean get(Object... args) {
        return ((Block) args[1]).isBlockIndirectlyPowered();
    }

    @Override
    public String a() {
        return "[power]";
    }
}
