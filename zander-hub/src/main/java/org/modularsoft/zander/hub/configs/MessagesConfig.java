package org.modularsoft.zander.hub.configs;

import de.myzelyam.api.vanish.VanishAPI;
import java.io.File;
import java.io.IOException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import static org.modularsoft.zander.hub.utils.ConfigValidator.isValidJoinLeave;
import static org.modularsoft.zander.hub.utils.ConfigValidator.validateConfig;

/**
 * Manages server messages for the plugin, and their persistence.
 * Handles loading, validation, and uniformity of managed data.
 */
public class MessagesConfig {
    private final JavaPlugin plugin;

    private TextComponent textCompJoinTemplate;
    private TextComponent textCompLeaveTemplate;

    public MessagesConfig(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /// Configure the join/leave messages, ensures valid entries in 'config.yml'
    public void setupJoinLeave() {
        FileConfiguration config = plugin.getConfig();

        String defaultJoin = "&7%p% <default join message>";
        String defaultLeave = "&7%p% <default leave message>";

        validateConfig(config, "messages.join", isValidJoinLeave, defaultJoin,
                template -> this.textCompJoinTemplate = template);
        validateConfig(config, "messages.leave", isValidJoinLeave, defaultLeave,
                template -> this.textCompLeaveTemplate = template);

        plugin.saveConfig(); // * save to external 'config.yml'

        String textLegacyJoin = config.getString("messages.join");
        String textLegacyLeave = config.getString("messages.leave");

        if (this.plugin.getServer().getPluginManager().getPlugin("PremiumVanish") != null) {
            updatePremiumVanish(textLegacyJoin, textLegacyLeave);
            VanishAPI.reloadConfig();
        }
    }

    /// Retrieve join message for `playerName`
    public Component playerJoin(Component playerName) {
        return insertPlayerName(this.textCompJoinTemplate, playerName);
    }

    /// Retrieve leave message for `playerName`
    public Component playerLeave(Component playerName) {
        return insertPlayerName(this.textCompLeaveTemplate, playerName);
    }

    /// Insert component `playerName` into `template`
    private Component insertPlayerName(TextComponent template, Component playerName) {
        if (template == null)
            throw new IllegalStateException("Missing setup, first run 'MessagesConfig.setupJoinLeave'");
        return template.replaceText(builder -> builder
                .match("%p%") // * 'parseToTemplate' ensured single %p%
                .replacement(playerName)); // * style is handled as expected
    }

    /// Replace join/leave messages in PremiumVanish 'messages.yml' file.
    private void updatePremiumVanish(String textLegacyJoin, String textLegacyLeave) {
        File pvYML = new File(this.plugin.getDataFolder().getParentFile(), "PremiumVanish/messages.yml");
        if (pvYML.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(pvYML);
            config.set("Messages.DiscordSRVFakeJoin", textLegacyJoin);
            config.set("Messages.DiscordSRVFakeQuit", textLegacyLeave);
            config.set("Messages.ReappearMessage", textLegacyJoin);
            config.set("Messages.VanishMessage", textLegacyLeave);
            try {
                config.save(pvYML);
            } catch (IOException e) {
                this.plugin.getLogger().warning("Failed to update PremiumVanish messages.yml");
            }
        }
    }
}
