package me.ekorn.EkorMines.common.command.ekormine;

import me.ekorn.EkorMines.common.ConfigService;
import me.ekorn.EkorMines.common.LoggingService;
import org.bukkit.command.CommandSender;

import java.util.List;

public class TestCommand implements SubCommand {
    private final LoggingService log;
    private final ConfigService config;



    public TestCommand(LoggingService log, ConfigService config) {
        this.log = log;
        this.config = config;
    }

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public String getDescription() {
        return "Just a test command for various tasks";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage("You performed the /ekm test command.");
        log.info("Debug enabled? " + config.isDebugMode());
        log.warn("Whats the locale? " + config.getLocale());
        log.severe("Say walla", "Something went horribly wrong");
        return true;
    }

    @Override
    public List<String> suggest(CommandSender sender, String[] args) {
        return List.of();
    }
}
