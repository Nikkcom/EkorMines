package me.ekorn.EkorMines.mine.service;

import me.ekorn.EkorMines.mine.domain.Mine;
import me.ekorn.EkorMines.mine.task.MineResetTask;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class BukkitResetScheduler implements ResetScheduler {
    private final JavaPlugin plugin;
    private final Map<String, BukkitTask> intervalTasks = new ConcurrentHashMap<>();
    private final Set<String> resetting = ConcurrentHashMap.newKeySet();

    public BukkitResetScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void scheduleInterval(Mine mine) {
        cancelInterval(mine.getId());
        if (!mine.getPolicy().isResetOnInterval()) return;

        long delay = mine.getPolicy().getResetIntervalSecs() * 20L;
        intervalTasks.put(mine.getId(), new BukkitRunnable() {
            @Override public void run() {
                performReset(mine, m -> {
                    if (m.getPolicy().isResetOnInterval()) {
                        scheduleInterval(m);
                    }
                });
            }
        }.runTaskLater(plugin, delay));
    }

    @Override
    public void cancelInterval(String mineId) {
        BukkitTask old = intervalTasks.remove(mineId);
        if (old != null) old.cancel();
    }

    @Override
    public void performReset(Mine mine, Consumer<Mine> onComplete) {
        String id = mine.getId();
        if (!resetting.add(id)) return;

        int bpt = mine.getConfig().getResetBlocksPerTicks();

        new MineResetTask(plugin, mine, bpt, completed -> {
            completed.getPolicy().recordReset();
            onComplete.accept(completed);
            resetting.remove(id);
        }).runTaskTimer(plugin, 1L, 1L);
    }

    @Override
    public boolean isResetting(String mineId) {
        return resetting.contains(mineId);
    }
}
