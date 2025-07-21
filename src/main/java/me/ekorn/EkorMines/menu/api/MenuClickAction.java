package me.ekorn.EkorMines.menu.api;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

@FunctionalInterface
public interface MenuClickAction {
    void onClick(Player player, ClickType clickType);
}
