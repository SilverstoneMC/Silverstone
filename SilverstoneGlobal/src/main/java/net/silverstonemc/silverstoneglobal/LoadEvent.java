package net.silverstonemc.silverstoneglobal;

import me.rerere.matrix.api.MatrixAPIProvider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public record LoadEvent(JavaPlugin plugin) implements Listener {
    @EventHandler
    public void onLoad(ServerLoadEvent event) {
        if (plugin.getServer().getPluginManager().getPlugin("Matrix") != null) {
            SilverstoneGlobal.matrix = MatrixAPIProvider.getAPI();
            plugin.getServer().getPluginManager().registerEvents(new AntiCheatDiscord(), plugin);
            plugin.getLogger().info("Hooked into the Matrix api!");
        }
    }
}
