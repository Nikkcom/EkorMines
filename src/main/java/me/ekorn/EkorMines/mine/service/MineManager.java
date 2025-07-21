package me.ekorn.EkorMines.mine.service;

import me.ekorn.EkorMines.common.PluginContext;
import me.ekorn.EkorMines.mine.domain.*;
import me.ekorn.EkorMines.mine.persistence.MinePersistence;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class MineManager {
    private final PluginContext ctx;
    private final MinePersistence persistence;
    private final ResetScheduler scheduler;
    private final ThresholdService threshold;


    public MineManager(PluginContext ctx, MinePersistence persistence, ResetScheduler scheduler, ThresholdService threshold) {
        this.ctx = ctx;
        this.persistence = persistence;
        this.scheduler = scheduler;
        this.threshold = threshold;

        persistence.reloadAll();
        for (Mine m : persistence.loadAll()) {
            scheduler.scheduleInterval(m);
        }
    }



    public boolean create(String id, Region region) {
        ctx.plugin().getLogger().info("[create] BEFORE: " + persistence.loadAll().stream()
                .map(Mine::getId).toList());
        if (persistence.load(id).isPresent()) {
            ctx.plugin().getLogger().warning("[create] '" + id + "' already exists, aborting");
            return false;
        }

        Mine mine = new Mine(id);
        mine.setRegion(region);
        mine.setPicker(new WeightedPicker<>());
        mine.setPolicy(new ResetPolicy());
        mine.setConfig(MineMetadata.withDefaults(region));
        mine.setCache(new BlockOrdering(region, ResetDirection.TOP_DOWN));
        persistence.save(mine);

        ctx.plugin().getLogger().info("[create] AFTER: "  + persistence.loadAll().stream()
                .map(Mine::getId).collect(Collectors.toList()));
        scheduler.scheduleInterval(mine);
        return true;
    }

    public Optional<Mine> get(String id) {
        return persistence.load(id.toLowerCase());
    }

    public Collection<Mine> getAll() {
        return persistence.loadAll();
    }

    public boolean exists(String id) {
        return get(id).isPresent();
    }

    public boolean delete(String id) {
        if (persistence.load(id).isEmpty()) return false;
        persistence.delete(id);
        scheduler.cancelInterval(id);
        return true;
    }

    // Reset operations
    public void resetManual(String id) {
        persistence.load(id)
                .ifPresent(m -> scheduler.performReset(m, persistence::save));
    }

    // Threshold
    public void onBlockBroken(Block b) {
        threshold.record(b);
    }

    public void setThreshold(String id, double pct, boolean enabled) {
        threshold.setThreshold(id, pct);
        threshold.setThresholdEnabled(id, enabled);
    }

    public boolean setSpawnLocation(String mineId, Location loc) {
        Optional<Mine> mineOpt = get(mineId);
        if (mineOpt.isEmpty()) return false;
        Mine mine = mineOpt.get();
        mine.getConfig().setSpawnLocation(loc);
        return true;
    }

    public boolean setRegion(String mineId, Region region) {
        Optional<Mine> mineOpt = get(mineId);
        if (mineOpt.isEmpty()) return false;
        Mine mine = mineOpt.get();
        mine.setRegion(region);
        return true;
    }

    public boolean setResetDirection(String mineId, ResetDirection direction) {
        Optional<Mine> mineOpt = get(mineId);
        if (mineOpt.isEmpty()) return false;
        Mine mine = mineOpt.get();
        mine.getCache().setDirection(direction);
        return true;
    }

    public void teleportPlayer(String mineId, Player player) {
        if (player == null || !player.isOnline()) return;
        Optional<Mine> mineOpt = get(mineId);
        if (mineOpt.isEmpty()) return;
        player.teleport(mineOpt.get().getSpawnLocation());
        player.sendMessage(Component.text(String.format("You teleported to '%s'!", mineOpt.get().getId())));
    }
}
