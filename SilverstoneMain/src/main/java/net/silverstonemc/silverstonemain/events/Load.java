package net.silverstonemc.silverstonemain.events;

import me.rerere.matrix.api.MatrixAPIProvider;
import net.silverstonemc.silverstonemain.SilverstoneMain;
import net.silverstonemc.silverstonemain.commands.Fly;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public record Load(JavaPlugin plugin) implements Listener {

    @EventHandler
    public void onLoad(ServerLoadEvent event) {
        if (plugin.getServer().getPluginManager().getPlugin("Matrix") != null) {
            new SilverstoneMain().setMatrix(MatrixAPIProvider.getAPI());
            plugin.getServer().getPluginManager().registerEvents(new Fly(), plugin);
            plugin.getLogger().info("Hooked into the Matrix api!");
        }
    }
}
