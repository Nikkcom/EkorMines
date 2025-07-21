package me.ekorn.EkorMines.mine.persistence.serializer;

import me.ekorn.EkorMines.common.storage.SectionSerializer;
import me.ekorn.EkorMines.mine.domain.Mine;
import me.ekorn.EkorMines.mine.domain.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class RegionSerializer implements SectionSerializer<Mine> {
    @Override
    public String getSectionName() {
        return "region";
    }

    @Override
    public void write(ConfigurationSection section, Mine value) {
        Region region = value.getRegion();
        section.set("world", region.getWorld().getName());

        section.set("loc1", String.format("%d;%d;%d",
                region.getXMin(), region.getYMin(), region.getZMin()));

        section.set("loc2", String.format("%d;%d;%d",
                region.getXMax(), region.getYMax(), region.getZMax()));
    }

    @Override
    public void read(ConfigurationSection section, Mine value) {
        if (!section.isString("world")) {
            throw new IllegalArgumentException("region.world is missing or not a string");
        }
        String worldName = section.getString("world");

        if (worldName == null) {
            throw new IllegalArgumentException("region.world was reported as a string but is null");
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new IllegalArgumentException("Cannot find world '" + worldName + "'");
        }

        Location loc1 = parseCorner(section, "loc1", world);
        Location loc2 = parseCorner(section, "loc2", world);

        value.setRegion(new Region(loc1, loc2));

    }

    private Location parseCorner(ConfigurationSection section, String key, World world) {
        if (!section.isString(key)) {
            throw new IllegalArgumentException("region." +key+ " is missing or not a string");
        }
        String raw = section.getString(key);

        if (raw == null) {
            throw new IllegalArgumentException("region." +key+ " was reported as a string but is null");
        }

        String[] parts = raw.split(";");
        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    String.format("region.%s must be in 'x;y;z' format (got '%s')", key, raw)
            );
        }
        try {
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int z = Integer.parseInt(parts[2]);
            return new Location(world, x, y, z);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    String.format("region.%s contains non-integer values: '%s'", key, raw), ex);
        }
    }
}
