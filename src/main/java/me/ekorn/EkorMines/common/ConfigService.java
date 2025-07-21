package me.ekorn.EkorMines.common;

import me.ekorn.EkorMines.common.api.PluginService;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ConfigService implements PluginService {
    private final JavaPlugin plugin;
    private LoggingService log;
    private FileConfiguration config;

    // Config keys required in the config file.
    private static final List<String> REQUIRED_KEYS = List.of(
            "locale",
            "debug"
    );

    public ConfigService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init(PluginContext ctx) {
        this.log = ctx.services().get(LoggingService.class);
        loadAndApplyDefaults();
    }

    private void loadAndApplyDefaults() {
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        log.setDebugEnabled(isDebugMode());
        log.info("Configuration reloaded.");
        validateConfig();
    }

    private void validateConfig() {
        boolean anyMissing = false;
        for (String key : REQUIRED_KEYS) {
            if (!config.contains(key)) {
                log.warn("Missing config key: '" +key+ "'. Using default value.");
                anyMissing = true;
            }
        }

        if (anyMissing) {
            log.warn("Some configuration keys were missing from config.yml. Using fallback defaults.");
        }
    }

    public String getLocale() {
        return config.getString("locale", "en_us");
    }

    public boolean isDebugMode() {
        return config.getBoolean("debug", false);
    }
    public FileConfiguration getRawConfig() {
        return config;
    }
}
