package net.silverstonemc.silverstonemain;

import github.scarsz.discordsrv.DiscordSRV;
import net.silverstonemc.silverstonemain.commands.GettingStarted;
import net.silverstonemc.silverstonemain.events.JoinEvent;
import net.silverstonemc.silverstonemain.events.JoinLeaveSpam;
import net.silverstonemc.silverstonemain.events.QuitEvent;
import net.silverstonemc.silverstonemain.events.WirelessButtons;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("DataFlowIssue")
public class SilverstoneSurvival extends JavaPlugin implements Listener {

    public static DataManager data;
    private static SilverstoneSurvival instance;

    public static SilverstoneSurvival getInstance() {
        return instance;
    }

    //todo merge all into SilverstoneMinigames

    @Override
    public void onEnable() {
        instance = this;

        data = new DataManager(this);

        saveDefaultConfig();
        data.saveDefaultConfig();

        if (getServer().getPluginManager().getPlugin("DiscordSRV") != null)
            DiscordSRV.api.subscribe(new DiscordReady(this));

        getCommand("gettingstarted").setExecutor(new GettingStarted());

        getCommand("rtplimit").setTabCompleter(new TabComplete());
        getCommand("ssm").setTabCompleter(new TabComplete());
        getCommand("claimpoints").setTabCompleter(new TabComplete());

        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new JoinEvent(this), this);
        pluginManager.registerEvents(new JoinLeaveSpam(this), this);
        pluginManager.registerEvents(new QuitEvent(), this);
        pluginManager.registerEvents(new TabComplete(), this);
        pluginManager.registerEvents(new WirelessButtons(), this);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length > 0) if (args[0].equalsIgnoreCase("reload")) {
            saveDefaultConfig();
            data.saveDefaultConfig();
            reloadConfig();
            data.reloadConfig();
            data = new DataManager(this);
            sender.sendMessage(ChatColor.GREEN + "SilverstoneMain reloaded!");
            return true;
        }
        return false;
    }
}
