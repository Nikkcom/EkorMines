package me.ekorn.EkorMines.menu.api;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface MenuOpenAction {
    void onOpen(Player player);
}
