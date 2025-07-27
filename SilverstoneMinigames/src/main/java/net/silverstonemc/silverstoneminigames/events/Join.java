package net.silverstonemc.silverstoneminigames.events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Join implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        String worldName = "utility";
        Player player = event.getPlayer();
        if (player.getLocation().getWorld().getName().equalsIgnoreCase(worldName))
            return;
        if (player.getGameMode() != GameMode.ADVENTURE) return;

        Location spawn = new Location(
            Bukkit.getWorld(worldName),
            -87.5,
            41.0,
            -26.5,
            0.0f,
            -90.0f);
        player.teleportAsync(spawn);
    }
}