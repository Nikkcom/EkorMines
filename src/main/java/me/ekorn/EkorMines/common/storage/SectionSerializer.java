package me.ekorn.EkorMines.common.storage;

import org.bukkit.configuration.ConfigurationSection;

public interface SectionSerializer<V> {
    String getSectionName();
    void write(ConfigurationSection section, V value);
    void read(ConfigurationSection section, V value);
}
