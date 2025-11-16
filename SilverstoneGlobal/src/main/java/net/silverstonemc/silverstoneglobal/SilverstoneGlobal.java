package net.silverstonemc.silverstoneglobal;

import me.rerere.matrix.api.MatrixAPI;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.ess3.api.IEssentials;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.silverstonemc.silverstoneglobal.commands.*;
import net.silverstonemc.silverstoneglobal.commands.guis.BuyGUI;
import net.silverstonemc.silverstoneglobal.commands.guis.ChatColorGUI;
import net.silverstonemc.silverstoneglobal.commands.guis.Tips;
import net.silverstonemc.silverstoneglobal.discord.Errors;
import net.silverstonemc.silverstoneglobal.discord.TPS;
import net.silverstonemc.silverstoneglobal.discord.Vanish;
import net.silverstonemc.silverstoneglobal.events.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SuppressWarnings("DataFlowIssue")
public class SilverstoneGlobal extends JavaPlugin implements Listener {
    public static JDA jda;
    public static MatrixAPI matrix;

    private static SilverstoneGlobal instance;
    private Errors errors;
    private IEssentials essentials;
    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        instance = this;
        essentials = (IEssentials) getServer().getPluginManager().getPlugin("Essentials");
        luckPerms = getServer().getServicesManager().load(LuckPerms.class);

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager()
            .getRegistration(LuckPerms.class);
        if (provider != null) //noinspection ResultOfMethodCallIgnored
            provider.getProvider();

        saveDefaultConfig();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (getConfig().getString("discord-token").isBlank()) {
                    getLogger().warning("Token is empty! Skipping Discord bot...");
                    return;
                }

