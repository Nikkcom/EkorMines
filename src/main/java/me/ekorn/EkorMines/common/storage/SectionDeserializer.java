package me.ekorn.EkorMines.common.storage;

import org.bukkit.configuration.ConfigurationSection;

public interface SectionDeserializer<T> {

    String getSectionName();

    T deserialize(ConfigurationSection section);
}
