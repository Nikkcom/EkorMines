package me.ekorn.EkorMines.mine.domain;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

public class Region {
    private final World world;

    private final int xMin, xMax;
    private final int yMin, yMax;
    private final int zMin, zMax;

    private final double xMinCentered, xMaxCentered;
    private final double yMinCentered, yMaxCentered;
    private final double zMinCentered, zMaxCentered;

    public Region(Location loc1, Location loc2) {
        Objects.requireNonNull(loc1, "loc1 must not be null");
        Objects.requireNonNull(loc2, "loc2 must not be null");
        World w1 = loc1.getWorld(), w2 = loc2.getWorld();
        if (w1 == null || w2 == null) {
            throw new IllegalArgumentException("Both locations must have a world");
        }
        if (!w1.equals(w2)) {
            throw new IllegalArgumentException("Both locations must be in the same world");
        }

        this.world = w1;

        this.xMin = Math.min(loc1.getBlockX(), loc2.getBlockX());
        this.xMax = Math.max(loc1.getBlockX(), loc2.getBlockX());
        this.yMin = Math.min(loc1.getBlockY(), loc2.getBlockY());
        this.yMax = Math.max(loc1.getBlockY(), loc2.getBlockY());
        this.zMin = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        this.zMax = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        this.xMinCentered = xMin + 0.5;
        this.xMaxCentered = xMax + 0.5;
        this.yMinCentered = yMin + 0.5;
        this.yMaxCentered = yMax + 0.5;
        this.zMinCentered = zMin + 0.5;
        this.zMaxCentered = zMax + 0.5;
    }

    public World getWorld() {
        return world;
    }

    public Location getLoc1() {
        return new Location(world, xMin, yMin, zMin);
    }

    public Location getLoc2() {
        return new Location(world, xMax, yMax, zMax);
    }

    public int getXWidth() {
        return xMax - xMin;
    }

    public int getHeight() {
        return yMax - yMin;
    }

    public int getZWidth() {
        return zMax - zMin;
    }

    public int getXMin() { return xMin; }
    public int getXMax() { return xMax; }
    public int getYMin() { return yMin; }
    public int getYMax() { return yMax; }
    public int getZMin() { return zMin; }
    public int getZMax() { return zMax; }

    public int getTotalBlockCount() {
        return getXWidth() * getHeight() * getZWidth();
    }

    public boolean contains(Location loc) {
        if (loc.getWorld() != world) return false;
        int x = loc.getBlockX(), y = loc.getBlockY(), z = loc.getBlockZ();
        return x >= xMin && x <= xMax
                && y >= yMin && y <= yMax
                && z >= zMin && z <= zMax;
    }

    public boolean contains(Location loc, double margin) {
        if (loc.getWorld() != world) return false;
        double x = loc.getX(), y = loc.getY(), z = loc.getZ();
        return x >= xMinCentered - margin && x <= xMaxCentered + margin
                && y >= yMinCentered - margin && y <= yMaxCentered + margin
                && z >= zMinCentered - margin && z <= zMaxCentered + margin;
    }

    public Location getCenter() {
        double cx = (xMinCentered + xMaxCentered) / 2.0;
        double cy = (yMinCentered + yMaxCentered) / 2.0;
        double cz = (zMinCentered + zMaxCentered) / 2.0;
        return new Location(world, cx, cy, cz);
    }

    public double getPointDistance() {
        return getLoc1().distance(getLoc2());
    }

    public double getPointDistanceSquared() {
        return getLoc1().distanceSquared(getLoc2());
    }

    @Override
    public String toString() {
        return String.format(
                "Region[world=%s, (%d, %d, %d) -> (%d, %d, %d)",
                world.toString(),
                xMin, yMin, zMin,
                xMax, yMax, zMax
        );
    }
}
