package me.ekorn.EkorMines.mine.persistence.serializer;

import me.ekorn.EkorMines.common.storage.SectionSerializer;
import me.ekorn.EkorMines.mine.domain.BlockOrdering;
import me.ekorn.EkorMines.mine.domain.Mine;
import me.ekorn.EkorMines.mine.domain.Region;
import me.ekorn.EkorMines.mine.domain.ResetDirection;
import org.bukkit.configuration.ConfigurationSection;

public class BlockOrderingSerializer implements SectionSerializer<Mine> {

    @Override
    public String getSectionName() {
        return "region-sequence";
    }

    @Override
    public void write(ConfigurationSection section, Mine value) {
        BlockOrdering cache = value.getCache();
        section.set("direction", cache.getDirection().name());
    }

    @Override
    public void read(ConfigurationSection section, Mine value) {
        if (!section.isString("direction")) {
            throw new IllegalArgumentException("cache.direction is missing or not a string");
        }
        String raw = section.getString("direction");
        ResetDirection dir;
        try {
            dir = ResetDirection.valueOf(raw);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid cache.direction: " +raw, ex);
        }
        Region region = value.getRegion();
        if (region == null) {
            throw new IllegalStateException("Region must be set before cache");
        }
        value.setCache(new BlockOrdering(region, dir));
    }
}
