package me.jasonhorkles.silverstonemain;

import com.onarandombox.MultiverseCore.MultiverseCore;
import github.scarsz.discordsrv.DiscordSRV;
import me.jasonhorkles.silverstonemain.commands.*;
import me.jasonhorkles.silverstonemain.managers.DataManager;
import me.jasonhorkles.silverstonemain.minigames.Void;
import me.jasonhorkles.silverstonemain.minigames.*;
import net.ess3.api.IEssentials;
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
public class SilverstoneMain extends JavaPlugin implements Listener {

    private LuckPerms luckPerms;
    private MultiverseCore core;
    private IEssentials essentials;

    public static DataManager data;

    private static SilverstoneMain instance;

    public static SilverstoneMain getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        luckPerms = getServer().getServicesManager().load(LuckPerms.class);
        core = (MultiverseCore) getServer().getPluginManager().getPlugin("Multiverse-Core");
        essentials = (IEssentials) getServer().getPluginManager().getPlugin("Essentials");

        data = new DataManager(this);

        saveDefaultConfig();
        data.saveDefaultConfig();

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null)
            provider.getProvider();

        if (getServer().getPluginManager().getPlugin("DiscordSRV") != null)
            DiscordSRV.api.subscribe(new DiscordReady(this));

        getCommand("allowend").setExecutor(new End(this));
        getCommand("doublejump").setExecutor(new DoubleJump(this));
        getCommand("dragon").setExecutor(new End(this));
        getCommand("enablecollision").setExecutor(new EnableCollision());
        getCommand("firework").setExecutor(new Firework());
        getCommand("fixlight").setExecutor(new Fixlight());
        getCommand("flyingcourse").setExecutor(new FlyingCourse(this));
        getCommand("gettingstarted").setExecutor(new GettingStarted());
        getCommand("help").setExecutor(new Help());
        getCommand("homec").setExecutor(new Homes(this));
        getCommand("homes?").setExecutor(new Homes(this));
        getCommand("hsheartbeat").setExecutor(new HideSeek(this));
        getCommand("hsrandomtaunt").setExecutor(new HideSeek(this));
        getCommand("hsresettauntpoints").setExecutor(new HideSeek(this));
        getCommand("htp").setExecutor(new Minigames(this));
        getCommand("keepinv").setExecutor(new KeepInv(this));
        getCommand("kitpvp").setExecutor(new KitPvPGUI(this));
        getCommand("live").setExecutor(new Live(this));
        getCommand("minigame").setExecutor(new Minigames(this));
        getCommand("plots?").setExecutor(new Plots());
        getCommand("rank").setExecutor(new Rank());
        getCommand("regenend").setExecutor(new End(this));
        getCommand("relay").setExecutor(new DiscordRelay(this));
        getCommand("rtplimit").setExecutor(new RTPLimit(this));
        getCommand("safk").setExecutor(new AFK());
        getCommand("spectate").setExecutor(new Spectate(this));
        getCommand("stopmusic").setExecutor(new Music());
        getCommand("taunt").setExecutor(new HideSeek(this));
        getCommand("tips").setExecutor(new Tips(this));
        getCommand("tntrun").setExecutor(new TNTRun(this));
        getCommand("togglemusic").setExecutor(new Music());
        getCommand("tpchunk").setExecutor(new TP());
        getCommand("tpregion").setExecutor(new TP());
        getCommand("watch").setExecutor(new Spectate(this));
        getCommand("zfcfinish").setExecutor(new FlyingCourse(this));
        getCommand("znewbiekit").setExecutor(new NewbieKit(this));

        getCommand("rtplimit").setTabCompleter(new TabComplete());
        getCommand("silverstonemain").setTabCompleter(new TabComplete());

        PluginManager pluginManager = this.getServer().getPluginManager();

        pluginManager.registerEvents(new AFK(), this);
        pluginManager.registerEvents(new AntiCheatDiscord(), this);
        pluginManager.registerEvents(new Back(), this);
        pluginManager.registerEvents(new Chat(this), this);
        pluginManager.registerEvents(new Death(this), this);
        pluginManager.registerEvents(new DoubleJump(this), this);
        pluginManager.registerEvents(new End(this), this);
        pluginManager.registerEvents(new EndJoin(this), this);
        pluginManager.registerEvents(new HideSeek(this), this);
        pluginManager.registerEvents(new IgnoreClaims(), this);
        pluginManager.registerEvents(new JoinEvent(this), this);
        pluginManager.registerEvents(new JoinLeaveSpam(this), this);
        pluginManager.registerEvents(new KitPvPGUI(this), this);
        pluginManager.registerEvents(new LockInv(), this);
        pluginManager.registerEvents(new Minigames(this), this);
        pluginManager.registerEvents(new PlotClaim(), this);
        pluginManager.registerEvents(new PvP(), this);
        pluginManager.registerEvents(new QuitEvent(this), this);
        pluginManager.registerEvents(new RTPLimit(this), this);
        pluginManager.registerEvents(new TabComplete(), this);
        pluginManager.registerEvents(new Tips(this), this);
        pluginManager.registerEvents(new Void(this), this);
        pluginManager.registerEvents(new WirelessButtons(), this);

        HideSeek.createInv();
        KitPvPGUI.createInv();
        Minigames.createHtpInv();
        Minigames.createGameInv();

        new FlyingCourse(this).updateFCScoreboard();

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                TPS.checkTPS();
            }
        };
        task.runTaskTimerAsynchronously(this, 600, 20);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length > 0) if (args[0].equalsIgnoreCase("reload")) {
            saveDefaultConfig();
            data.saveDefaultConfig();
            reloadConfig();
            data.reloadConfig();
            data = new DataManager(this);
            new FlyingCourse(this).updateFCScoreboard();
            sender.sendMessage(ChatColor.GREEN + "SilverstoneMain reloaded!");
            return true;
        }
        return false;
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public MultiverseCore getMVCore() {
        return core;
    }

    public IEssentials getEssentials() {
        return essentials;
    }
}
