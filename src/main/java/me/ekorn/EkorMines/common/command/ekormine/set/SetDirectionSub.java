package me.ekorn.EkorMines.common.command.ekormine.set;

import me.ekorn.EkorMines.common.command.ekormine.SubCommand;
import me.ekorn.EkorMines.mine.domain.Mine;
import me.ekorn.EkorMines.mine.domain.ResetDirection;
import me.ekorn.EkorMines.mine.service.MineManager;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SetDirectionSub implements SubCommand {
    private final MineManager mineManager;

    public SetDirectionSub(MineManager mineManager) {
        this.mineManager = mineManager;
    }

    @Override
    public String getName() {
        return "direction";
    }

    @Override
    public String getDescription() {
        return "Sets the direction the mine resets";
    }

    @Override
    public String getUsage() {
        return "<direction>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage("ARGS: " + Arrays.stream(args).toList());
        if (args.length != 2) {
            return false;
        }
        String id = args[0].toLowerCase();
        String dirRaw = args[1];
        Optional<Mine> mineOpt = mineManager.get(id);
        if (mineOpt.isEmpty()) {
            sender.sendMessage("Could not find the mine '" +id+ "'");
            return true;
        }
        ResetDirection dir;
        try {
            dir = ResetDirection.valueOf(dirRaw);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("Invalid reset direction!");
            return true;
        }
        if (mineManager.setResetDirection(id, dir)) {
            sender.sendMessage(String.format("You set the reset direction of '%s' to '%s'", id, dir.name()));
        } else {
            sender.sendMessage("Could not find the mine '" +id+ "'");
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
        if (args.length == 2) {
            String prefix = args[1].toLowerCase();
            return Arrays.stream(ResetDirection.values())
                    .map(ResetDirection::name)
                    .filter(s -> s.startsWith(prefix.toUpperCase()))
                    .toList();
        }
        return List.of();
    }
}
