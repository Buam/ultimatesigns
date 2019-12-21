package com.buam.ultimatesigns.editor;

import com.buam.ultimatesigns.SharedConstants;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class SignEditorHelper {
    /**
     * The protocol manager of ProtocolLib
     */
    private final ProtocolManager protocolManager;

    /**
     * All listeners currently active
     */
    private final Map<String, SignGUIListener> listeners;

    /**
     * All the sign locations that are being edited
     */
    private final Map<String, Vector> signLocations;

    /**
     * A set of players who expect a sign update (because they opened the editor)
     * The SIGN_UPDATE packets of these players will be caught and modified
     */
    private final Set<UUID> expectsSignUpdate;

    /**
     * Sets up the packet listener and all variables
     * @param plugin The plugin
     */
    public SignEditorHelper(Plugin plugin) {
        protocolManager = ProtocolLibrary.getProtocolManager();
        listeners = new ConcurrentHashMap<>();
        signLocations = new ConcurrentHashMap<>();
        expectsSignUpdate = new HashSet<>();

        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(plugin, PacketType.Play.Client.UPDATE_SIGN) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        // ONLY WORKS IN 1.12 - 1.14!
                        final Player player = event.getPlayer();
                        if (!expectsSignUpdate.contains(player.getUniqueId())) return;
                        expectsSignUpdate.remove(player.getUniqueId());
                        Vector v = signLocations.remove(player.getName());
                        BlockPosition bp = event.getPacket().getBlockPositionModifier().getValues().get(0);
                        final String[] lines = event.getPacket().getStringArrays().read(0);
                        final SignGUIListener response = listeners.remove(event.getPlayer().getName());
                        if (v == null) return;
                        if (bp.getX() != v.getBlockX()) return;
                        if (bp.getY() != v.getBlockY()) return;
                        if (bp.getZ() != v.getBlockZ()) return;
                        if (response != null) {
                            event.setCancelled(true);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> response.onSignDone(player, lines));
                        }
                    }
                }
        );

    }

    /**
     * Opens the sign editor for a player
     * @param player The player
     * @param sign The sign
     * @param defaultText The default text which should be displayed in the editor (the original sign text)
     * @param response The sign listener which will handle the closing of the editor and set the signs lines to the right value
     */
    //@SuppressWarnings("deprecation")
    public void open(Player player, Location sign, String[] defaultText, SignGUIListener response) {
        List<PacketContainer> packets = new ArrayList<>();
        int x = sign.getBlockX();
        int y = sign.getBlockY();
        int z = sign.getBlockZ();
        BlockPosition bpos = new BlockPosition(x, y, z);
        PacketContainer signEditorPacket = protocolManager.createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR);

        if (defaultText != null) {
            PacketContainer blockChangePacket = protocolManager.createPacket(PacketType.Play.Server.BLOCK_CHANGE);
            PacketContainer signUpdatePacket = protocolManager.createPacket(PacketType.Play.Server.UPDATE_SIGN);
            WrappedBlockData iblock = WrappedBlockData.createData(SharedConstants.getMaterial("LEGACY_SIGN_POST"));
            WrappedChatComponent[] cc = {WrappedChatComponent.fromText(defaultText[0]), WrappedChatComponent.fromText(defaultText[1]), WrappedChatComponent.fromText(defaultText[2]), WrappedChatComponent.fromText(defaultText[3])};

            blockChangePacket.getBlockPositionModifier().write(0, bpos);
            blockChangePacket.getBlockData().write(0, iblock);
            signUpdatePacket.getBlockPositionModifier().write(0, bpos);
            signUpdatePacket.getChatComponentArrays().write(0, cc);
            packets.add(blockChangePacket);
            packets.add(signUpdatePacket);
        }

        signEditorPacket.getBlockPositionModifier().write(0, bpos);
        packets.add(signEditorPacket);

        if (defaultText != null) {
            PacketContainer blockChangePacket = protocolManager.createPacket(PacketType.Play.Server.BLOCK_CHANGE);
            WrappedBlockData iblock = WrappedBlockData.createData(Material.BEDROCK);

            blockChangePacket.getBlockPositionModifier().write(0, bpos);
            blockChangePacket.getBlockData().write(0, iblock);
            packets.add(blockChangePacket);
        }

        try {
            for (PacketContainer packet : packets) {
                protocolManager.sendServerPacket(player, packet);
            }
            expectsSignUpdate.add(player.getUniqueId());
            signLocations.put(player.getName(), new Vector(x, y, z));
            listeners.put(player.getName(), response);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    /**
     * Clean up everything and remove the packet listener
     */
    public void destroy() {
        listeners.clear();
        signLocations.clear();
    }

    /**
     * The sign listener interface
     */
    @FunctionalInterface
    public interface SignGUIListener {
        void onSignDone(Player player, String[] lines);
    }
}