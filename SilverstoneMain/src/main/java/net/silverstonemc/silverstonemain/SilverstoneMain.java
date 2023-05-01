package net.silverstonemc.silverstonemain;

import github.scarsz.discordsrv.DiscordSRV;
import net.ess3.api.IEssentials;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.silverstonemc.silverstonemain.commands.GettingStarted;
import net.silverstonemc.silverstonemain.commands.Home;
import net.silverstonemc.silverstonemain.events.JoinEvent;
import net.silverstonemc.silverstonemain.events.JoinLeaveSpam;
import net.silverstonemc.silverstonemain.events.WirelessButtons;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"DataFlowIssue", "unused"})
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

        saveDefaultConfig();

        if (getServer().getPluginManager().getPlugin("DiscordSRV") != null)
            DiscordSRV.api.subscribe(new DiscordReady(this));

        getCommand("gettingstarted").setExecutor(new GettingStarted());
        getCommand("homec").setExecutor(new Home(this));

        getCommand("ssm").setTabCompleter(new TabComplete());

        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new JoinEvent(), this);
        pluginManager.registerEvents(new JoinLeaveSpam(this), this);
        pluginManager.registerEvents(new TabComplete(), this);
        pluginManager.registerEvents(new WirelessButtons(), this);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length > 0) if (args[0].equalsIgnoreCase("reload")) {
            saveDefaultConfig();
            reloadConfig();
            sender.sendMessage(Component.text("SilverstoneMain reloaded!").color(NamedTextColor.GREEN));
            return true;
        }
        return false;
    }

    public IEssentials getEssentials() {
        return essentials;
    }
}
