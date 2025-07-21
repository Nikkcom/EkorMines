package me.ekorn.EkorMines.menu.api;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface MenuCloseAction {
    void onClose(Player player);
}
