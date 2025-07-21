package me.ekorn.EkorMines.common.command.ekormine;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {
    String getName();
    String getDescription();
    String getUsage();
    boolean execute(CommandSender sender, String[] args);
    List<String> suggest(CommandSender sender, String[] args);
}
