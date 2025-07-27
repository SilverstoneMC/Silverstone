package net.silverstonemc.silverstoneminigames.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class Void implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getLocation().getBlockY() <= -90)
            if (!player.isFlying()) Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "spawn " + player.getName());
    }
}