package net.silverstonemc.silverstonesurvival.events;

import me.rerere.matrix.api.MatrixAPIProvider;
import net.silverstonemc.silverstonesurvival.SilverstoneMain;
import net.silverstonemc.silverstonesurvival.commands.Fly;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public record Load(JavaPlugin plugin) implements Listener {

    @EventHandler
    public void onLoad(ServerLoadEvent event) {
        if (plugin.getServer().getPluginManager().getPlugin("Matrix") != null) {
            SilverstoneMain.matrix = MatrixAPIProvider.getAPI();
            plugin.getServer().getPluginManager().registerEvents(new Fly(), plugin);
            plugin.getLogger().info("Hooked into the Matrix api!");
        }
    }
}
