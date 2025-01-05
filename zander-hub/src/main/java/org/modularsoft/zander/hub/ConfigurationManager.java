package org.modularsoft.zander.hub;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.modularsoft.zander.hub.configs.MessagesConfig;
import org.modularsoft.zander.hub.configs.HubLocationsConfig;

public final class ConfigurationManager {
    private static FileConfiguration welcomeFile;
    private static HubLocationsConfig hubLocationsConfig;
    private static MessagesConfig messagesConfig;

    private ConfigurationManager() {
        throw new IllegalStateException("Utility class shouldn't be instantiated");
    }

    public static void setupHubLocations() {
        if (hubLocationsConfig != null)
            throw new IllegalStateException("Already setup, ensure there's a single call");
        hubLocationsConfig = new HubLocationsConfig(ZanderHubMain.plugin);
        hubLocationsConfig.setupSpawn();
        // future? hubLocationsConfig.setupParkour();
    }

    public static void setupMessages() {
        if (messagesConfig != null)
            throw new IllegalStateException("Already setup, ensure there's a single call");
        messagesConfig = new MessagesConfig(ZanderHubMain.plugin);
        messagesConfig.setupJoinLeave();
    }

    public static void setupWelcomeFile() {
        if (welcomeFile != null)
            throw new IllegalStateException("Already setup, ensure there's a single call");
        File dataFolder = ZanderHubMain.plugin.getDataFolder();
        File welcomeFileYML = new File(dataFolder, "welcome.yml");
        if (!welcomeFileYML.exists())
            ZanderHubMain.plugin.saveResource("welcome.yml", false);

        ConfigurationManager.welcomeFile = YamlConfiguration.loadConfiguration(welcomeFileYML);
    }

    public static HubLocationsConfig getHubLocations() {
        if (hubLocationsConfig == null)
            throw new IllegalStateException("Missing setup, first run 'ConfigurationManager.setupHubLocations'");
        return hubLocationsConfig;
    }

    public static MessagesConfig getMessages() {
        if (messagesConfig == null)
            throw new IllegalStateException("Missing setup, first run 'ConfigurationManager.setupMessages'");
        return messagesConfig;
    }

    public static FileConfiguration getWelcome() {
        if (welcomeFile == null)
            throw new IllegalStateException("Missing setup, first run 'ConfigurationManager.setupWelcomeFile'");
        return welcomeFile;
    }
}
