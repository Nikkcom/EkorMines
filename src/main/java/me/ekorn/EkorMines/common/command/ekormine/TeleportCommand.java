package me.ekorn.EkorMines.common.command.ekormine;

import me.ekorn.EkorMines.mine.domain.Mine;
import me.ekorn.EkorMines.mine.service.MineManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TeleportCommand implements SubCommand {
    private final MineManager mineManager;

    public TeleportCommand(MineManager mineManager) {
        this.mineManager = mineManager;
    }

    @Override
    public String getName() {
        return "teleport";
    }

    @Override
    public String getDescription() {
        return "Teleports you to the mine";
    }

    @Override
    public String getUsage() {
        return "<mine>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to perform this command");
            return true;
        }
        if (args.length != 1) {
            return false;
        }
        String id = args[0];
        if (!mineManager.exists(id)) {
            sender.sendMessage("Invalid Mine: '" +id+ "'");
            return true;
        }
        mineManager.teleportPlayer(id, player);
        return true;
    }

    @Override
    public List<String> suggest(CommandSender sender, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return mineManager.getAll().stream()
                    .map(Mine::getId)
                    .filter(s -> s.toLowerCase().startsWith(prefix))
                    .toList();
        }
        return List.of();
    }
}
