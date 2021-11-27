package me.jasonhorkles.silverstoneglobal;

import me.jasonhorkles.silverstoneglobal.commands.*;
import me.jasonhorkles.silverstoneglobal.commands.guis.BuyGUI;
import me.jasonhorkles.silverstoneglobal.commands.guis.ChatColorGUI;
import me.jasonhorkles.silverstoneglobal.commands.guis.SocialGUI;
import net.luckperms.api.LuckPerms;
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

@SuppressWarnings("ConstantConditions")
public class SilverstoneGlobal extends JavaPlugin implements Listener {

    private LuckPerms luckPerms;

    private static SilverstoneGlobal instance;

    public static SilverstoneGlobal getInstance() {
        return instance;
    }

    // Startup
    @Override
    public void onEnable() {
        instance = this;
        luckPerms = getServer().getServicesManager().load(LuckPerms.class);

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null)
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
        getCommand("discord").setExecutor(new SocialGUI(this));
        getCommand("effects").setExecutor(new Effects());
        getCommand("facepalm").setExecutor(new ChatEmotes());
        getCommand("forcerestart").setExecutor(new Restart(this));
        getCommand("freezeserver").setExecutor(new Freeze());
        getCommand("github").setExecutor(new SocialGUI(this));
        getCommand("instagram").setExecutor(new SocialGUI(this));
        getCommand("joinleavesounds").setExecutor(new ChatnSounds(this));
        getCommand("listops").setExecutor(new ListOPs(this));
        getCommand("localchat").setExecutor(new LocalChat());
        getCommand("monitortps").setExecutor(new TPSMonitor(this));
        getCommand("nv").setExecutor(new NightVision());
        getCommand("optifine").setExecutor(new OptiFine());
        getCommand("quickrestart").setExecutor(new Restart(this));
        getCommand("restart").setExecutor(new Restart(this));
        getCommand("restartwhenempty").setExecutor(new Restart(this));
        getCommand("rules").setExecutor(new Rules(this));
        getCommand("schedulerestart").setExecutor(new Restart(this));
        getCommand("shrug").setExecutor(new ChatEmotes());
        getCommand("silverstoneglobal").setTabCompleter(new TabComplete());
        getCommand("social").setExecutor(new SocialGUI(this));
        getCommand("tableflip").setExecutor(new ChatEmotes());
        getCommand("twitch").setExecutor(new SocialGUI(this));
        getCommand("twitter").setExecutor(new SocialGUI(this));
        getCommand("updatecommands").setExecutor(new UpdateCommands());
        getCommand("youtube").setExecutor(new SocialGUI(this));

        PluginManager pluginManager = this.getServer().getPluginManager();

        pluginManager.registerEvents(new BuyGUI(this), this);
        pluginManager.registerEvents(new ChatColorGUI(this), this);
        pluginManager.registerEvents(new ChatnSounds(this), this);
        pluginManager.registerEvents(new Rules(this), this);
        pluginManager.registerEvents(new SocialGUI(this), this);
        pluginManager.registerEvents(new TimeOut(), this);
        pluginManager.registerEvents(new NightVision(), this);

        BuyGUI.defaultInv = BuyGUI.createInv(0);
        BuyGUI.inv1 = BuyGUI.createInv(1);
        BuyGUI.inv2 = BuyGUI.createInv(2);
        BuyGUI.inv3 = BuyGUI.createInv(3);
        BuyGUI.inv4 = BuyGUI.createInv(4);
        new ChatColorGUI(this).createDefaultInv();
        new Rules(this).createInv();
        SocialGUI.createInv();

        new Security(this).check();

        getServer().getMessenger().registerIncomingPluginChannel(this, "silverstone:pluginmsg", new ChatnSounds(this));

        BukkitRunnable whitelist = new BukkitRunnable() {
            @Override
            public void run() {
                Whitelist.whitelist();
            }
        };
        whitelist.runTaskTimer(this, 100, 18000);
    }

    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length > 0) if (args[0].equalsIgnoreCase("reload")) {
            saveDefaultConfig();
            reloadConfig();
            BuyGUI.defaultInv = BuyGUI.createInv(0);
            BuyGUI.inv1 = BuyGUI.createInv(1);
            BuyGUI.inv2 = BuyGUI.createInv(2);
            BuyGUI.inv3 = BuyGUI.createInv(3);
            BuyGUI.inv4 = BuyGUI.createInv(4);
            new Rules(this).createInv();
            SocialGUI.createInv();
            sender.sendMessage(ChatColor.GREEN + "SilverstoneGlobal reloaded!");
            return true;
        }
        return false;
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }
}
