package me.ekorn.EkorMines.menu.domain;

import me.ekorn.EkorMines.menu.api.MenuClickAction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a single entry in a Menu, consisting of an ItemStack rendered at a specific slot
 * and a set of click‐type–to–action mappings that determine what happens when the player clicks it.
 */
public class MenuItem {

    private final ItemStack item;
    private final MenuClickAction defaultAction;
    private final Map<ClickType, MenuClickAction> overrides = new HashMap<>();

    /**
     * Constructs a new MenuItem.
     *
     * @param item the ItemStack to render in the menu
     */
    public MenuItem(ItemStack item, MenuClickAction defaultAction) {
        this.item = Objects.requireNonNull(item, "item");
        this.defaultAction = Objects.requireNonNull(defaultAction);
    }

    private MenuItem(ItemStack item, MenuClickAction defaultAction, Map<ClickType, MenuClickAction> overrides) {
        this.item = item;
        this.defaultAction = defaultAction;
        this.overrides.putAll(overrides);
    }


    /**
     * Registers a click handler for a specific type of click.
     *
     * @param type   the ClickType (e.g. LEFT, RIGHT, SHIFT_LEFT) to listen for
     * @param action the action to invoke when the player performs that click on this item
     */
    public void setOverride(ClickType type, MenuClickAction action) {
        overrides.put(type, action);
    }

    /**
     * Handles a click event by looking up the registered action for the given click type
     * and invoking it. If no action is registered for that type, this method does nothing.
     *
     * @param player the player who clicked the item
     * @param type   the type of click performed
     */
    public void handleClick(Player player, ClickType type) {
        MenuClickAction action = overrides.getOrDefault(type, defaultAction);
        action.onClick(player, type);

    }

    /**
     * Returns the ItemStack that this MenuItem displays.
     *
     * @return the ItemStack for this menu entry
     */
    public ItemStack getItem() {
        return item;
    }
}