package me.ekorn.EkorMines.mine.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.ekorn.EkorMines.mine.domain.MineWand;
import me.ekorn.EkorMines.mine.domain.Region;
import me.ekorn.EkorMines.mine.service.CreationService;
import me.ekorn.EkorMines.mine.service.MineManager;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SessionChatListener implements Listener {
    private final JavaPlugin plugin;
    private final CreationService creation;
    private final MineManager mineManager;
    private final MineWand mineWand;

    public SessionChatListener(JavaPlugin plugin, CreationService creation, MineManager mineManager, MineWand mineWand) {
        this.plugin = plugin;
        this.creation = creation;
        this.mineManager = mineManager;
        this.mineWand = mineWand;
    }


   @EventHandler
   public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        if (!creation.hasSession(player.getUniqueId())) return;
        String plainTextMessage = PlainTextComponentSerializer.plainText().serialize(event.message());
        String msg = plainTextMessage.trim().toLowerCase();
        if (!msg.equalsIgnoreCase("cancel") && !msg.equalsIgnoreCase("complete")) return;
        event.setCancelled(true);
        if (msg.equalsIgnoreCase("cancel")) {
            creation.cancelSession(player.getUniqueId());
            player.sendMessage("§cMine creation canceled.");
            return;
        }

        var maybe = creation.completeSession(player.getUniqueId());
        if (maybe.isEmpty()) {
            player.sendMessage("§cYou must select both corners before completing.");
            return;
        }

        var session = maybe.get();
        Region region = new Region(session.getFirst().get(), session.getSecond().get());

       Bukkit.getScheduler().runTask(plugin, () -> {
           if (mineManager.create(session.getMineId(), region)) {
               player.sendMessage("§aMine '" + session.getMineId() + "' created!");
           } else {
               player.sendMessage("§cMine '" + session.getMineId() + "' already exists!");
           }
       });
   }
}
