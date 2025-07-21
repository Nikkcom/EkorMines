package me.ekorn.EkorMines.mine.service;

import me.ekorn.EkorMines.mine.domain.Mine;
import me.ekorn.EkorMines.mine.persistence.MinePersistence;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultThresholdService implements ThresholdService {
    private final ResetScheduler scheduler;
    private final MinePersistence persistence;
    private final Map<String, Integer> brokenCounts = new HashMap<>();


    public DefaultThresholdService(ResetScheduler scheduler, MinePersistence persistence) {
        this.scheduler = scheduler;
        this.persistence = persistence;
    }

    @Override
    public void record(Block block) {
        recordBatch(List.of(block));
    }

    @Override
    public void recordBatch(List<Block> blocks) {
        Map<String, Integer> delta = new HashMap<>();
        for (Block b : blocks) {
            Mine m = persistence.loadAll().stream()
                    .filter(x -> x.getRegion().contains(b.getLocation()))
                    .findFirst()
                    .orElse(null);
            if (m == null || !m.getPolicy().isResetOnThreshold()) continue;
            delta.merge(m.getId(), 1, Integer::sum);
        }

        for (var e : delta.entrySet()) {
            String id = e.getKey();
            int add = e.getValue();
            int totalBroken = brokenCounts.merge(id, add, Integer::sum);

            Mine mine = persistence.load(id).orElseThrow(() -> new IllegalStateException("Mine disappeared: " + id));


            if (mine.getPolicy().shouldResetByThreshold(totalBroken, mine.getRegion().getTotalBlockCount())
                    && !scheduler.isResetting(id))
            {
                scheduler.performReset(mine, completedMine -> {
                    brokenCounts.put(id, 0);
                    persistence.save(completedMine);
                });
            }
        }
    }

    @Override
    public void setThreshold(String mineId, double percent) {
        persistence.load(mineId).ifPresent(mine -> {
            mine.getPolicy().setThresholdPct(percent);
            persistence.save(mine);
        });
    }

    @Override
    public void setThresholdEnabled(String mineId, boolean enabled) {
        persistence.load(mineId).ifPresent(mine -> {
            mine.getPolicy().setResetOnThreshold(enabled);
            persistence.save(mine);
        });
    }
}
