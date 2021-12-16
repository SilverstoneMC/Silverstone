package net.silverstonemc.silverstonemain.commands;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class AFK implements Listener {

    // AFK at night
    @EventHandler
    public void onAFK(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().toLowerCase().startsWith("/afk")) {
            Player player = event.getPlayer();
            World world = player.getWorld();

            if (player.hasPermission("silverstone.moderator")) return;
            if (!world.getName().startsWith("survival")) return;

            // 6:30PM - 5:59AM
            if (world.getTime() >= 12500 && world.getTime() <= 23999) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You can't do /afk at night!");
            }
        }
    }
}
