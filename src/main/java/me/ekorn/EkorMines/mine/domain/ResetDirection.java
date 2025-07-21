package me.ekorn.EkorMines.mine.domain;

public enum ResetDirection {
    TOP_DOWN,
    BOTTOM_UP,
    NORTH_SOUTH,
    SOUTH_NORTH,
    EAST_WEST,
    WEST_EAST;





    private static final ResetDirection[] VALUES = values();

    public ResetDirection next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }
}
