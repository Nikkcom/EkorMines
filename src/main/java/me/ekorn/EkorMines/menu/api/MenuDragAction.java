package me.ekorn.EkorMines.menu.api;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;

@FunctionalInterface
public interface MenuDragAction {
    void onDrag(Player player, InventoryDragEvent event);
}
