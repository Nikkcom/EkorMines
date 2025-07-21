package me.ekorn.EkorMines.mine.domain;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Objects;

public class MineMetadata {
    private Location spawnLocation;
    private Material displayItem;
    private int resetBlocksPerTicks;

    public MineMetadata() {}

    public MineMetadata(Location spawnLocation, Material displayItem, int resetBlocksPerTicks) {
        this.spawnLocation = Objects.requireNonNull(spawnLocation, "spawnLocation");
        this.displayItem = Objects.requireNonNull(displayItem, "displayItem");
        this.resetBlocksPerTicks = resetBlocksPerTicks;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location loc) {
        this.spawnLocation = Objects.requireNonNull(loc, "loc");
    }


    public Material getDisplayItem() {
        if (displayItem == null || displayItem.isAir()) {
            return Material.STONE;
        }
        return displayItem;
    }

    public void setDisplayItem(Material displayItem) {
        this.displayItem = Objects.requireNonNull(displayItem, "displayItem");
    }

    public int getResetBlocksPerTicks() {
        return resetBlocksPerTicks;
    }

    public void setResetBlocksPerTicks(int resetBlocksPerTicks) {
        this.resetBlocksPerTicks = resetBlocksPerTicks;
    }

    @Override
    public String toString() {
        return "MineConfig[" +
                "spawn=" + (spawnLocation != null
                ? spawnLocation.getWorld().getName()+"@("+spawnLocation.getBlockX()
                +","+spawnLocation.getBlockY()+","+spawnLocation.getBlockZ()+")"
                : "default") +
                ", icon=" + displayItem +
                ", resetBlocksPerTick=" + resetBlocksPerTicks +
                "]";
    }

    public static MineMetadata withDefaults(Region region) {
        double centerX = (region.getXMin() + region.getXMax()) / 2.0;
        double centerZ = (region.getZMin() + region.getZMax()) / 2.0;
        double spawnY  = region.getYMax() + 1.0;
        Location defaultSpawn = new Location(
                region.getWorld(),
                centerX,
                spawnY,
                centerZ
        );
        return new MineMetadata(
                defaultSpawn,        // spawn
                Material.STONE,      // default display item
                20                   // default reset-blocks-per-tick
        );
    }
}
