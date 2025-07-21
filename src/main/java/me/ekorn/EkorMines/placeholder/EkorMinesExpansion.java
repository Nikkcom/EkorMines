package me.ekorn.EkorMines.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.ekorn.EkorMines.common.LoggingService;
import me.ekorn.EkorMines.mine.domain.Mine;
import me.ekorn.EkorMines.mine.service.MineManager;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;
import java.util.stream.Collectors;

public class EkorMinesExpansion extends PlaceholderExpansion {
    private final JavaPlugin plugin;
    private final MineManager mineManager;
    private final LoggingService log;

    public EkorMinesExpansion(JavaPlugin plugin, MineManager mineManager, LoggingService loggingService) {
        this.plugin = plugin;
        this.mineManager = mineManager;
        this.log = loggingService;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ekormines";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Ekorn";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        String[] parts = params.split("_", 2);
        String category = parts[0].toLowerCase();
        String remainder = parts.length > 1 ? parts[1] : "";
        log.debug(String.format("Parsing Placeholder '%s', category=%s, remainder=%s", params, category, remainder));
        String result;

        switch (category) {
            case "version" -> {
                result = plugin.getPluginMeta().getVersion();
                log.debug("version → " + result);
                return result;
            }
            case "mine" -> {
                String[] mineParts = remainder.split("_", 2);
                if (mineParts.length < 2) {
                    log.warn("PAPI", "Invalid 'mine' placeholder format: '" +remainder+ "'");
                    return null;
                }

                String mineId = mineParts[0].toLowerCase();
                String field = mineParts[1].toLowerCase();

                result = handleMinePlaceholder(mineId, field);

                if (result == null) {
                    log.warn("PAPI", "handleMinePlaceholder returned null for " +
                            mineId + " / " + field);
                } else {
                    log.info("[PAPI]    handleMinePlaceholder → " + result);
                }
                return result;
            }

            default -> {
                log.warn("[PAPI]  unknown category: '" + category + "'");
                return null;
            }
        }
    }

    private String handleMinePlaceholder(String mineId, String field) {



        var optMine = mineManager.get(mineId);
        if (optMine.isEmpty()) {
            log.warn("[PAPI]  → No mine found with ID '" + mineId + "'");
            return null;
        }

        Mine mine = optMine.get();
        String result;

        return switch(field) {
            case "interval" -> String.valueOf(mine.getPolicy().getResetIntervalSecs());
            case "oninterval" -> String.valueOf(mine.getPolicy().isResetOnInterval());
            case "oninterval_label" -> mine.getPolicy().isResetOnInterval() ? "ON" : "OFF";
            case "threshold" -> String.valueOf(mine.getPolicy().getThresholdPct());
            case "onthreshold" -> String.valueOf(mine.getPolicy().isResetOnThreshold());
            case "onthreshold_label" -> mine.getPolicy().isResetOnThreshold() ? "ON" : "OFF";
            case "direction" -> mine.getCache().getDirection().name();
            case "spawn_x" -> String.valueOf((int) mine.getSpawnLocation().getX());
            case "spawn_y" -> String.valueOf((int) mine.getSpawnLocation().getY());
            case "spawn_z" -> String.valueOf((int) mine.getSpawnLocation().getZ());
            case "spawn_coordinates" -> String.format("%d,%d,%d",
                    (int) mine.getSpawnLocation().getX(),
                    (int) mine.getSpawnLocation().getY(),
                    (int) mine.getSpawnLocation().getZ());
            case "spawn_pitch" -> String.valueOf(mine.getSpawnLocation().getPitch());
            case "spawn_yaw" -> String.valueOf(mine.getSpawnLocation().getYaw());
            case "spawn_world" -> mine.getSpawnLocation().getWorld().getName();
            case "display_item" -> mine.getDisplayItem().name();
            case "total_blocks" -> String.valueOf(mine.getRegion().getTotalBlockCount());
            case "block_types_count" -> String.valueOf(mine.getPicker().getWeights().size());
            case "block_types_list" -> mine.getPicker().getWeights().keySet().stream()
                    .map(Material::name).collect(Collectors.joining(","));
            case "reset_blocks_per_tick" -> String.valueOf(mine.getConfig().getResetBlocksPerTicks());
            case "manual_cooldown" -> String.valueOf(mine.getPolicy().getManualResetCooldownSecs());
            case "name" -> mine.getId();
            case "allow_manual_reset" -> String.valueOf(mine.getPolicy().isAllowManualReset());


            default -> null;
        };
    }
}
