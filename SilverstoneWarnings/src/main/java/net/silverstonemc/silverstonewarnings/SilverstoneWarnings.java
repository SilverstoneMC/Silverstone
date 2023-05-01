package net.silverstonemc.silverstonewarnings;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.silverstonemc.silverstonewarnings.commands.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SilverstoneWarnings extends Plugin implements Listener {
    public static JDA jda;

    private static BungeeAudiences adventure;
    private static SilverstoneWarnings plugin;

    @Override
    public void onEnable() {
        plugin = this;
        new ConfigurationManager().initialize();
        adventure = BungeeAudiences.create(this);

        // Cache users
        for (String key : ConfigurationManager.userCache.getSection("users").getKeys()) {
            UUID uuid = UUID.fromString(key);
            String username = ConfigurationManager.userCache.getString("users." + key);
            UserManager.playerMap.put(uuid, username);
        }

        new Thread(() -> {
            getLogger().info("Starting Discord bot...");
            JDABuilder builder = JDABuilder.createDefault(new Secrets().getBotToken());
            builder.disableIntents(GatewayIntent.GUILD_MESSAGE_TYPING);
            builder.disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE);
            builder.setMemberCachePolicy(MemberCachePolicy.NONE);
            builder.setStatus(OnlineStatus.ONLINE);
            builder.setActivity(Activity.watching("for cheaters"));
            builder.setEnableShutdownHook(false);
            builder.addEventListeners(new DiscordEvents());
            jda = builder.build();
        }, "Discord Bot").start();

        PluginManager pluginManager = getProxy().getPluginManager();

        Runnable task = () -> {
            getLogger().info("Registering commands...");
            pluginManager.registerCommand(plugin, new ReasonsCommand());
            pluginManager.registerCommand(plugin, new BaseCommand());
            pluginManager.registerCommand(plugin, new RelayCommand());
            pluginManager.registerCommand(plugin, new WarnCommand());
            pluginManager.registerCommand(plugin, new WarningsCommand());
            pluginManager.registerCommand(plugin, new WarnListCommand());
            pluginManager.registerCommand(plugin, new WarnQueueCommand());
        };
        getProxy().getScheduler().schedule(this, task, 7, TimeUnit.SECONDS);

        pluginManager.registerListener(this, new JoinEvent());
        pluginManager.registerListener(this, new QuitEvent());
    }

    @Override
    public void onDisable() {
        if (adventure != null) {
            adventure.close();
            adventure = null;
        }

        plugin.getLogger().info("Shutting down Discord bot...");
        try {
            // Initating the shutdown, this closes the gateway connection and subsequently closes the requester queue
            jda.shutdown();
            // Allow at most 10 seconds for remaining requests to finish
            if (!jda.awaitShutdown(10,
                TimeUnit.SECONDS)) { // returns true if shutdown is graceful, false if timeout exceeded
                jda.shutdownNow(); // Cancel all remaining requests, and stop thread-pools
                jda.awaitShutdown(); // Wait until shutdown is complete (indefinitely)
            }
        } catch (NoClassDefFoundError | InterruptedException ignored) {
        }
    }

    public static BungeeAudiences getAdventure() {
        if (adventure == null)
            throw new IllegalStateException("Cannot retrieve audience provider while plugin is not enabled");
        return adventure;
    }

    public static SilverstoneWarnings getPlugin() {
        return plugin;
    }
}
