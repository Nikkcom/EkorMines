package me.ekorn.EkorMines.common.command.ekormine;

import me.ekorn.EkorMines.common.LoggingService;
import me.ekorn.EkorMines.mine.domain.Mine;
import me.ekorn.EkorMines.mine.service.MineManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class ListCommand implements SubCommand {
    private final MineManager mineManager;
    private final LoggingService log;

    public ListCommand(MineManager mineManager, LoggingService loggingService) {
        this.mineManager = mineManager;
        this.log = loggingService;
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "Lists all Mines";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        List<String> ids = mineManager.getAll().stream()
                .map(Mine::getId)
                .sorted()
                .toList();
        if (ids.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No mines found.");
        } else {
            String joined = String.join(ChatColor.WHITE + ", " + ChatColor.GREEN, ids);
            sender.sendMessage(
                    ChatColor.GOLD + "Mines (" + ids.size() + "): " +
                            ChatColor.GREEN + joined
            );
        }
        return true;
    }

    @Override
    public List<String> suggest(CommandSender sender, String[] args) {
        return List.of();
    }
}
