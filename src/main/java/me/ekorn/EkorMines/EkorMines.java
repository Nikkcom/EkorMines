package me.ekorn.EkorMines;

import me.ekorn.EkorMines.common.PluginContext;
import org.bukkit.plugin.java.JavaPlugin;

public final class EkorMines extends JavaPlugin {

    private PluginContext context;

    @Override
    public void onEnable() {
        context = new PluginContext(this);
    }

    @Override
    public void onDisable() {
        context.shutdown();
    }
}
