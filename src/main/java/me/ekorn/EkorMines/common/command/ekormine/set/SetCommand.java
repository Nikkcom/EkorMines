package me.ekorn.EkorMines.common.command.ekormine.set;

import me.ekorn.EkorMines.common.command.ekormine.SubCommand;
import me.ekorn.EkorMines.mine.service.MineManager;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetCommand implements SubCommand {
    private final MineManager mineManager;

    public SetCommand(MineManager mineManager) {
        this.mineManager = mineManager;
        subs.put("spawn", new SetSpawnSub(mineManager));
        subs.put("direction", new SetDirectionSub(mineManager));
        subs.put("interval", new SetIntervalSub(mineManager));
        subs.put("threshold", new SetThresholdSub(mineManager));
        subs.put("display", new SetDisplaySub(mineManager));
    }

    private final Map<String, SubCommand> subs = new HashMap<>();

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "Modify a property on an existing mine";
    }

    @Override
    public String getUsage() {
        return "<spawn|direction|interval|threshold|display> <id> [values...]";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        // need at least the property name and the mineId
        if (args.length < 2) {
            sender.sendMessage("Usage: /ekm set " + getUsage());
            return true;
        }
        String which = args[0].toLowerCase();
        SubCommand op = subs.get(which);
        if (op == null) {
            sender.sendMessage("Unknown setting '" + which + "'.");
            return true;
        }
        // delegate, dropping the first arg
        String[] childArgs = Arrays.copyOfRange(args, 1, args.length);
        return op.execute(sender, childArgs);
    }

    @Override
    public List<String> suggest(CommandSender sender, String[] args) {
        // 1) no args yet → suggest the five child names
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return subs.keySet().stream()
                    .filter(k -> k.startsWith(prefix))
                    .toList();
        }
        // 2) child name present
        SubCommand op = subs.get(args[0].toLowerCase());
        if (op == null) return List.of();
        // 3) delegate, again dropping the first slot
        String[] childArgs = Arrays.copyOfRange(args, 1, args.length);
        List<String> s = op.suggest(sender, childArgs);
        // 4) filter suggestions by the *current* token
        String cur = childArgs[childArgs.length - 1].toLowerCase();
        return s.stream()
                .filter(str -> str.toLowerCase().startsWith(cur))
                .toList();
    }
}
