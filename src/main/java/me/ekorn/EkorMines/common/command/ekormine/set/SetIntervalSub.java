package me.ekorn.EkorMines.common.command.ekormine.set;

import me.ekorn.EkorMines.common.command.ekormine.SubCommand;
import me.ekorn.EkorMines.mine.service.MineManager;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SetIntervalSub implements SubCommand {

    private final MineManager mineManager;

    public SetIntervalSub(MineManager mineManager) {
        this.mineManager = mineManager;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        return false;
    }

    @Override
    public List<String> suggest(CommandSender sender, String[] args) {
        return List.of();
    }
}
