package me.ekorn.EkorMines.mine.listener;

import me.ekorn.EkorMines.mine.domain.MineWand;
import me.ekorn.EkorMines.mine.service.CreationService;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class MineWandListener implements Listener {
    private final MineWand mineWand;
    private final CreationService creation;

    public MineWandListener(MineWand mineWand, CreationService creation) {
        this.mineWand = mineWand;
        this.creation = creation;
    }


    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!mineWand.isWand(event.getItem())) return;
        if (!event.getAction().name().contains("BLOCK")) return;
        event.setCancelled(true);
        Player player = event.getPlayer();
        if (!creation.hasSession(player.getUniqueId())) return;
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block b = event.getClickedBlock();
            if (b == null) return;
            Location loc = b.getLocation();
            creation.setFirstCorner(player.getUniqueId(), loc);
            player.sendMessage(String.format("§eYou set first corner to (%d, %d, %d)!", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block b = event.getClickedBlock();
            if (b == null) return;
            Location loc = b.getLocation();
            creation.setSecondCorner(player.getUniqueId(), loc);
            player.sendMessage(String.format("§eYou set second corner to (%d, %d, %d)!", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            return;
        }
    }
}
