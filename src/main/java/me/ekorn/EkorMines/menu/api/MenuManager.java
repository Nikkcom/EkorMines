package me.ekorn.EkorMines.menu.api;

import me.ekorn.EkorMines.menu.domain.Menu;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;


/**
 * Central registry and controller for all inventory‐based menus.
 * <p>
 * Allows registering menu templates, opening and closing them per‐player,
 * querying and refreshing open menus, and managing viewer groups.
 * </p>
 */
public interface MenuManager {


    void register(String key, Supplier<Menu> factory);

    /**
     * Unregisters a previously registered menu key, preventing future opens.
     *
     * @param key the identifier of the menu to unregister
     */
    void unregister(String key);

    /**
     * Returns the set of all menu keys that have been registered.
     *
     * @return an unmodifiable set of registered menu keys
     */
    Set<String> getRegisteredKeys();

    /**
     * Opens the menu identified by {@code key} for the given player.
     *
     * @param key    the menu key to open
     * @param player the player who should see the menu
     */
    void open(String key, Player player);

    /**
     * Closes whatever menu the given player currently has open.
     * If no menu is open for that player, this is a no‐op.
     *
     * @param player the player whose open menu should be closed
     */
    void close(Player player);

    /**
     * Forces a refresh of the currently open menu for the given player,
     * causing its contents to be rebuilt in place.
     *
     * @param player the player whose menu should be refreshed
     */
    void refresh(Player player);

    /**
     * Forces a refresh of all open menus that match the given key,
     * rebuilding each of their inventories in place.
     *
     * @param key the menu key whose open instances should be refreshed
     */
    void refreshAll(String key);

    /**
     * Finds a registered menu template by its key.
     *
     * @param key the identifier of the menu
     * @return an Optional containing the template if present, otherwise empty
     */
    Optional<Menu> find(String key);

    /**
     * Retrieves the menu instance that the specified player currently has open.
     *
     * @param player the player to check
     * @return an Optional containing the open menu instance, or empty if none
     */
    Optional<Menu> getOpenMenu(Player player);

    /**
     * Returns true if the given player currently has any menu open.
     *
     * @param player the player to check
     * @return true if a menu is open for that player; false otherwise
     */
    boolean hasOpenMenu(Player player);

    /**
     * Gets the set of players currently viewing the menu identified by {@code key}.
     * Offline or invalid players are filtered out.
     *
     * @param key the menu key whose viewers to retrieve
     * @return a set of players viewing that menu
     */
    Set<Player> getViewers(String key);

    /**
     * Closes and removes all open menus for all players viewing the
     * menu identified by {@code key}.
     *
     * @param key the menu key whose open instances should be removed
     */
    void removeAll(String key);
}
