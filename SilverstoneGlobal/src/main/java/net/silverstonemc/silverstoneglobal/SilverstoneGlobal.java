package net.silverstonemc.silverstoneglobal;

import me.rerere.matrix.api.MatrixAPI;
import net.luckperms.api.LuckPerms;
import net.silverstonemc.silverstoneglobal.commands.*;
import net.silverstonemc.silverstoneglobal.commands.guis.BuyGUI;
import net.silverstonemc.silverstoneglobal.commands.guis.ChatColorGUI;
import net.silverstonemc.silverstoneglobal.events.ChatnSounds;
import net.silverstonemc.silverstoneglobal.events.Gamemode;
import net.silverstonemc.silverstoneglobal.events.JoinnLeave;
import net.silverstonemc.silverstoneglobal.events.Load;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("DataFlowIssue")
public class SilverstoneGlobal extends JavaPlugin implements Listener {
    public static MatrixAPI matrix;

    private LuckPerms luckPerms;
    private static SilverstoneGlobal instance;

    // Startup
    @Override
    public void onEnable() {
        instance = this;
        luckPerms = getServer().getServicesManager().load(LuckPerms.class);

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager()
            .getRegistration(LuckPerms.class);
        if (provider != null)
            //noinspection ResultOfMethodCallIgnored
            provider.getProvider();

        saveDefaultConfig();

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        getCommand("bclag").setExecutor(new Broadcasts());
        getCommand("bcnolag").setExecutor(new Broadcasts());
        getCommand("bcrestart").setExecutor(new Broadcasts());
        getCommand("bcshutdown").setExecutor(new Broadcasts());
        getCommand("buy").setExecutor(new BuyGUI(this));
        getCommand("chatcolor").setExecutor(new ChatColorGUI(this));
        getCommand("chatsounds").setExecutor(new ChatnSounds(this));
        getCommand("discord").setExecutor(new Discord());
        getCommand("effects").setExecutor(new Effects());
        getCommand("exit").setExecutor(new Exit(this));
        getCommand("facepalm").setExecutor(new ChatEmotes());
        getCommand("forcerestart").setExecutor(new Restart(this));
        getCommand("freezeserver").setExecutor(new Freeze());
        getCommand("joinleavesounds").setExecutor(new ChatnSounds(this));
        getCommand("listops").setExecutor(new ListOPs(this));
        getCommand("live").setExecutor(new Live(this));
        getCommand("localchat").setExecutor(new LocalChat());
        getCommand("monitortps").setExecutor(new TPSMonitor(this));
        getCommand("nv").setExecutor(new NightVision());
        getCommand("quickrestart").setExecutor(new Restart(this));
        getCommand("restart").setExecutor(new Restart(this));
        getCommand("restartwhenempty").setExecutor(new Restart(this));
        getCommand("schedulerestart").setExecutor(new Restart(this));
        getCommand("shrug").setExecutor(new ChatEmotes());
        getCommand("silverstoneglobal").setTabCompleter(new TabComplete());
        getCommand("spectate").setExecutor(new Spectate(this));
        getCommand("tableflip").setExecutor(new ChatEmotes());
        getCommand("tpchunk").setExecutor(new TP());
        getCommand("tpregion").setExecutor(new TP());
        getCommand("updatecommands").setExecutor(new UpdateCommands());
        getCommand("watch").setExecutor(new Spectate(this));

        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new BuyGUI(this), this);
        pluginManager.registerEvents(new ChatColorGUI(this), this);
        pluginManager.registerEvents(new ChatnSounds(this), this);
        pluginManager.registerEvents(new Exit(this), this);
        pluginManager.registerEvents(new Gamemode(), this);
        pluginManager.registerEvents(new JoinnLeave(), this);
        pluginManager.registerEvents(new Load(this), this);
        pluginManager.registerEvents(new TabComplete(), this);
        pluginManager.registerEvents(new TimeOut(), this);

        BuyGUI.inv = BuyGUI.createInv();
        new ChatColorGUI(this).createDefaultInv();

        if (!getConfig().getString("server").equalsIgnoreCase("survival")) {
            new Security(this).check();

            new BukkitRunnable() {
                @Override
                public void run() {
                    Whitelist.whitelist();
                }
            }.runTaskTimer(this, 100, 18000);

            new BukkitRunnable() {
                @Override
                public void run() {
                    TPS.checkTPS();
                }
            }.runTaskTimerAsynchronously(this, 600, 20);
        }
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length > 0) if (args[0].equalsIgnoreCase("reload")) {
            saveDefaultConfig();
            reloadConfig();
            BuyGUI.inv = BuyGUI.createInv();
            sender.sendMessage(ChatColor.GREEN + "SilverstoneGlobal reloaded!");
            return true;
        }
        return false;
    }

    public static SilverstoneGlobal getInstance() {
        return instance;
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }
}
