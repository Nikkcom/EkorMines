package me.ekorn.EkorMines.common.command.ekormine;

import me.ekorn.EkorMines.mine.domain.Mine;
import me.ekorn.EkorMines.mine.service.MineManager;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;

public class DeleteCommand implements SubCommand {
    private final MineManager mineManager;

    public DeleteCommand(MineManager mineManager) {
        this.mineManager = mineManager;
    }

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getDescription() {
        return "Permanently delete a mine";
    }

    @Override
    public String getUsage() {
        return "<mine>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            return false;
        }
        String id = args[0];
        if (mineManager.delete(id)) {
            sender.sendMessage(String.format("Successfully deleted mine '%s'", id));
            return true;
        } else {
            sender.sendMessage(String.format("Mine does not exists '%s'", id));
        }
        return true;
    }

    @Override
    public List<String> suggest(CommandSender sender, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return mineManager.getAll().stream()
                    .map(Mine::getId)
                    .filter(s -> s.startsWith(prefix))
                    .toList();
        }
        return List.of();
    }
}
