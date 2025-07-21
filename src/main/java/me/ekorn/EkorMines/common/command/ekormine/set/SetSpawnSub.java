package me.ekorn.EkorMines.common.command.ekormine.set;

import me.ekorn.EkorMines.common.command.ekormine.SubCommand;
import me.ekorn.EkorMines.mine.domain.Mine;
import me.ekorn.EkorMines.mine.service.MineManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class SetSpawnSub implements SubCommand {
    private final MineManager mineManager;

    public SetSpawnSub(MineManager mineManager) {
        this.mineManager = mineManager;
    }

    @Override
    public String getName() {
        return "spawn";
    }

    @Override
    public String getDescription() {
        return "Set the spawn location for a mine to your current location.";
    }

    @Override
    public String getUsage() {
        return "<mineId>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only player can perform this command.");
            return true;
        }
        if (args.length != 1) {
            return false;
        }
        String id = args[0];
        return mineManager.setSpawnLocation(id, player.getLocation());
        // MESSAGE_HERE
    }

    @Override
    public List<String> suggest(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return mineManager.getAll().stream()
                    .map(Mine::getId)
                    .filter(id -> id.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
