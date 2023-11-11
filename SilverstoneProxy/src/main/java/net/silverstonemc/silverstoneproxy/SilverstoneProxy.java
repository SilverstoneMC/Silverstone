package net.silverstonemc.silverstoneproxy;

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
import net.silverstonemc.silverstoneproxy.commands.*;
import net.silverstonemc.silverstoneproxy.commands.chatemotes.FacePalm;
import net.silverstonemc.silverstoneproxy.commands.chatemotes.Shrug;
import net.silverstonemc.silverstoneproxy.commands.chatemotes.TableFlip;
import net.silverstonemc.silverstoneproxy.events.DiscordButtons;
import net.silverstonemc.silverstoneproxy.events.Join;
import net.silverstonemc.silverstoneproxy.events.Leave;
import net.silverstonemc.silverstoneproxy.events.PluginMessage;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SilverstoneProxy extends Plugin implements Listener {
    public static JDA jda;

    private static BungeeAudiences adventure;
    private static SilverstoneProxy plugin;

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
            builder.addEventListeners(new DiscordButtons());
            jda = builder.build();
        }, "Discord Bot").start();

        PluginManager pluginManager = getProxy().getPluginManager();

        pluginManager.registerCommand(this, new BaseCommand());
        pluginManager.registerCommand(this, new Discord());
        pluginManager.registerCommand(this, new FacePalm());
        pluginManager.registerCommand(this, new Forums());
        pluginManager.registerCommand(this, new Mods());
        pluginManager.registerCommand(this, new Restart());
        pluginManager.registerCommand(this, new RestartWhenEmpty());
        pluginManager.registerCommand(this, new Rules());
        pluginManager.registerCommand(this, new Shrug());
        pluginManager.registerCommand(this, new TableFlip());

        Runnable task = () -> {
            pluginManager.registerCommand(plugin, new WarnReasons());
            pluginManager.registerCommand(plugin, new BaseCommand());
            pluginManager.registerCommand(plugin, new Relay());
            pluginManager.registerCommand(plugin, new Warn());
            pluginManager.registerCommand(plugin, new Warnings());
            pluginManager.registerCommand(plugin, new WarnList());
            pluginManager.registerCommand(plugin, new WarnQueue());
            getLogger().info("Warning commands registered!");
        };
        getProxy().getScheduler().schedule(this, task, 3, TimeUnit.SECONDS);

        pluginManager.registerListener(this, new Join());
        pluginManager.registerListener(this, new PluginMessage());
        pluginManager.registerListener(this, new Leave());

        getProxy().registerChannel("silverstone:pluginmsg");
    }

    @Override
    public void onDisable() {
        if (adventure != null) {
            adventure.close();
            adventure = null;
        }

        getLogger().info("Shutting down Discord bot...");
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

    public static SilverstoneProxy getPlugin() {
        return plugin;
    }
}
