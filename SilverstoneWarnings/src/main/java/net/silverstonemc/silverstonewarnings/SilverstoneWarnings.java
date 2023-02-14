package net.silverstonemc.silverstonewarnings;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.silverstonemc.silverstonewarnings.commands.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SilverstoneWarnings extends Plugin implements Listener {

    private static SilverstoneWarnings plugin;

    public static Configuration config;
    public static Configuration data;
    public static Configuration queue;
    public static Configuration userCache;

    public static JDA jda;

    @Override
    public void onEnable() {
        plugin = this;
        config = loadFile("config.yml");
        data = loadFile("data.yml");
        queue = loadFile("queue.yml");
        userCache = loadFile("usercache.yml");

        getLogger().info("Starting Discord bot...");
        Thread bot = new Thread(() -> {
            JDABuilder builder = JDABuilder.createDefault(new Secrets().getBotToken());
            builder.disableIntents(GatewayIntent.GUILD_MESSAGE_TYPING);
            builder.disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE);
            builder.setMemberCachePolicy(MemberCachePolicy.NONE);
            builder.setBulkDeleteSplittingEnabled(false);
            builder.setStatus(OnlineStatus.ONLINE);
            builder.setActivity(Activity.watching("for cheaters"));
            builder.setEnableShutdownHook(false);
            try {
                jda = builder.build();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Wait until the api works
            while (jda.getGuildById(455919765999976461L) == null) try {
                //noinspection BusyWait
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            jda.addEventListener(new DiscordEvents());
        });
        bot.start();

        PluginManager pluginManager = getProxy().getPluginManager();

        Runnable task = () -> {
            getLogger().info("Registering commands...");
            pluginManager.registerCommand(plugin, new ReasonsCommand());
            pluginManager.registerCommand(plugin, new BaseCommand());
            pluginManager.registerCommand(plugin, new BlankCommand());
            pluginManager.registerCommand(plugin, new RelayCommand());
            pluginManager.registerCommand(plugin, new WarnCommand());
            pluginManager.registerCommand(plugin, new WarningsCommand());
            pluginManager.registerCommand(plugin, new WarnListCommand());
            pluginManager.registerCommand(plugin, new WarnQueueCommand());
        };
        getProxy().getScheduler().schedule(this, task, 7, TimeUnit.SECONDS);

        pluginManager.registerListener(this, new JoinEvent());
    }

    @Override
    public void onDisable() {
        plugin.getLogger().info("Shutting down Discord bot...");
        jda.shutdown();
    }

    public ProxiedPlayer getOnlinePlayer(UUID uuid) {
        return getProxy().getPlayer(uuid);
    }

    public String getPlayerName(UUID uuid) {
        String username = userCache.getString("uuids." + uuid);
        if (username.isBlank()) username = "N/A";
        return username;
    }

    public UUID getPlayerUUID(String username) {
        UUID uuid = null;
        try {
            uuid = UUID.fromString(userCache.getString("usernames." + username.toLowerCase()));
        } catch (IllegalArgumentException ignored) {
        }
        return uuid;
    }

    public void nonexistentPlayerMessage(String username, CommandSender sender) {
        BaseComponent[] message = new ComponentBuilder("Couldn't find player ").color(ChatColor.RED)
                .append(username)
                .color(ChatColor.GRAY)
                .append(" in the user cache!")
                .color(ChatColor.RED)
                .create();
        sender.sendMessage(message);
    }

    public Configuration loadFile(String fileName) {
        if (!getDataFolder().exists())
            //noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), fileName);

        if (!file.exists()) try (InputStream in = getResourceAsStream(fileName)) {
            Files.copy(in, file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), fileName));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveData() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .save(data, new File(getDataFolder(), "data.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveQueue() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .save(queue, new File(getDataFolder(), "queue.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveUserCache() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .save(userCache, new File(getDataFolder(), "usercache.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SilverstoneWarnings getPlugin() {
        return plugin;
    }
}
