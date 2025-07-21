package me.ekorn.EkorMines.common.command.ekormine;

import me.ekorn.EkorMines.mine.domain.*;
import me.ekorn.EkorMines.mine.service.MineManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class InfoCommand implements SubCommand {

    private final MineManager mineManager;

    public InfoCommand(MineManager mineManager) {
        this.mineManager = mineManager;
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Displays info of the selected mine";
    }

    @Override
    public String getUsage() {
        return "<mine>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage("ARGS " + Arrays.toString(args));
        if (args.length != 1) {
            sender.sendMessage(getUsage());
            return true;
        }

        String id = args[0];
        Optional<Mine> mineOpt = mineManager.get(id);
        if (!mineManager.exists(id) || mineOpt.isEmpty()) {
            sender.sendMessage("Invalid mine");
            return true;
        }
        Mine mine = mineOpt.get();
        Region region = mine.getRegion();
        ResetPolicy policy = mine.getPolicy();
        BlockOrdering cache = mine.getCache();
        MineMetadata config = mine.getConfig();
        sender.sendMessage(ChatColor.GOLD + "---- Mine Info: " + ChatColor.GREEN + id + ChatColor.GOLD + " ----");
        sender.sendMessage(ChatColor.YELLOW + "World: " + ChatColor.WHITE + region.getWorld().getName());
        sender.sendMessage(ChatColor.YELLOW + "Bounds: "
                + ChatColor.WHITE + "("
                + region.getXMin() + ","
                + region.getYMin() + ","
                + region.getZMin() + ")"
                + ChatColor.YELLOW + " → "
                + ChatColor.WHITE + "("
                + region.getXMax() + ","
                + region.getYMax() + ","
                + region.getZMax() + ")");
        sender.sendMessage(ChatColor.YELLOW + "Volume: "
                + ChatColor.WHITE + region.getTotalBlockCount() + " blocks");
        sender.sendMessage(ChatColor.YELLOW + "Center: "
                + ChatColor.WHITE + region.getCenter().toVector().toString());

        sender.sendMessage(ChatColor.YELLOW + "Spawn Location: "
                + ChatColor.WHITE + config.getSpawnLocation().toVector().toString());
        sender.sendMessage(ChatColor.YELLOW + "Display Item: "
                + ChatColor.WHITE + config.getDisplayItem().name());
        sender.sendMessage(ChatColor.YELLOW + "Broken Blocks Since Last Reset: "
                + ChatColor.WHITE + mine.getBrokenBlocksCount());

        sender.sendMessage(ChatColor.YELLOW + "Reset Direction: "
                + ChatColor.WHITE + cache.getDirection().name());

        sender.sendMessage(ChatColor.YELLOW + "Interval Reset: "
                + ChatColor.WHITE
                + (policy.isResetOnInterval()
                ? ("Enabled every " + policy.getResetIntervalSecs() + "s")
                : "Disabled"));

        sender.sendMessage(ChatColor.YELLOW + "Threshold Reset: "
                + ChatColor.WHITE
                + (policy.isResetOnThreshold()
                ? ("Enabled at " + (int)(policy.getThresholdPct() * 100) + "% broken")
                : "Disabled"));

        sender.sendMessage(ChatColor.YELLOW + "Manual Reset: "
                + ChatColor.WHITE
                + (policy.isAllowManualReset()
                ? ("Allowed (cooldown " + policy.getManualResetCooldownSecs() + "s)")
                : "Disabled"));

        return true;
    }

    @Override
    public List<String> suggest(CommandSender sender, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return mineManager.getAll().stream()
                    .map(Mine::getId)
                    .filter(s -> s.startsWith(prefix))
                    .toList();
        }
        return List.of();
    }
}
