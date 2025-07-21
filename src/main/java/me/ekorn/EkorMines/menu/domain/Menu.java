package me.ekorn.EkorMines.menu.domain;

import me.ekorn.EkorMines.menu.api.MenuClickAction;
import me.ekorn.EkorMines.menu.api.MenuCloseAction;
import me.ekorn.EkorMines.menu.api.MenuDragAction;
import me.ekorn.EkorMines.menu.api.MenuOpenAction;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;

/**
 * Abstract base for all menus.
 * <p>
 * Manages a per‐player session, underlying Inventory, item layout,
 * and dispatch of open/close, click, and drag events.
 * </p>
 */
public abstract class Menu {

    private final UUID sessionId;
    private final String key;
    private final Inventory inventory;
    private final Map<Integer, MenuItem> items = new HashMap<>();

    private MenuClickAction generalMenuClick;
    private MenuClickAction generalPlayerClick;
    private MenuOpenAction openAction;
    private MenuCloseAction closeAction;
    private MenuDragAction dragAction;

    /**
     * Constructs a new menu template.
     *
     * @param key   unique identifier for this menu type
     * @param rows  the number of rows in the inventory
     * @param title the title component shown at the top of the inventory
     */
    public Menu(String key, int rows, Component title) {
        this.key = key;
        this.sessionId = UUID.randomUUID();
        this.inventory = Bukkit.createInventory(null, rows * 9, title);
    }

    /**
     * Returns the unique key identifying this menu type.
     *
     * @return the menu key
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the unique session identifier for this menu instance.
     *
     * @return the UUID assigned to this menu session
     */
    public UUID getSessionId() {
        return sessionId;
    }

    /**
     * Returns the underlying Bukkit Inventory instance.
     *
     * @return the inventory backing this menu
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Returns the size (slot count) of this menu’s inventory.
     *
     * @return inventory size
     */
    public int getSize() {
        return inventory.getSize();
    }

    public int getRows() {
        return getSize() / 9;
    }

    /**
     * Opens this menu for the given player, rebuilding its contents
     * and invoking the open hook if one is registered.
     *
     * @param player the player for whom to open the menu
     */
    public void open(Player player) {
        refresh();
        player.openInventory(inventory);
        if (openAction != null) {
            openAction.onOpen(player);
        }
    }

    /**
     * Closes this menu for the given player and invokes the close hook if one is registered.
     *
     * @param player the player for whom to close the menu
     */
    public void close(Player player) {
        player.closeInventory();
        if (closeAction != null) {
            closeAction.onClose(player);
        }
    }

    /**
     * Rebuilds the inventory contents from the current list of MenuItem entries.
     * Subclasses may override to customize layout behavior, but should call super.refresh().
     */
    public void refresh() {
        inventory.clear();
        for (Map.Entry<Integer, MenuItem> entry : items.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getItem());
        }
    }

    /**
     * Removes all MenuItem entries from this menu.
     *
     * @return this Menu instance, for chaining
     */
    public Menu clearItems() {
        items.clear();
        return this;
    }

    /**
     * Adds a MenuItem to this menu. The item’s slot determines its position.
     *
     * @param item the MenuItem to add
     * @return this Menu instance, for chaining
     */
    public Menu addItem(MenuItem item, int slot) {
        items.put(slot, item);
        return this;
    }

    /**
     * Returns an unmodifiable list of all MenuItem entries in this menu.
     *
     * @return list of MenuItems
     */
    public Map<Integer, MenuItem> getItems() {
        return Collections.unmodifiableMap(items);
    }

    /**
     * Registers a general click hook for clicks inside the menu’s inventory.
     *
     * @param action the MenuClickAction to invoke on in-menu clicks
     * @return this Menu instance, for chaining
     */
    public Menu setMenuClickAction(MenuClickAction action) {
        this.generalMenuClick = action;
        return this;
    }

    /**
     * Registers a general click hook for clicks in the player’s own inventory.
     *
     * @param action the MenuClickAction to invoke on player-inventory clicks
     * @return this Menu instance, for chaining
     */
    public Menu setPlayerClickAction(MenuClickAction action) {
        this.generalPlayerClick = action;
        return this;
    }

    /**
     * Registers a hook to invoke when this menu is opened.
     *
     * @param action the MenuOpenAction to invoke on open
     * @return this Menu instance, for chaining
     */
    public Menu setOpenAction(MenuOpenAction action) {
        this.openAction = action;
        return this;
    }

    /**
     * Registers a hook to invoke when this menu is closed.
     *
     * @param action the MenuCloseAction to invoke on close
     * @return this Menu instance, for chaining
     */
    public Menu setCloseAction(MenuCloseAction action) {
        this.closeAction = action;
        return this;
    }

    /**
     * Registers a hook to invoke when the player drags items in the menu.
     *
     * @param action the MenuDragAction to invoke on drag
     * @return this Menu instance, for chaining
     */
    public Menu setDragAction(MenuDragAction action) {
        this.dragAction = action;
        return this;
    }

    /**
     * Handles a click event fired against this menu. Cancels the event,
     * invokes the appropriate general hook (menu vs. player inventory),
     * and then dispatches to the clicked MenuItem if one exists.
     *
     * @param player the player who clicked
     * @param event  the InventoryClickEvent to handle
     */
    public void handleClick(Player player, InventoryClickEvent event) {
        boolean inMenu = event.getClickedInventory() == inventory;
        MenuClickAction hook = inMenu
                ? generalMenuClick
                : generalPlayerClick;
        if (hook != null) {
            hook.onClick(player, event.getClick());
        }
        int slot = event.getRawSlot();
        MenuItem clickedItem = items.get(slot);
        if (clickedItem != null) {
            clickedItem.handleClick(player, event.getClick());
        }
    }

    /**
     * Handles a drag event fired against this menu. Cancels the event
     * and invokes the registered drag hook if present.
     *
     * @param player the player who dragged
     * @param event  the InventoryDragEvent to handle
     */
    public void handleDrag(Player player, InventoryDragEvent event) {
        if (dragAction != null) {
            dragAction.onDrag(player, event);
        }
    }
}

