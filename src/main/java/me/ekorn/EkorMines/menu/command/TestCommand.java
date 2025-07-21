package me.ekorn.EkorMines.menu.command;

import me.ekorn.EkorMines.menu.api.MenuManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TestCommand implements CommandExecutor {

    private final MenuManager menuManager;

    public TestCommand(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can open the Test Menu.");
            return true;
        }
        sender.sendMessage("Hey niga");
        menuManager.open("testmenu", player);
        return true;
    }
}
