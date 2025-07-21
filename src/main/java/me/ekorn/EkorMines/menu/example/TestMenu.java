package me.ekorn.EkorMines.menu.example;

import me.ekorn.EkorMines.menu.domain.DefaultSlotPatterns;
import me.ekorn.EkorMines.menu.domain.MenuItem;
import me.ekorn.EkorMines.menu.domain.PaginatedMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TestMenu extends PaginatedMenu {

    public TestMenu() {
        super("testmenu", 4, Component.text("Test Pagination"));
    }

    @Override
    protected void defineSections() {
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        for (int slot : DefaultSlotPatterns.HEADER.slotsForPage(getRows(), 0)) {
            addStaticItem(new MenuItem(
                    glass,
                    (player, click) -> {}
            ), slot);
        }

        // 2) BODY: 50 paper items, click sends chat
        List<MenuItem> bodyItems = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            int idx = i;
            ItemStack paper = new ItemStack(Material.PAPER);
            ItemMeta m = paper.getItemMeta();
            m.displayName(Component.text("Item #" + idx));
            paper.setItemMeta(m);

            bodyItems.add(new MenuItem(
                    paper,
                    (player, click) -> player.sendMessage(
                            Component.text("You clicked item #" + idx + " with " + click)
                    )
            ));
        }


        // add a paginated BODY section, with Prev/Next arrows in footer corners
        Section body = addSection(DefaultSlotPatterns.BODY, bodyItems);
        ItemStack prevArrow = navIcon(Material.ARROW, "« Prev");
        ItemStack nextArrow = navIcon(Material.ARROW, "Next »");
        // footer slots: first and last of footer row
        List<Integer> footerSlots = DefaultSlotPatterns.FOOTER.slotsForPage(getRows(), 0);
        body.withNav(
                prevArrow, footerSlots.getFirst(),
                nextArrow, footerSlots.getLast()
        );

        // 3) FOOTER CENTER: an “Exit” barrier button that always closes
        int exitSlot = footerSlots.get(footerSlots.size() / 2);
        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta bm = barrier.getItemMeta();
        bm.displayName(Component.text("Exit"));
        barrier.setItemMeta(bm);
        addStaticItem(new MenuItem(
                barrier,
                (player, click) -> player.closeInventory()
        ), exitSlot);



    }

    /** Utility to create a one-line name for nav arrows */
    private ItemStack navIcon(Material mat, String name) {
        ItemStack icon = new ItemStack(mat);
        ItemMeta meta = icon.getItemMeta();
        meta.displayName(Component.text(name));
        icon.setItemMeta(meta);
        return icon;
    }
}
