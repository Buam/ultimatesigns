package com.buam.ultimatesigns.update;

import com.google.common.base.Preconditions;
import com.google.common.io.Resources;
import com.google.common.net.HttpHeaders;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.function.BiConsumer;

public class UpdateCheck {
    /**
     * The spigot URL to get updates from
     */
    private static final String SPIGOT_URL = "https://api.spigotmc.org/legacy/update.php?resource=%d";

    /**
     * A private reference to the plugin
     */
    private final JavaPlugin javaPlugin;

    /**
     * The current version of the plugin
     */
    private String currentVersion;

    /**
     * The resource if of the plugin
     */
    private int resourceId = -1;

    /**
     * The response of the version, whether there is a new version or not
     */
    private BiConsumer<VersionResponse, String> versionResponse;

    private UpdateCheck(JavaPlugin javaPlugin) {
        this.javaPlugin = Objects.requireNonNull(javaPlugin, "javaPlugin");
        this.currentVersion = javaPlugin.getDescription().getVersion();
    }

    public static UpdateCheck of(JavaPlugin javaPlugin) {
        return new UpdateCheck(javaPlugin);
    }

    public UpdateCheck currentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
        return this;
    }

    public UpdateCheck resourceId(int resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    public UpdateCheck handleResponse(BiConsumer<VersionResponse, String> versionResponse) {
        this.versionResponse = versionResponse;
        return this;
    }

    /**
     * Checks for a new version
     */
    public void check() {
        Objects.requireNonNull(this.javaPlugin, "javaPlugin");
        Objects.requireNonNull(this.currentVersion, "currentVersion");
        Preconditions.checkState(this.resourceId != -1, "resource id not set");
        Objects.requireNonNull(this.versionResponse, "versionResponse");

        Bukkit.getScheduler().runTaskAsynchronously(this.javaPlugin, () -> {
            try {
                HttpURLConnection httpURLConnection = (HttpsURLConnection) new URL(String.format(SPIGOT_URL, this.resourceId)).openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty(HttpHeaders.USER_AGENT, "Mozilla/5.0");

                @SuppressWarnings("UnstableApiUsage") String fetchedVersion = Resources.toString(httpURLConnection.getURL(), Charset.defaultCharset());

                boolean latestVersion = fetchedVersion.equalsIgnoreCase(this.currentVersion);

                Bukkit.getScheduler().runTask(this.javaPlugin, () -> this.versionResponse.accept(latestVersion ? VersionResponse.LATEST : VersionResponse.FOUND_NEW, latestVersion ? this.currentVersion : fetchedVersion));
            } catch (IOException exception) {
                exception.printStackTrace();
                Bukkit.getScheduler().runTask(this.javaPlugin, () -> this.versionResponse.accept(VersionResponse.UNAVAILABLE, null));
            }
        });
    }
}