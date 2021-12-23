package net.silverstonemc.silverstonecreative;

import net.silverstonemc.silverstonecreative.commands.Plots;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings({"ConstantConditions", "unused"})
public class SilverstoneCreative extends JavaPlugin implements Listener {

    // Startup
    @Override
    public void onEnable() {
        getCommand("plots?").setExecutor(new Plots());

        PluginManager pluginManager = this.getServer().getPluginManager();

        pluginManager.registerEvents(new PlotClaim(), this);
    }
}
