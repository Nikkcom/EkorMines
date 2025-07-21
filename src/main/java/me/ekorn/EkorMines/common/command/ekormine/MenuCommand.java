package me.ekorn.EkorMines.common.command.ekormine;

import me.ekorn.EkorMines.menu.api.MenuManager;
import me.ekorn.EkorMines.menu.domain.Menu;
import me.ekorn.EkorMines.menu.domain.menu.MinesMenu;
import me.ekorn.EkorMines.mine.service.MineManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MenuCommand implements SubCommand {

    private final MineManager mineManager;
    private final MenuManager menuManager;

    public MenuCommand(MineManager mineManager, MenuManager menuManager) {
        this.mineManager = mineManager;
        this.menuManager = menuManager;
    }

    @Override
    public String getName() {
        return "menu";
    }

    @Override
    public String getDescription() {
        return "Opens the Mines menu";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a Player to use this command.");
            return true;
        }

        menuManager.open(MinesMenu.KEY, player);
        player.sendMessage("You opened 'mines_menu'");
        return true;
    }

    @Override
    public List<String> suggest(CommandSender sender, String[] args) {
        return List.of();
    }
}
