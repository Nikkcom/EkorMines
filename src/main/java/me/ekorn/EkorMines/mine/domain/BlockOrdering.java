package me.ekorn.EkorMines.mine.domain;

import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockOrdering {
    private final Region region;
    private ResetDirection direction;
    private List<Block> cachedBlocks;
    private boolean dirty = true;

    public BlockOrdering(Region region, ResetDirection initialDirection) {
        this.region = region;
        this.direction = initialDirection;
    }

    public void setDirection(ResetDirection newDirection) {
        if (this.direction != newDirection) {
            this.direction = newDirection;
            this.dirty = true;
        }
    }

    public Iterable<Block> getBlocks() {
        if (dirty || cachedBlocks == null) {
            rebuildCache();
            dirty = false;
        }
        return cachedBlocks;
    }

    public void rebuildCache() {
        World world = region.getWorld();
        int xMin = region.getXMin(), xMax = region.getXMax();
        int yMin = region.getYMin(), yMax = region.getYMax();
        int zMin = region.getZMin(), zMax = region.getZMax();

        List<Block> blocks = new ArrayList<>(region.getTotalBlockCount());
        switch (direction) {
            case TOP_DOWN -> {
                for (int y = yMax; y >= yMin; y--) {
                    for (int z = zMin; z <= zMax; z++) {
                        for (int x = xMin; x <= xMax; x++) {
                            blocks.add(world.getBlockAt(x, y, z));
                        }
                    }
                }
            }
            case BOTTOM_UP -> {
                for (int y = yMin; y <= yMax; y++) {
                    for (int z = zMin; z <= zMax; z++) {
                        for (int x = xMin; x <= xMax; x++) {
                            blocks.add(world.getBlockAt(x, y, z));
                        }
                    }
                }
            }
            case NORTH_SOUTH -> {
                for (int z = zMin; z <= zMax; z++) { // -Z to +Z
                    for (int y = yMin; y <= yMax; y++) {
                        for (int x = xMin; x <= xMax; x++) {
                            blocks.add(world.getBlockAt(x, y, z));
                        }
                    }
                }
            }
            case SOUTH_NORTH -> {
                for (int z = zMax; z >= zMin; z--) { // +Z to -Z
                    for (int y = yMin; y <= yMax; y++) {
                        for (int x = xMin; x <= xMax; x++) {
                            blocks.add(world.getBlockAt(x, y, z));
                        }
                    }
                }
            }
            case EAST_WEST -> {
                for (int x = xMax; x >= xMin; x--) { // +X to -X
                    for (int y = yMin; y <= yMax; y++) {
                        for (int z = zMin; z <= zMax; z++) {
                            blocks.add(world.getBlockAt(x, y, z));
                        }
                    }
                }
            }
            case WEST_EAST -> {
                for (int x = xMin; x <= xMax; x++) { // -X to +X
                    for (int y = yMin; y <= yMax; y++) {
                        for (int z = zMin; z <= zMax; z++) {
                            blocks.add(world.getBlockAt(x, y, z));
                        }
                    }
                }
            }
        }

        this.cachedBlocks = Collections.unmodifiableList(blocks);
    }

    public ResetDirection getDirection() {
        return direction;
    }
}
