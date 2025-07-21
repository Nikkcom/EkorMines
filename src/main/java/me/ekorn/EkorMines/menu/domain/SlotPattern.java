package me.ekorn.EkorMines.menu.domain;

import java.util.List;

@FunctionalInterface
public interface SlotPattern {


    List<Integer> slotsForPage(int rows, int pageIndex);

    default SlotPattern withOffset(int offsetX, int offsetY) {
        int rowWidth = 9;
        return (rows, page) ->
                this.slotsForPage(rows, page).stream()
                        .map(slot -> {
                          int x = slot % rowWidth;
                          int y = slot / rowWidth;
                          int newX = x + offsetX;
                          int newY = y + offsetY;
                          return newY * rowWidth + newX;
                        })
                        .filter(i -> i >= 0 && i < rows * 9)
                        .toList();
    }
}
