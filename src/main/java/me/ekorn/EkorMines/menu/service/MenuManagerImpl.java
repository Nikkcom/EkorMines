package me.ekorn.EkorMines.menu.service;

import me.ekorn.EkorMines.common.PluginContext;
import me.ekorn.EkorMines.common.api.PluginService;
import me.ekorn.EkorMines.menu.api.MenuManager;
import me.ekorn.EkorMines.menu.domain.Menu;
import me.ekorn.EkorMines.menu.domain.menu.MinesMenu;
import me.ekorn.EkorMines.mine.service.MineManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link MenuManager}.
 * <p>
 * Manages registration of menu templates, opening and closing menus for players,
 * and grouping of viewers per menu key.
 * </p>
 */
public class MenuManagerImpl implements MenuManager, PluginService {

    private final Map<String, Supplier<Menu>> menuFactories = new HashMap<>();
    private final Map<UUID, Menu> openMenus = new HashMap<>();
    private final Map<String, Set<UUID>> viewerGroups = new HashMap<>();


    @Override
    public void register(String key, Supplier<Menu> factory) {
        menuFactories.put(key, factory);
    }

    @Override
    public void init(PluginContext ctx) {
        MineManager mineManager = ctx.services().get(MineManager.class);
        register(MinesMenu.KEY, () -> new MinesMenu(new ArrayList<>(mineManager.getAll())));
    }

    @Override
    public void unregister(String key) {
        menuFactories.remove(key);
    }

    @Override
    public Set<String> getRegisteredKeys() {
        return Collections.unmodifiableSet(menuFactories.keySet());
    }

    /**
     * Finds a registered menu template by its key.
     *
     * @param key the identifier of the menu
     * @return an Optional containing the menu if present, or empty otherwise
     */
    @Override
    public Optional<Menu> find(String key) {
        Supplier<Menu> factory = menuFactories.get(key);
        return factory == null ? Optional.empty() : Optional.of(factory.get());
    }

    /**
     * Opens the menu identified by {@code key} for the specified player.
     * Tracks the player as having this menu open and adds them to the
     * viewer group for that menu key.
     *
     * @param key    the menu key to open
     * @param player the player for whom to open the menu
     */
    @Override
    public void open(String key, Player player) {
        Supplier<Menu> factory = menuFactories.get(key);
        if (factory == null) {
            player.sendMessage("Unknown Menu: " +key);
            return;
        }

        Menu menu = factory.get();

        menu.open(player);

        openMenus.put(player.getUniqueId(), menu);
        viewerGroups
                .computeIfAbsent(key, k -> new HashSet<>())
                .add(player.getUniqueId());
    }

    /**
     * Closes whatever menu the given player currently has open.
     * Removes the player from the open‐menus registry and from the
     * viewer group of that menu’s key.
     *
     * @param player the player whose open menu should be closed
     */
    @Override
    public void close(Player player) {
        UUID playerId = player.getUniqueId();
        Menu menu = openMenus.remove(playerId);
        if (menu == null) {
            return;
        }

        menu.close(player);

        String key = menu.getKey();
        Set<UUID> viewers = viewerGroups.get(key);
        if (viewers != null) {
            viewers.remove(playerId);
            if (viewers.isEmpty()) {
                viewerGroups.remove(key);
            }
        }
    }


    /**
     * Forces a refresh of the currently open menu for the given player,
     * causing it to be rebuilt in place.
     *
     * @param player the player whose menu should be refreshed
     */
    @Override
    public void refresh(Player player) {
        Optional<Menu> opt = Optional.ofNullable(openMenus.get(player.getUniqueId()));
        opt.ifPresent(Menu::refresh);
    }

    /**
     * Forces a refresh of all open menus that match the given key,
     * rebuilding each of their inventories in place.
     *
     * @param key the menu key whose open instances should be refreshed
     */
    @Override
    public void refreshAll(String key) {
        getViewers(key).forEach(this::refresh);
    }

    /**
     * Retrieves the menu instance that the specified player currently has open.
     *
     * @param player the player to check
     * @return an Optional containing the open menu, or empty if none
     */
    @Override
    public Optional<Menu> getOpenMenu(Player player) {
        return Optional.ofNullable(openMenus.get(player.getUniqueId()));
    }


    /**
     * Returns true if the given player currently has any menu open.
     *
     * @param player the player to check
     * @return true if a menu is open for that player; false otherwise
     */
    @Override
    public boolean hasOpenMenu(Player player) {
        return openMenus.containsKey(player.getUniqueId());
    }

    /**
     * Returns a set of all players currently viewing the menu
     * identified by {@code key}. Dead or offline players are filtered out.
     *
     * @param key the menu key whose viewers to retrieve
     * @return a set of players viewing that menu
     */
    @Override
    public Set<Player> getViewers(String key) {
        return viewerGroups
                .getOrDefault(key, Collections.emptySet())
                .stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * Closes and removes all open menus for all players viewing the
     * menu identified by {@code key}. Useful for force‐closing a menu
     * type for everyone.
     *
     * @param key the menu key whose viewers should all be closed
     */
    @Override
    public void removeAll(String key) {
        getViewers(key).forEach(this::close);
        viewerGroups.remove(key);
    }
}
