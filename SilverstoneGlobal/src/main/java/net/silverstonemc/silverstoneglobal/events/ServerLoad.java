package net.silverstonemc.silverstoneglobal.events;

import me.rerere.matrix.api.MatrixAPIProvider;
import net.silverstonemc.silverstoneglobal.SilverstoneGlobal;
import net.silverstonemc.silverstoneglobal.discord.AntiCheat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerLoad implements Listener {
    public ServerLoad(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    private final JavaPlugin plugin;
    
    @EventHandler
    public void onLoad(ServerLoadEvent event) {
        if (plugin.getServer().getPluginManager().getPlugin("Matrix") != null) {
            SilverstoneGlobal.matrix = MatrixAPIProvider.getAPI();
            plugin.getServer().getPluginManager().registerEvents(new AntiCheat(), plugin);
            plugin.getLogger().info("Hooked into the Matrix api!");
        }
    }
}
