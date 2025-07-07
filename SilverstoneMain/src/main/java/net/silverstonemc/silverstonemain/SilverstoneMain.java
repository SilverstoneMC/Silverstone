package net.silverstonemc.silverstonemain;

import net.ess3.api.IEssentials;
import net.silverstonemc.silverstonemain.commands.GettingStarted;
import net.silverstonemc.silverstonemain.commands.JHome;
import net.silverstonemc.silverstonemain.events.WirelessButtons;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings({
    "DataFlowIssue",
    "unused"
})
public class SilverstoneMain extends JavaPlugin implements Listener {
    public static SilverstoneMain getInstance() {
        return instance;
    }

    private static SilverstoneMain instance;
    private IEssentials essentials;

    @Override
    public void onEnable() {
        instance = this;
        essentials = (IEssentials) getServer().getPluginManager().getPlugin("Essentials");

        getCommand("gettingstarted").setExecutor(new GettingStarted());
        getCommand("homec").setExecutor(new JHome(this));

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new WirelessButtons(), this);

        getServer().getMessenger().registerOutgoingPluginChannel(this, "silverstone:pluginmsg");
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    public IEssentials getEssentials() {
        return essentials;
    }
}
