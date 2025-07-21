package me.ekorn.EkorMines.mine.domain;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Objects;

public class Mine {
    private final String id;
    private Region region;
    private WeightedPicker<Material> picker;
    private ResetPolicy policy;
    private BlockOrdering cache;
    private MineMetadata config;

    private int brokenBlocksCount = 0;

    public Mine(String id) {
        this.id = id;
    }
    public Mine(String id,
                Region region,
                WeightedPicker<Material> picker,
                ResetPolicy policy,
                BlockOrdering cache,
                MineMetadata config) {
        this.id      = Objects.requireNonNull(id,      "id must not be null");
        this.region  = Objects.requireNonNull(region,  "region must not be null");
        this.picker  = Objects.requireNonNull(picker,  "picker must not be null");
        this.policy  = Objects.requireNonNull(policy,  "policy must not be null");
        this.cache   = Objects.requireNonNull(cache,   "cache must not be null");
        this.config  = Objects.requireNonNull(config,  "config must not be null");
    }
    public WeightedPicker<Material> getPicker() {
        return picker;
    }

    public ResetPolicy getPolicy() {
        return policy;
    }

    public BlockOrdering getCache() {
        return cache;
    }

    public MineMetadata getConfig() {
        return config;
    }

    public int getBrokenBlocksCount() {
        return brokenBlocksCount;
    }

    public void setPicker(WeightedPicker<Material> picker) {
        this.picker = picker;
    }

    public void setPolicy(ResetPolicy policy) {
        this.policy = policy;
    }

    public void setCache(BlockOrdering cache) {
        this.cache = cache;
    }

    public void setConfig(MineMetadata config) {
        this.config = config;
    }



    public String getId() {
        return id;
    }

    public boolean isIn(Location loc) {
        return region.contains(loc);
    }

    public void recordBreak() {
        brokenBlocksCount++;
    }

    public Material getRandomMaterial() {
        return picker.nextRandom();
    }

    public Iterable<Block> getBlocks() {
        return cache.getBlocks();
    }

    public Location getSpawnLocation() {
        return config.getSpawnLocation();
    }

    public Material getDisplayItem() {
        return config.getDisplayItem();
    }

    public void resetIfNeeded() {
        boolean byInterval = policy.isResetOnInterval()
                && policy.shouldResetByInterval();
        boolean byThreshold = policy.isResetOnThreshold()
                && policy.shouldResetByThreshold(brokenBlocksCount, region.getTotalBlockCount());

        if (!(byInterval || byThreshold)) return;

        for (Block block : cache.getBlocks()) {
            block.setType(getRandomMaterial(), false);

            brokenBlocksCount = 0;
            policy.recordReset();
        }
    }

    @Override
    public String toString() {
        return "Mine{" +
                "id='" + id + '\'' +
                ", region=" + region +
                ", spawn=" + config.getSpawnLocation() +
                ", displayItem=" + config.getDisplayItem() +
                ", policy=" + policy +
                ", brokenCount=" + brokenBlocksCount +
                '}';
    }

    public Region getRegion() {
        return region;
    }
    public void setRegion(Region region) {
        this.region = region;
    }
}
