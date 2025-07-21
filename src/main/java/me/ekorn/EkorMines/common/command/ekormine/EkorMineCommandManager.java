package me.ekorn.EkorMines.common.command.ekormine;

import me.ekorn.EkorMines.common.ConfigService;
import me.ekorn.EkorMines.common.LoggingService;
import me.ekorn.EkorMines.common.PluginContext;
import me.ekorn.EkorMines.common.ServiceRegistry;
import me.ekorn.EkorMines.common.command.ekormine.set.SetCommand;
import me.ekorn.EkorMines.menu.api.MenuManager;
import me.ekorn.EkorMines.mine.domain.MineWand;
import me.ekorn.EkorMines.mine.service.CreationService;
import me.ekorn.EkorMines.mine.service.MineManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class EkorMineCommandManager implements CommandExecutor, TabCompleter {
    private final Map<String, SubCommand> roots = new HashMap<>();

    public EkorMineCommandManager(PluginContext ctx) {
        ServiceRegistry ser = ctx.services();
        MineManager mineManager = ser.get(MineManager.class);
        LoggingService logging = ser.get(LoggingService.class);
        roots.put("help", new HelpCommand(roots));
        roots.put("create", new CreateCommand(ser.get(CreationService.class), mineManager, ser.get(MineWand.class)));
        roots.put("set", new SetCommand(mineManager));
        roots.put("wand", new WandCommand(ser.get(MineWand.class)));
        roots.put("info", new InfoCommand(mineManager));
        roots.put("delete", new DeleteCommand(mineManager));
        roots.put("list", new ListCommand(mineManager, logging));
        roots.put("teleport", new TeleportCommand(mineManager));
        roots.put("menu", new MenuCommand(mineManager, ser.get(MenuManager.class)));
        roots.put("test", new TestCommand(logging, ser.get(ConfigService.class)));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command cmd,
                             @NotNull String label,
                             @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            // Help? Syntax msg?
            return roots.get("help").execute(sender, args);
        }

        SubCommand sub = roots.get(args[0].toLowerCase());
        if (sub == null) {
            sender.sendMessage(String.format("Unknown subcommand, see /%s help", label));
            return true;
        }

        return sub.execute(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command cmd,
                                                @NotNull String label,
                                                @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return roots.keySet().stream()
                    .filter(name -> name.startsWith(prefix))
                    .sorted()
                    .collect(Collectors.toList());
        }

        String chosen = args[0].toLowerCase();
        SubCommand sub = roots.get(chosen);
        if (sub == null) {
            return Collections.emptyList();
        }

        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);

        List<String> suggestions = sub.suggest(sender, subArgs);
        if (suggestions == null) {
            return Collections.emptyList();
        }

        String current = subArgs[subArgs.length - 1].toLowerCase();
        return suggestions.stream()
                .filter(s -> s.toLowerCase().startsWith(current))
                .sorted()
                .collect(Collectors.toList());

    }

    public List<String> getRootNames() {
        return List.copyOf(roots.keySet());
    }

    public void registerRoot(String name, SubCommand sub) {
        roots.put(name, sub);
    }
}