                getLogger().info("Starting Discord bot...");
                JDABuilder builder = JDABuilder.createDefault(getConfig().getString("discord-token"));
                builder.disableIntents(GatewayIntent.GUILD_MESSAGE_TYPING);
                builder.disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE);
                builder.setMemberCachePolicy(MemberCachePolicy.NONE);
                builder.setStatus(OnlineStatus.ONLINE);
                builder.setEnableShutdownHook(false);
                // #serverSpecific
                // Set vanish button listener
                if (getConfig().getString("server").equalsIgnoreCase("minigames")) builder.addEventListeners(
                    new Vanish(instance));
                switch (getConfig().getString("server")) {
                    case "minigames" -> builder.setActivity(Activity.customStatus("Playing minigames"));
                    case "creative" -> builder.setActivity(Activity.customStatus("Being creative"));
                    case "survival" -> builder.setActivity(Activity.customStatus("Surviving"));
                }
                jda = builder.build();

                try {
                    jda.awaitReady();

                    // #serverSpecific
                    // Send vanish buttons if not already sent
                    if (getConfig().getString("server").equalsIgnoreCase("minigames")) {
                        TextChannel channel = jda.getTextChannelById(1075640285083734067L);
                        //noinspection DataFlowIssue
                        if (channel.getIterableHistory().takeAsync(1).thenApply(ArrayList::new).get(
                            30, TimeUnit.SECONDS).isEmpty())
                            channel.sendMessage("## Select a vanish state").addComponents(ActionRow.of(
                                Button.success("vanish-on", "Vanish"),
                                Button.danger("vanish-off", "Un-vanish"))).queue();
                    }

                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    throw new RuntimeException(e);
                }

                errors = new Errors(instance);
                errors.start();
            }
        }.runTaskAsynchronously(this);

        getCommand("bclag").setExecutor(new Broadcasts());
        getCommand("bcnolag").setExecutor(new Broadcasts());
        getCommand("bcrestart").setExecutor(new Broadcasts());
        getCommand("bcshutdown").setExecutor(new Broadcasts());
        getCommand("buy").setExecutor(new BuyGUI(this));
        getCommand("bwarn").setExecutor(new BackendWarn());
        getCommand("centeronblock").setExecutor(new CenterOnBlock());
        getCommand("chatcolor").setExecutor(new ChatColorGUI());
        getCommand("effects").setExecutor(new Effects());
        getCommand("exit").setExecutor(new Exit(this));
        getCommand("forcerestart").setExecutor(new Restart(this));
        getCommand("freezeserver").setExecutor(new Freeze());
        getCommand("ggamerule").setExecutor(new GlobalGameRule());
        getCommand("ggamerule").setTabCompleter(new TabComplete());
        getCommand("glist").setExecutor(new Glist());
        getCommand("help").setExecutor(new Help(this));
        getCommand("listops").setExecutor(new ListOPs(this));
        getCommand("live").setExecutor(new Live(this));
        getCommand("localchat").setExecutor(new LocalChat());
        getCommand("monitortps").setExecutor(new TPSMonitor(this));
        getCommand("nv").setExecutor(new NightVision());
        getCommand("quickrestart").setExecutor(new Restart(this));
        getCommand("restart").setExecutor(new Restart(this));
        getCommand("restartwhenempty").setExecutor(new Restart(this));
        getCommand("schedulerestart").setExecutor(new Restart(this));
        getCommand("spectate").setExecutor(new Spectate(this));
        getCommand("stuck").setExecutor(new Stuck());
        getCommand("tips").setExecutor(new Tips(this));
        getCommand("tpchunk").setExecutor(new TP());
        getCommand("tpregion").setExecutor(new TP());
        getCommand("updatecommands").setExecutor(new UpdateCommands());
        getCommand("watch").setExecutor(new Spectate(this));

        PluginManager pluginManager = getServer().getPluginManager();
        if (essentials != null) pluginManager.registerEvents(new AFK(this), this);
        pluginManager.registerEvents(new CancelChat(), this);
        pluginManager.registerEvents(new Gamemode(), this);
        pluginManager.registerEvents(new JoinAndLeave(this), this);
        pluginManager.registerEvents(new Kick(), this);
        pluginManager.registerEvents(new ServerLoad(this), this);
        pluginManager.registerEvents(new TabComplete(), this);

        getServer().getMessenger().registerIncomingPluginChannel(
            this,
            "silverstone:pluginmsg",
            new PluginMessages());
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "silverstone:pluginmsg");

        // #serverSpecific
        // Don't run security checks on survival or events servers
        String serverName = getConfig().getString("server");
        if (!serverName.equalsIgnoreCase("survival") && !serverName.equalsIgnoreCase("events")) {
            new Security(this).check();

            new BukkitRunnable() {
                @Override
                public void run() {
                    Whitelist.whitelist();
                }
            }.runTaskTimer(this, 100, 18000);
        }

        // #serverSpecific
        // Don't run these methods on the events server
        if (serverName.equalsIgnoreCase("events")) return;

        new BukkitRunnable() {
            final TPS tps = new TPS();

            @Override
            public void run() {
                tps.checkTPS();
            }
        }.runTaskTimerAsynchronously(this, 300, 60);

        new BukkitRunnable() {
            final Heartbeat heartbeat = new Heartbeat(instance);

            @Override
            public void run() {
                heartbeat.sendHeartbeat();
            }
        }.runTaskTimerAsynchronously(this, 200, 1200);
    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);

        // #serverSpecific
        // Don't stop the following on the events server (it doesn't have a bot)
        if (getConfig().getString("server").equalsIgnoreCase("events")) return;

        errors.dumpQueue();
        errors.remove();

        getLogger().info("Shutting down Discord bot...");
        try {
            if (jda == null) return; // The bot was never started

            // Initating the shutdown, this closes the gateway connection and subsequently closes the requester queue
            jda.shutdown();
            // Allow at most 10 seconds for remaining requests to finish
            if (!jda.awaitShutdown(
                10,
                TimeUnit.SECONDS)) { // returns true if shutdown is graceful, false if timeout exceeded
                jda.shutdownNow(); // Cancel all remaining requests, and stop thread-pools
                jda.awaitShutdown(); // Wait until shutdown is complete (indefinitely)
            }
        } catch (NoClassDefFoundError | InterruptedException ignored) {
        }
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        saveDefaultConfig();
        reloadConfig();
        sender.sendMessage(Component.text("SilverstoneGlobal reloaded!", NamedTextColor.GREEN));
        return true;
    }

    public static SilverstoneGlobal getInstance() {
        return instance;
    }

    public IEssentials getEssentials() {
        return essentials;
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }
}
