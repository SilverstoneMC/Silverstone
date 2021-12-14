package net.silverstonemc.silverstonemain.minigames;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public record Void(JavaPlugin plugin) implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getName().equalsIgnoreCase(plugin.getConfig().getString("empty-minigame-world")))
            if (player.getLocation().getBlockY() <= 0) if (!player.isFlying())
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "warp Mini " + player.getName());
    }
}