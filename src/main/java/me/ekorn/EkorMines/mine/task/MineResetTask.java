package me.ekorn.EkorMines.mine.task;

import me.ekorn.EkorMines.mine.domain.Mine;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.function.Consumer;

public class MineResetTask extends BukkitRunnable {
    private final Mine mine;
    private final Iterator<Block> blocks;
    private final int blocksPerTick;
    private final Consumer<Mine> onComplete;

    public MineResetTask(JavaPlugin plugin,
                         Mine mine,
                         int blocksPerTick,
                         Consumer<Mine> onComplete) {
        this.mine = mine;
        this.blocksPerTick = blocksPerTick;
        this.onComplete = onComplete != null ? onComplete : m -> {};

        this.blocks = mine.getCache().getBlocks().iterator();

        plugin.getLogger().info("[MineResetTask] Starting reset of mine “"
                + mine.getId() + "” with "
                + blocksPerTick + " blocks/tick");
    }

    @Override
    public void run() {
        for (int i = 0; i < blocksPerTick && blocks.hasNext(); i++) {
            Block b = blocks.next();
            b.setType(mine.getRandomMaterial(), false);
        }

        if (!blocks.hasNext()) {
            mine.getPolicy().recordReset();
            onComplete.accept(mine);
            cancel();
        }
    }
}
