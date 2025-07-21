package me.ekorn.EkorMines.mine.service;

import org.bukkit.block.Block;

import java.util.List;

public interface ThresholdService {
    void record(Block block);
    void recordBatch(List<Block> blocks);
    void setThreshold(String mineId, double percent);
    void setThresholdEnabled(String mineId, boolean enabled);
}
