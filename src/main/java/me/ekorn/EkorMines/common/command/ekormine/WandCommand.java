package me.ekorn.EkorMines.common.command.ekormine;

import me.ekorn.EkorMines.mine.domain.MineWand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class WandCommand implements SubCommand {
    private final MineWand mineWand;

    public WandCommand(MineWand mineWand) {
        this.mineWand = mineWand;
    }

    @Override
    public String getName() {
        return "wand";
    }

    @Override
    public String getDescription() {
        return "Gives the Mine Wand";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players may perform this command.");
            return true;
        }
        player.getInventory().addItem(mineWand.create());
        player.sendMessage("You have been given the Mine Wand!");
        return true;
    }

    @Override
    public List<String> suggest(CommandSender sender, String[] args) {
        return List.of();
    }
}
