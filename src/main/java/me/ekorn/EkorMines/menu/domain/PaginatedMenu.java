package me.ekorn.EkorMines.menu.domain;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class PaginatedMenu extends Menu {

    // Non changing items, that never change on page change. Navigation items, static items.
    private final Map<MenuItem, Integer> staticItems = new HashMap<>();

    // All defined sections of the GUI.
    private final List<Section> sections = new ArrayList<>();
    /**
     * Constructs a new menu template.
     *
     * @param key   unique identifier for this menu type
     * @param rows  the number of rows in the inventory
     * @param title the title component shown at the top of the inventory
     */
    public PaginatedMenu(String key, int rows, Component title) {
        super(key, rows, title);
    }

    @Override
    public void open(Player player) {
        for (Section section : sections) {
            section.currentPage = 0;
        }
        super.open(player);
    }

    protected abstract void defineSections();

    protected void addStaticItem(MenuItem item, int slot) {
        staticItems.put(item, slot);
    }

    // PAGINATED // STATIC SECTIONS

    protected Section addSection(SlotPattern pattern, List<MenuItem> items) {
        Section section = new Section(pattern, items);
        sections.add(section);
        return section;
    }

    // REFRESH

    @Override
    public void refresh() {
        clearItems();

        staticItems.forEach((this::addItem));

        for (Section sec : sections) {
            sec.render();
        }

        super.refresh();
    }



    // SECTIONS


    protected class Section {
        private final SlotPattern pattern;
        private final List<MenuItem> allItems;
        private int currentPage = 0;

        private ItemStack prevItem;
        private int prevSlot = -1;
        private ItemStack nextItem;
        private int nextSlot = -1;

        Section(SlotPattern pattern, List<MenuItem> items) {
            this.pattern = pattern;
            this.allItems = new ArrayList<>(items);
        }

        public Section withNav(ItemStack prevItem, int prevSlot, ItemStack nextItem, int nextSlot) {
            this.prevItem = prevItem;
            this.prevSlot = prevSlot;
            this.nextItem = nextItem;
            this.nextSlot = nextSlot;
            return this;
        }

        public int totalPages() {
            int cap = pattern.slotsForPage(getRows(), 0).size();
            if (cap == 0) return 1;
            return (allItems.size() + cap - 1) / cap;
        }

        private List<MenuItem> getPageItems() {
            int cap = pattern.slotsForPage(getRows(), 0).size();
            int start = currentPage * cap;
            int end = Math.min(start + cap, allItems.size());
            if (start >= allItems.size()) return Collections.emptyList();
            return allItems.subList(start, end);
        }

        private void render() {
            List<MenuItem> toShow = getPageItems();
            List<Integer> slots = pattern.slotsForPage(getRows(), currentPage);

            for (int i = 0; i < toShow.size() && i < slots.size(); i++) {
                int slot = slots.get(i);
                if (slot < 0 || slot >= getSize()) continue;
                MenuItem menuItem = toShow.get(i);
                addItem(menuItem, slot);
            }

            if (prevItem != null) {
                addItem(new MenuItem(prevItem, (player, clickType) -> {
                    if (currentPage > 0) {
                        currentPage--;
                        PaginatedMenu.this.refresh();
                    }
                }), prevSlot);
            }

            if (nextItem != null) {
                addItem(new MenuItem(nextItem, (player, clickType) -> {
                    if (currentPage < totalPages() - 1) {
                        currentPage++;
                        PaginatedMenu.this.refresh();
                    }
                }), nextSlot);
            }
        }
    }
}
