package net.silverstonemc.silverstoneminigames.commands;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class Back implements Listener {
    @EventHandler
    public void onBack(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().toLowerCase().startsWith("/back")) {
            Player player = event.getPlayer();

            if (player.hasPermission("silverstone.moderator")) return;

            event.setCancelled(true);
            player.performCommand("warp Mini");
        }
    }
}
