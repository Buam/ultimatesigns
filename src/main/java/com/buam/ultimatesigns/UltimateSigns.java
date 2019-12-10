package com.buam.ultimatesigns;

import com.buam.ultimatesigns.commands.CommandUS;
import com.buam.ultimatesigns.config.Aliases;
import com.buam.ultimatesigns.config.Config;
import com.buam.ultimatesigns.config.Messages;
import com.buam.ultimatesigns.editor.SignEditorHelper;
import com.buam.ultimatesigns.events.SignListener;
import com.buam.ultimatesigns.extras.SignUpdater;
import com.buam.ultimatesigns.update.UpdateCheck;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class UltimateSigns extends JavaPlugin {
    /**
     * Prefix of the plugin. Used by some commands
     */
    public static final String PREFIX = ChatColor.GREEN + "Ultimate" + ChatColor.BLUE + "Signs" + ChatColor.GRAY + " >> " + ChatColor.WHITE;

    /**
     * Static reference to the plugins main class
     */
    public static UltimateSigns i;

    /**
     * Version of the server. (Use contains to check for certain versions
     */
    public static String version;

    /**
     * Static reference to the sign editor class
     */
    public static SignEditorHelper signEditor;

    /**
     * Economy instance. Is null if Vault could not be hooked or if there is no economy plugin
     */
    public static Economy economy;

    /**
     * Gets populated with players that should not receive any messages.
     * Used by the (silent) command modifier
     */
    public final Set<Player> messagesBlocked = new HashSet<>();

    /**
     * Static reference to the Main Command class (which handles all subcommands)
     */
    public static CommandUS command;

    /**
     * When the Sign Uses where last reset (milliseconds since 1/1/1970)
     */
    public long lastReset;

    /**
     * Gets called when the plugin is enabled, initializes all above variables
     * and adds a Packet Listener that listens for message packets that block the packet if
     * the player who it is send to is in the messagesBlocked Set
     * Also loads the config.yml and messages.yml files
     * Creates a new SignManager (that loads all the signs from the data.csv file)
     * Starts the sign update task
     * Checks for updates
     */
    public void onEnable() {
        i = this;
        version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new SignListener(), this);

        if(!setupEconomy()) {
            System.out.println("[UltimateSigns] No Vault dependency found!");
        } else {
            System.out.println("[UltimateSigns] Successfully hooked into Vault!");
        }

        if(getServer().getPluginManager().isPluginEnabled("ProtocolLib") && (version.contains("1_14") || version.contains("1_13") || version.contains("1_12"))) {
            signEditor = new SignEditorHelper(this);
            // Add (silent) Packet listener to silence commands from signs
            ProtocolLibrary.getProtocolManager().addPacketListener(
                    new PacketAdapter(this, PacketType.Play.Server.CHAT) {
                        @Override
                        public void onPacketSending(PacketEvent event) {
                            if (messagesBlocked.contains(event.getPlayer())) {
                                // Prevent it from ever being sent
                                event.setCancelled(true);
                            }
                        }
                    }
            );
        }

        command = new CommandUS(this);
        getCommand("ultimatesigns").setExecutor(command);

        // Load configuration
        new Config(getConfig());
        new Messages(this);
        new Aliases(this);

        // New SignManager with data path
        new SignManager(getDataFolder() + Constants.DATA_FILE);

        // Schedule Sign Update task
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, SignUpdater::updateAllSigns, 0, Config.i.i(Constants.SIGN_UPDATE_TIME));

        // Schedule Sign Uses Reset Task
        if(Config.i.i(Constants.SIGN_USES_RESET_TIME) != 0) Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> SignManager.i.resetSignUses(), Config.i.i(Constants.SIGN_USES_RESET_TIME), Config.i.i(Constants.SIGN_USES_RESET_TIME));

        // Create commands folder if it doesn't exist
        File commandsFolder = new File(getDataFolder() + Config.i.s(Constants.COMMANDS_SUBFOLDER));
        if(!commandsFolder.mkdirs()) System.out.println(ChatColor.RED + "[UltimateSigns] Failed to create commands subfolder");

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, this::checkForUpdates);
    }

    /**
     * Reloads the config.yml and messages.yml files
     * Restarts the sign update task (if it was cancelled by the server for whatever reason)
     * Saves all signs and creates a new SignManager (which loads them again)
     */
    public void reload() {
        // Reload configuration
        reloadConfig();
        new Config(getConfig());
        new Messages(this);
        new Aliases(this);

        // Re-register sign update task
        Bukkit.getScheduler().cancelTasks(this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, SignUpdater::updateAllSigns, 0, Config.i.i(Constants.SIGN_UPDATE_TIME));

        // Schedule Sign Uses Reset Task
        if(Config.i.i(Constants.SIGN_USES_RESET_TIME) != 0) Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> SignManager.i.resetSignUses(), 0, Constants.SIGN_USES_RESET_CHECK_INTERVAL);

        // Also reload signs (save and load them)
        SignManager.i.saveSigns();
        new SignManager(getDataFolder() + Constants.DATA_FILE);

        // Update all signs - just in case
        SignUpdater.updateAllSigns();
    }

    /**
     * Gets called when the plugin is disabled.
     * Saves all signs and destroys the sign editor
     */
    public void onDisable() {
        SignManager.i.saveSigns();
        signEditor.destroy();
        ProtocolLibrary.getProtocolManager().removePacketListeners(this);
    }

    /**
     * Hooks into Vault and gets the Economy plugin (if there is one)
     * @return If it succeeded
     */
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    /**
     * Update checker
     */
    private void checkForUpdates() {
        System.out.println("[UltimateSigns] Checking for updates...");
        UpdateCheck
                .of(this)
                .resourceId(72462)
                .currentVersion(getDescription().getVersion().toLowerCase().trim())
                .handleResponse(((versionResponse, version) ->  {
                    switch(versionResponse) {
                        case FOUND_NEW:
                            System.out.println("[UltimateSigns] New version found: " + version);
                            break;
                        case LATEST:
                            System.out.println("[UltimateSigns] No new version found");
                            break;
                        case UNAVAILABLE:
                            System.out.println("[UltimateSigns] Unable to perform an update check!");
                            break;
                    }
                })).check();
    }
}
