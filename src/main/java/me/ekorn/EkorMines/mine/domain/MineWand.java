package me.ekorn.EkorMines.mine.domain;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class MineWand {
    private final NamespacedKey wandKey;
    private final ItemStack prototype;

    public MineWand(JavaPlugin plugin, String keyName, ItemStack prototype) {
        this.wandKey = new NamespacedKey(plugin, Objects.requireNonNull(keyName));
        this.prototype = Objects.requireNonNull(prototype.clone());
    }

    public ItemStack create() {
        ItemStack wand = prototype.clone();
        ItemMeta meta = Objects.requireNonNull(wand.getItemMeta());
        meta.getPersistentDataContainer()
                .set(wandKey, PersistentDataType.BYTE, (byte) 1);
        wand.setItemMeta(meta);
        return wand;
    }

    public boolean isWand(ItemStack item) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer()
                .has(wandKey, PersistentDataType.BYTE);
    }
}
