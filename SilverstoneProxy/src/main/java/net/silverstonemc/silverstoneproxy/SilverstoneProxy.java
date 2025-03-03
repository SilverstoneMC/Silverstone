package net.silverstonemc.silverstoneproxy;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.draycia.carbon.api.CarbonChatProvider;
import net.draycia.carbon.api.event.events.CarbonChatEvent;
import net.draycia.carbon.api.event.events.CarbonPrivateChatEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.silverstonemc.silverstoneproxy.commands.*;
import net.silverstonemc.silverstoneproxy.events.*;
import org.slf4j.Logger;
import org.spongepowered.configurate.ConfigurationNode;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static net.silverstonemc.silverstoneproxy.ConfigurationManager.FileType.USERCACHE;
import static net.silverstonemc.silverstoneproxy.ConfigurationManager.FileType.WHITELIST;

@Plugin(id = "silverstoneproxy", name = "SilverstoneProxy", version = "%VERSION%", description = "Features for the Silverstone proxy, including warnings", authors = {"JasonHorkles"}, dependencies = {@Dependency(id = "luckperms"), @Dependency(id = "carbonchat"), @Dependency(id = "libertybans")})

public class SilverstoneProxy {
    public static JDA jda;
    public final Logger logger;
    public final ProxyServer server;
    public final ConfigurationManager fileManager;
    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from(
        "silverstone:pluginmsg");

    private ConsoleErrors errors;
    private LuckPerms luckPerms;
    private Thread jdaThread;
    private static SilverstoneProxy instance;

