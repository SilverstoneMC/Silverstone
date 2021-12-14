package net.silverstonemc.silverstonemain;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public record EndJoin(JavaPlugin plugin) implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getName().equalsIgnoreCase(plugin.getConfig().getString("end-world")))
            if (player.getGameMode() == GameMode.ADVENTURE || player.getGameMode() == GameMode.SURVIVAL)
                if (!player.isInvulnerable()) {
                    player.setInvulnerable(true);
                    BukkitRunnable task = new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.setInvulnerable(false);
                        }
                    };
                    task.runTaskLater(plugin, 60);
                }
    }
}
