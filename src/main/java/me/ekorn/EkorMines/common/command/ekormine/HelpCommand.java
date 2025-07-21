package me.ekorn.EkorMines.common.command.ekormine;

import org.bukkit.command.CommandSender;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class HelpCommand implements SubCommand {
    private final Map<String, SubCommand> roots;

    public HelpCommand(Map<String, SubCommand> roots) {
        this.roots = roots;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Displays this help message";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage("Available /ekm commands:");
        roots.values().stream()
                .sorted(Comparator.comparing(SubCommand::getName))
                .forEach(sub -> {
                    String usage = "/ekm " + sub.getName()
                            + (sub.getUsage().isEmpty() ? "" : " " + sub.getUsage());
                    sender.sendMessage(" §e" + usage + " §7- " + sub.getDescription());
                });
        return true;
    }

    @Override
    public List<String> suggest(CommandSender sender, String[] args) {
        return List.of();
    }
}