    @Inject
    public SilverstoneProxy(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        fileManager = new ConfigurationManager(dataDirectory);

        fileManager.loadFiles();
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        instance = this;
        luckPerms = LuckPermsProvider.get();

        // Cache users
        for (ConfigurationNode users : fileManager.files.get(USERCACHE).node("users").childrenMap()
            .values()) {
            //noinspection DataFlowIssue
            UUID uuid = UUID.fromString(users.key().toString());
            String username = fileManager.files.get(USERCACHE).node("users", users.key()).getString();
            UserManager.playerMap.put(uuid, username);
        }

        startJDA();
        registerCommands();

        EventManager eventManager = server.getEventManager();
        eventManager.register(this, new Chat(this));
        eventManager.register(this, new Glist(this));
        eventManager.register(this, new Join(this));
        eventManager.register(this, new Leave(this));
        eventManager.register(this, new PluginMessages(this));

        CarbonChatProvider.carbonChat().eventHandler().subscribe(
            CarbonChatEvent.class,
            0,
            false,
            chatEvent -> new Chat(this).onChat(chatEvent));
        CarbonChatProvider.carbonChat().eventHandler().subscribe(
            CarbonPrivateChatEvent.class,
            0,
            false,
            chatEvent -> new Chat(this).onPrivateChat(chatEvent));

        server.getChannelRegistrar().register(IDENTIFIER);

        new AutoBroadcast(this).scheduleBroadcasts();

        // Check whitelist
        server.getScheduler().buildTask(
            this, () -> {
                if (fileManager.files.get(WHITELIST).node("enabled").getBoolean()) {
                    logger.warn("Whitelist is on");

                    Player jason = server.getPlayer(UUID.fromString("a28173af-f0a9-47fe-8549-19c6bccf68da"))
                        .orElse(null);
                    if (jason == null) return;

                    jason.sendActionBar(Component.text(
                        "Proxy whitelist is on",
                        NamedTextColor.GOLD,
                        TextDecoration.BOLD));
                }
            }).delay(5, TimeUnit.SECONDS).repeat(5, TimeUnit.MINUTES).schedule();

        // Uptime heartbeat
        try {
            Heartbeat heartbeat = new Heartbeat(new Secrets().heartbeatUrl());
            server.getScheduler().buildTask(this, heartbeat::sendHeartbeat).repeat(1, TimeUnit.MINUTES).delay(10,
                TimeUnit.SECONDS).schedule();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerCommands() {
        CommandManager commandManager = server.getCommandManager();
        commandManager.register(commandManager.metaBuilder("bcnetworkrestart").build(), new Restart(this));
        commandManager.register(commandManager.metaBuilder("chatsounds").build(), new ChatSounds(this));
        commandManager.register(commandManager.metaBuilder("discord").build(), new Discord());
        commandManager.register(
            commandManager.metaBuilder("forums").aliases(
                "site",
                "bugreport",
                "reportbug",
                "report",
                "reportplayer",
                "playerreport",
                "builder").build(), new Forums());
        commandManager.register(commandManager.metaBuilder("jdamanager").build(), new JDAManager(this));
        commandManager.register(
            commandManager.metaBuilder("joinleavesounds").build(),
            new JoinLeaveSounds(this));
        commandManager.register(commandManager.metaBuilder("mmotd").build(), new MiniMOTDControls(this));
        commandManager.register(
            commandManager.metaBuilder("nickname").aliases("nick").build(),
            new Nickname(this));
        commandManager.register(commandManager.metaBuilder("prefixes").build(), new Prefixes(this));
        commandManager.register(
            commandManager.metaBuilder("prestartwhenempty").build(),
            new RestartWhenEmpty(this));
        commandManager.register(commandManager.metaBuilder("pwhitelisttoggle").build(), new Whitelist(this));
        commandManager.register(commandManager.metaBuilder("realname").build(), new Realname(this));
        commandManager.register(commandManager.metaBuilder("relay").build(), new Relay());
        commandManager.register(commandManager.metaBuilder("rules").build(), new Rules(this));
        commandManager.register(
            commandManager.metaBuilder("ssp").aliases("ssw").build(),
            new BaseCommand(this));
        commandManager.register(commandManager.metaBuilder("warn").build(), new Warn(this));
        commandManager.register(commandManager.metaBuilder("warnings").build(), new Warnings(this));
        commandManager.register(commandManager.metaBuilder("warnlist").build(), new WarnList(this));
        commandManager.register(commandManager.metaBuilder("warnqueue").build(), new WarnQueue(this));
        commandManager.register(
            commandManager.metaBuilder("warnreasons").aliases("categories", "reasons")
                .build(),
            new WarnReasons(this));
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        shutdownJDA();
        errors.remove();
    }

    public void startJDA() {
        jdaThread = new Thread(
            () -> {
                logger.info("Starting Discord bot...");
                JDABuilder builder = JDABuilder.createDefault(new Secrets().botToken());
                builder.disableIntents(GatewayIntent.GUILD_MESSAGE_TYPING);
                builder.disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE);
                builder.setMemberCachePolicy(MemberCachePolicy.NONE);
                builder.setStatus(OnlineStatus.ONLINE);
                builder.setActivity(Activity.watching("over Silverstone"));
                builder.setEnableShutdownHook(false);
                builder.addEventListeners(new DiscordButtons(this));
                jda = builder.build();

                errors = new ConsoleErrors(this);
                errors.start();
            }, "Discord Bot");
        jdaThread.start();
    }

    public void shutdownJDA() {
        try {
            logger.info("Shutting down Discord bot...");

            // Initating the shutdown, this closes the gateway connection and subsequently closes the requester queue
            jda.shutdown();
            // Allow at most 10 seconds for remaining requests to finish
            if (!jda.awaitShutdown(
                10,
                TimeUnit.SECONDS)) { // returns true if shutdown is graceful, false if timeout exceeded
                jda.shutdownNow(); // Cancel all remaining requests, and stop thread-pools
                jda.awaitShutdown(); // Wait until shutdown is complete (indefinitely)
            }
        } catch (InterruptedException ignored) {
        } finally {
            jdaThread = null;
        }
    }

    public static SilverstoneProxy getInstance() {
        return instance;
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public Thread getJdaThread() {
        return jdaThread;
    }
}
