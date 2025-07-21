package me.ekorn.EkorMines.menu.listener;


import me.ekorn.EkorMines.menu.api.MenuManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/**
 * Listens for inventory‐click and –drag events and delegates to the
 * currently open {@link Menu} (if any) via the {@link MenuManager}.
 */
public class MenuListener implements Listener {

    private final MenuManager menuManager;

    public MenuListener(MenuManager menuManager) {
        this.menuManager = menuManager;
    }


    /**
     * Intercepts clicks in any inventory.  If the player has a Menu open,
     * cancels the click and calls that menu’s handleClick().
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        menuManager.getOpenMenu(player).ifPresent(menu -> {
            event.setCancelled(true);
            menu.handleClick(player, event);
        });
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        menuManager.getOpenMenu(player).ifPresent(menu -> {
            event.setCancelled(true);
            menu.handleDrag(player, event);
        });
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        menuManager.close(player);
    }
}
