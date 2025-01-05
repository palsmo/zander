package org.modularsoft.zander.hub.configs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import static org.modularsoft.zander.hub.utils.ConfigValidator.isValidDouble;
import static org.modularsoft.zander.hub.utils.ConfigValidator.isValidPitch;
import static org.modularsoft.zander.hub.utils.ConfigValidator.isValidWorld;
import static org.modularsoft.zander.hub.utils.ConfigValidator.isValidYaw;
import static org.modularsoft.zander.hub.utils.ConfigValidator.validateConfig;

/**
 * Manages hub locations for the plugin, and their persistence.
 * Handles loading, validation, and access to managed data.
 */
public class HubLocationsConfig {
    private final JavaPlugin plugin;

    private Location locationSpawn;
    // future? private Location locationParkour;

    public HubLocationsConfig(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /// Configure the spawn location, ensures valid entries in 'config.yml'
    public void setupSpawn() {
        FileConfiguration config = plugin.getConfig();

        // * access server's primary world (guaranteed by Bukkit to exist)
        Location defaultSpawn = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();

        validateConfig(config, "hub.world", isValidWorld, defaultSpawn.getWorld().getName());
        validateConfig(config, "hub.x", isValidDouble, defaultSpawn.getX());
        validateConfig(config, "hub.y", isValidDouble, defaultSpawn.getY());
        validateConfig(config, "hub.z", isValidDouble, defaultSpawn.getZ());
        validateConfig(config, "hub.pitch", isValidPitch, defaultSpawn.getPitch());
        validateConfig(config, "hub.yaw", isValidYaw, defaultSpawn.getYaw());

        plugin.saveConfig(); // * save to external 'config.yml'

        World hubWorld = Bukkit.getWorld(config.getString("hub.world"));
        double hubX = config.getDouble("hub.x");
        double hubY = config.getDouble("hub.y");
        double hubZ = config.getDouble("hub.z");
        float hubYaw = (float) config.getDouble("hub.yaw");
        float hubPitch = (float) config.getDouble("hub.pitch");
        this.locationSpawn = new Location(hubWorld, hubX, hubY, hubZ, hubYaw, hubPitch);
    }

    /// Retrieve the spawn location.
    public Location spawn() {
        if (this.locationSpawn == null)
            throw new IllegalStateException("Missing setup, first run 'HubLocationsConfig.setupSpawn'");
        return this.locationSpawn.clone();
    }
}
