package me.ekorn.EkorMines.common.command.ekormine;

import me.ekorn.EkorMines.mine.domain.MineWand;
import me.ekorn.EkorMines.mine.service.CreationService;
import me.ekorn.EkorMines.mine.service.MineManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class CreateCommand implements SubCommand {
    private final CreationService creation;
    private final MineManager mineManager;
    private final MineWand mineWand;

    public CreateCommand(CreationService creation, MineManager mineManager, MineWand mineWand) {
        this.creation = creation;
        this.mineManager = mineManager;
        this.mineWand = mineWand;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Creates a mine";
    }

    @Override
    public String getUsage() {
        return "<mineId>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can create mines.");
            return true;
        }
        if (args.length != 1) return false;
        UUID u = player.getUniqueId();
        String id = args[0];

        if (mineManager.get(id).isPresent()) {
            player.sendMessage("§cMine '" +id+ "' already exists!");
            return true;
        }

        if (!creation.startSession(u, id)) {
            player.sendMessage("You already got a session!");
        }
        if (!mineWand.isWand(player.getInventory().getItemInMainHand())) {
            player.getInventory().addItem(mineWand.create());
        }
        player.sendMessage("§aStarted creation of mine '" + id + "'.");
        player.sendMessage("§aUse wand: left‐click = corner1, right‐click = corner2.");
        player.sendMessage("§aThen type “§ecomplete§a” or “§ecancel§a” in chat.");
        return true;
    }

    @Override
    public List<String> suggest(CommandSender sender, String[] args) {
        return List.of();
    }
}
