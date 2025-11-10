package net.silverstonemc.silverstonecreative;

import com.plotsquared.core.PlotAPI;
import net.silverstonemc.silverstonecreative.events.PlotSquaredListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SilverstoneCreative extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        PlotAPI plotAPI = new PlotAPI();
        plotAPI.registerListener(new PlotSquaredListener());

        getLogger().info("Enabled PlotSquared integration!");
    }
}
