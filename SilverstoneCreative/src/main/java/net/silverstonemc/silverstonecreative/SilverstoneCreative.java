package net.silverstonemc.silverstonecreative;

import com.plotsquared.core.PlotAPI;
import net.silverstonemc.silverstonecreative.events.PlotSquaredListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SilverstoneCreative extends JavaPlugin implements Listener {
    public static SilverstoneCreative getInstance() {
        return instance;
    }

    private static SilverstoneCreative instance;

    @Override
    public void onEnable() {
        instance = this;

        PlotAPI plotAPI = new PlotAPI();
        plotAPI.registerListener(new PlotSquaredListener());

        getLogger().info("Enabled PlotSquared integration!");
    }
}
