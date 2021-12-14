package net.silverstonemc.silverstonewarnings;

import net.silverstonemc.silverstonewarnings.commands.WarnCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public record JoinEvent(JavaPlugin plugin) implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (!SilverstoneWarnings.queue.getConfig().contains("queue." + uuid)) return;
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                WarnCommand.warn(uuid, SilverstoneWarnings.queue.getConfig().getString("queue." + uuid));
                SilverstoneWarnings.queue.getConfig().set("queue." + uuid, null);
                SilverstoneWarnings.queue.saveConfig();
            }
        };
        task.runTaskLater(plugin, 60);
    }
}
