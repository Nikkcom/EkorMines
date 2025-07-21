package me.ekorn.EkorMines.mine.persistence.serializer;

import me.ekorn.EkorMines.common.storage.SectionSerializer;
import me.ekorn.EkorMines.mine.domain.Mine;
import me.ekorn.EkorMines.mine.domain.MineMetadata;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class MineMetadataSerializer implements SectionSerializer<Mine> {
    @Override
    public String getSectionName() {
        return "mine-config";
    }

    @Override
    public void write(ConfigurationSection section, Mine value) {
        MineMetadata cfg = value.getConfig();
        Location spawn = cfg.getSpawnLocation();
        String locStr = String.format("%s;%f;%f;%f",
                spawn.getWorld().getName(),
                spawn.getX(), spawn.getY(), spawn.getZ());
        section.set("spawn", locStr);
        section.set("displayItem", cfg.getDisplayItem().name());
        section.set("resetBlocksPerTick", cfg.getResetBlocksPerTicks());
    }

    @Override
    public void read(ConfigurationSection section, Mine value) {
        // spawn
        if (!section.isString("spawn")) {
            throw new IllegalArgumentException("mine-config.spawn missing or not a string");
        }
        String rawSpawn = section.getString("spawn");
        if (rawSpawn == null) {
            throw new IllegalArgumentException("mine-config.spawn is missing");
        }
        String[] parts = rawSpawn.split(";");
        if (parts.length != 4) {
            throw new IllegalArgumentException("mine-config.spawn must be 'world;x;y;z'");
        }
        var world = Bukkit.getWorld(parts[0]);
        if (world == null) {
            throw new IllegalArgumentException("Unknown world in mine-config.spawn: " + parts[0]);
        }
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double z = Double.parseDouble(parts[3]);
        Location spawn = new Location(world, x, y, z);

        // displayItem
        if (!section.isString("displayItem")) {
            throw new IllegalArgumentException("mine-config.displayItem missing or not a string");
        }
        String rawItem = section.getString("displayItem");
        if (rawItem == null) {
            throw new IllegalArgumentException("mine-config.displayItem is missing");
        }
        Material displayItem;
        try {
            displayItem = Material.valueOf(rawItem);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid displayItem in mine-config: " + rawItem, ex);
        }

        // resetBlocksPerTick
        if (!section.isInt("resetBlocksPerTick")) {
            throw new IllegalArgumentException("mine-config.resetBlocksPerTick missing or not an integer");
        }
        int resetBlocksPerTick = section.getInt("resetBlocksPerTick");

        value.setConfig(new MineMetadata(spawn, displayItem, resetBlocksPerTick));
    }
}