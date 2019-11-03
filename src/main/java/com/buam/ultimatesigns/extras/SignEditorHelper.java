package com.buam.ultimatesigns.extras;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.buam.ultimatesigns.Constants;
import com.buam.ultimatesigns.UltimateSigns;
import io.netty.util.internal.ConcurrentSet;
import jdk.nashorn.internal.objects.Global;
import jdk.nashorn.internal.parser.JSONParser;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.json.simple.JSONObject;


public class SignEditorHelper {
    protected ProtocolManager protocolManager;
    protected PacketAdapter packetListener;
    protected Map<String, SignGUIListener> listeners;
    protected Map<String, Vector> signLocations;
    protected Set<UUID> expectsSignUpdate;

    public SignEditorHelper(Plugin plugin) {
        protocolManager = ProtocolLibrary.getProtocolManager();
        listeners = new ConcurrentHashMap<String, SignGUIListener>();
        signLocations = new ConcurrentHashMap<String, Vector>();
        expectsSignUpdate = new ConcurrentSet<UUID>();

        ProtocolLibrary.getProtocolManager().addPacketListener(
                packetListener =  new PacketAdapter(plugin, PacketType.Play.Client.UPDATE_SIGN) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        // ONLY WORKS IN 1.12 - 1.14!
                        final Player player = event.getPlayer();
                        if(!expectsSignUpdate.contains(player.getUniqueId())) return;
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
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                public void run() {
                                    response.onSignDone(player, lines);
                                }
                            });
                        }
                    }
                }
        );
    }

    private String convertJSON(String in) {
        JSONParser parser = new JSONParser(in, Global.instance(), false);
        JSONObject o = (JSONObject) parser.parse();

        String text = (String) o.get("text");
        String extra = (String) o.get("extra");

        if(extra.equalsIgnoreCase("")) {
            return text;
        } else if(text.equalsIgnoreCase("")) {
            return extra;
        }
        return "";
    }


    public void open(Player player, Location sign, String[] defaultText, SignGUIListener response) {
        List<PacketContainer> packets = new ArrayList<PacketContainer>();
        int x = sign.getBlockX();
        int y = sign.getBlockY();
        int z = sign.getBlockZ();
        BlockPosition bpos = new BlockPosition(x, y, z);
        PacketContainer packet133 = protocolManager.createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR);

        if (defaultText != null) {
            PacketContainer packet53 = protocolManager.createPacket(PacketType.Play.Server.BLOCK_CHANGE);
            PacketContainer packet130 = protocolManager.createPacket(PacketType.Play.Server.UPDATE_SIGN);
            WrappedBlockData iblock = WrappedBlockData.createData(Constants.getMaterial("LEGACY_SIGN_POST"));
            WrappedChatComponent[] cc = {WrappedChatComponent.fromText(defaultText[0]), WrappedChatComponent.fromText(defaultText[1]), WrappedChatComponent.fromText(defaultText[2]), WrappedChatComponent.fromText(defaultText[3])};

            packet53.getBlockPositionModifier().write(0, bpos);
            packet53.getBlockData().write(0, iblock);
            packet130.getBlockPositionModifier().write(0, bpos);
            packet130.getChatComponentArrays().write(0, cc);
            packets.add(packet53);
            packets.add(packet130);
        }

        packet133.getBlockPositionModifier().write(0, bpos);
        packets.add(packet133);

        if (defaultText != null) {
            PacketContainer packet53 = protocolManager.createPacket(PacketType.Play.Server.BLOCK_CHANGE);
            WrappedBlockData iblock = WrappedBlockData.createData(Material.BEDROCK);

            packet53.getBlockPositionModifier().write(0, bpos);
            packet53.getBlockData().write(0, iblock);
            packets.add(packet53);
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

    public void destroy() {
        protocolManager.removePacketListener(packetListener);
        listeners.clear();
        signLocations.clear();
    }

    public interface SignGUIListener {
        public void onSignDone(Player player, String[] lines);
    }
}