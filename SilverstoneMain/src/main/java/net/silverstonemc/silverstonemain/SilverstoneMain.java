package net.silverstonemc.silverstonemain;

import com.onarandombox.MultiverseCore.MultiverseCore;
import github.scarsz.discordsrv.DiscordSRV;
import net.ess3.api.IEssentials;
import net.luckperms.api.LuckPerms;
import net.silverstonemc.silverstonemain.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("ConstantConditions")
public class SilverstoneMain extends JavaPlugin implements Listener {

    private LuckPerms luckPerms;
    private IEssentials essentials;
    private MultiverseCore multiverse;

    public static DataManager data;

    private static SilverstoneMain instance;

    public static SilverstoneMain getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        luckPerms = getServer().getServicesManager().load(LuckPerms.class);
        essentials = (IEssentials) getServer().getPluginManager().getPlugin("Essentials");
        multiverse = (MultiverseCore) getServer().getPluginManager().getPlugin("Multiverse-Core");

        data = new DataManager(this);

        saveDefaultConfig();
        data.saveDefaultConfig();

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null)
            //noinspection ResultOfMethodCallIgnored
            provider.getProvider();

        if (getServer().getPluginManager().getPlugin("DiscordSRV") != null)
            DiscordSRV.api.subscribe(new DiscordReady(this));

        getCommand("allowend").setExecutor(new End(this));
        getCommand("dragon").setExecutor(new End(this));
        getCommand("enablecollision").setExecutor(new EnableCollision());
        getCommand("gettingstarted").setExecutor(new GettingStarted());
        getCommand("homec").setExecutor(new Homes(this));
        getCommand("homes?").setExecutor(new Homes(this));
        getCommand("keepinv").setExecutor(new KeepInv(this));
        getCommand("regenend").setExecutor(new End(this));
        getCommand("relay").setExecutor(new DiscordRelay(this));
        getCommand("rtplimit").setExecutor(new RTPLimit(this));
        getCommand("znewbiekit").setExecutor(new NewbieKit(this));
        getCommand("hideitemframe").setExecutor(new HideItemFrame(this));
        getCommand("masshideitemframe").setExecutor(new HideItemFrame(this));

        getCommand("rtplimit").setTabCompleter(new TabComplete());
        getCommand("ssm").setTabCompleter(new TabComplete());

        PluginManager pluginManager = this.getServer().getPluginManager();

        pluginManager.registerEvents(new AFK(), this);
        pluginManager.registerEvents(new Chat(this), this);
        pluginManager.registerEvents(new Death(), this);
        pluginManager.registerEvents(new End(this), this);
        pluginManager.registerEvents(new EndJoin(this), this);
        pluginManager.registerEvents(new JoinEvent(this), this);
        pluginManager.registerEvents(new JoinLeaveSpam(this), this);
        pluginManager.registerEvents(new QuitEvent(), this);
        pluginManager.registerEvents(new RTPLimit(this), this);
        pluginManager.registerEvents(new TabComplete(), this);
        pluginManager.registerEvents(new Tips(this), this);
        pluginManager.registerEvents(new WirelessButtons(), this);
        pluginManager.registerEvents(new HideItemFrame(this), this);
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

    public IEssentials getEssentials() {
        return essentials;
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public MultiverseCore getMVCore() {
        return multiverse;
    }
}
