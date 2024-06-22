package net.silverstonemc.silverstoneglobal.events;

import me.rerere.matrix.api.MatrixAPIProvider;
import net.silverstonemc.silverstoneglobal.SilverstoneGlobal;
import net.silverstonemc.silverstoneglobal.discord.AntiCheat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public record ServerLoad(JavaPlugin plugin) implements Listener {
    // Add a delay for Matrix to finish starting
    @EventHandler
    public void onLoad(ServerLoadEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.getServer().getPluginManager().getPlugin("Matrix") != null) {
                    SilverstoneGlobal.matrix = MatrixAPIProvider.getAPI();
                    plugin.getServer().getPluginManager().registerEvents(new AntiCheat(), plugin);
                    plugin.getLogger().info("Hooked into the Matrix api!");
                }
            }
        }.runTaskLater(plugin, 60);
    }
}
