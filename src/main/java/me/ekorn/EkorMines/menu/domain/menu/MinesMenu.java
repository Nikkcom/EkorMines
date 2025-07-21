package me.ekorn.EkorMines.menu.domain.menu;

import me.ekorn.EkorMines.menu.domain.DefaultSlotPatterns;
import me.ekorn.EkorMines.menu.domain.MenuItem;
import me.ekorn.EkorMines.menu.domain.PaginatedMenu;
import me.ekorn.EkorMines.mine.domain.Mine;
import me.ekorn.EkorMines.mine.service.MineManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MinesMenu extends PaginatedMenu {
    public static final String KEY = "mines_menu";
    public static final int ROWS = 3;
    public static final Component TITLE = Component.text("Mines");

    private List<Mine> mines = new ArrayList<>();

    public MinesMenu(List<Mine> mines) {
        super(KEY, ROWS, TITLE);
        this.mines.addAll(mines);
        defineSections();
    }

    @Override
    protected void defineSections() {
        setHeader();
        setFooter();
        setBody();
    }

    private void setBody() {
        List<MenuItem> mineItems = new ArrayList<>();

        for (Mine mine : mines) {
            Material displayIconMaterial = mine.getConfig().getDisplayItem();
            ItemStack item = new ItemStack(displayIconMaterial);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text(mine.getId()));
            item.setItemMeta(meta);
            MenuItem menuItem = new MenuItem(item, ((player, clickType) -> {
                player.sendMessage(Component.text("You clicked '" +mine.getId()+ "'! Would of teleport ok?"));
            }));
            mineItems.add(menuItem);
        }


        ItemStack stack = new ItemStack(Material.ARROW);
        addSection(DefaultSlotPatterns.BODY, mineItems).withNav(stack, getRows() * 9 - 6, stack, getRows() * 9 - 4);
    }

    private void setHeader() {
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        meta.displayName(Component.empty());
        glass.setItemMeta(meta);
        for (int slot : DefaultSlotPatterns.HEADER.slotsForPage(getRows(), 0)) {
            addStaticItem(new MenuItem(
                    glass,
                    (player, click) -> {}
            ), slot);
        }
    }
    private void setFooter() {
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        meta.displayName(Component.empty());
        glass.setItemMeta(meta);
        for (int slot : DefaultSlotPatterns.FOOTER.slotsForPage(getRows(), 0)) {
            addStaticItem(new MenuItem(
                    glass,
                    (player, click) -> {}
            ), slot);
        }
    }
}
