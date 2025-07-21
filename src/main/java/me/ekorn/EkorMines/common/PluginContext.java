package me.ekorn.EkorMines.common;

import me.ekorn.EkorMines.EkorMines;
import me.ekorn.EkorMines.common.api.PluginService;
import me.ekorn.EkorMines.common.storage.SectionSerializer;
import me.ekorn.EkorMines.common.storage.Storage;
import me.ekorn.EkorMines.menu.api.MenuManager;
import me.ekorn.EkorMines.menu.command.TestCommand;
import me.ekorn.EkorMines.menu.listener.MenuListener;
import me.ekorn.EkorMines.menu.service.MenuManagerImpl;
import me.ekorn.EkorMines.mine.domain.Mine;
import me.ekorn.EkorMines.mine.domain.MineWand;
import me.ekorn.EkorMines.mine.listener.MineWandListener;
import me.ekorn.EkorMines.mine.listener.SessionChatListener;
import me.ekorn.EkorMines.mine.persistence.MinePersistence;
import me.ekorn.EkorMines.mine.persistence.YamlMinePersistence;
import me.ekorn.EkorMines.mine.persistence.serializer.*;
import me.ekorn.EkorMines.mine.service.*;
import me.ekorn.EkorMines.mine.persistence.YamlFileMineStorage;
import me.ekorn.EkorMines.common.command.ekormine.EkorMineCommandManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class PluginContext {

    public static final boolean DEBUG_ENABLED = true;

    private final JavaPlugin plugin;
    private final ServiceRegistry registry = new ServiceRegistry();

    public PluginContext(EkorMines plugin) {
        this.plugin = plugin;
        registerServices();
        initServices();
        registerEvents();
        registerCommands();
    }
    private void registerServices() {
        registry.register(JavaPlugin.class, plugin);

        // MineManager setup
        MineManager mineManager = initMineManager();
        registry.register(MineManager.class, mineManager);

        // MenuManager setup
        MenuManager menuManager = new MenuManagerImpl();
        registry.register(MenuManager.class, menuManager);

        // CreationService setup
        CreationService mineCreationService = new CreationService();
        registry.register(CreationService.class, mineCreationService);

        // MineWand service setup
        MineWand wand = initMineWand();
        registry.register(MineWand.class, wand);

        // Config Service - Default, global configuration
        ConfigService configService = new ConfigService(plugin);
        registry.register(ConfigService.class, configService);

        LoggingService loggingService = new LoggingService(plugin.getLogger());
        registry.register(LoggingService.class, loggingService);

    }

    private void initServices() {
        for (Object service : registry.all()) {
            if (service instanceof PluginService initService) {
                initService.init(this);
            }
        }
    }

    private void registerEvents() {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(new MenuListener(registry.get(MenuManager.class)), plugin);
        pm.registerEvents(new MineWandListener(registry.get(MineWand.class), registry.get(CreationService.class)), plugin);
        pm.registerEvents(new SessionChatListener(plugin,
                registry.get(CreationService.class),
                registry.get(MineManager.class),
                registry.get(MineWand.class)), plugin);
    }

    private void registerCommands() {
        plugin.getCommand("testmenu").setExecutor(new TestCommand(registry.get(MenuManager.class)));

        EkorMineCommandManager manager = new EkorMineCommandManager(this);
        plugin.getCommand("ekormine").setExecutor(manager);
        plugin.getCommand("ekormine").setTabCompleter(manager);
    }

    public ServiceRegistry services() {
        return registry;
    }

    private MineManager initMineManager() {
        File mineFolder = new File(plugin.getDataFolder(), "mines");
        if (!mineFolder.exists()) {
            mineFolder.mkdirs();
        }

        List<SectionSerializer<Mine>> serializers = List.of(
                new RegionSerializer(),
                new WeightedPickerSerializer(),
                new ResetPolicySerializer(),
                new BlockOrderingSerializer(),
                new MineMetadataSerializer()
        );

        Storage<String, Mine> storage =
                new YamlFileMineStorage(mineFolder, serializers);

        MinePersistence persistence = new YamlMinePersistence(storage);
        ResetScheduler scheduler = new BukkitResetScheduler(plugin);
        ThresholdService threshold = new DefaultThresholdService(scheduler, persistence);

        return new MineManager(this,persistence, scheduler, threshold);
    }

    private MineWand initMineWand() {
        ItemStack proto = new ItemStack(Material.STICK, 1);
        ItemMeta m = proto.getItemMeta();
        m.displayName(Component.text("Mine Wand", NamedTextColor.GREEN));
        m.lore(List.of(
                Component.text("Left-click: set corner #1", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                Component.text("Right-click: set corner #2", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
        ));
        m.setUnbreakable(true);
        m.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        m.addEnchant(Enchantment.MENDING, 1, true);
        proto.setItemMeta(m);

        return new MineWand(plugin, "mine_wand", proto);
    }
    public JavaPlugin plugin() {
        return this.plugin;
    }
    public void shutdown() {
        services().clear();
    }
}
