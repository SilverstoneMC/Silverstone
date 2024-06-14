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
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.silverstonemc.silverstoneproxy.commands.*;
import net.silverstonemc.silverstoneproxy.commands.chatemotes.FacePalm;
import net.silverstonemc.silverstoneproxy.commands.chatemotes.Shrug;
import net.silverstonemc.silverstoneproxy.commands.chatemotes.TableFlip;
import net.silverstonemc.silverstoneproxy.events.*;
import ninja.leaping.configurate.ConfigurationNode;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static net.silverstonemc.silverstoneproxy.ConfigurationManager.FileType.USERCACHE;

@Plugin(id = "silverstoneproxy", name = "SilverstoneProxy", version = "%VERSION%", description = "Features for the Silverstone proxy, including warnings", authors = {"JasonHorkles"}, dependencies = {@Dependency(id = "luckperms"), @Dependency(id = "carbonchat"), @Dependency(id = "libertybans")})

public class SilverstoneProxy {
    @Inject
    public SilverstoneProxy(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.fileManager = new ConfigurationManager(dataDirectory);

        fileManager.loadFiles();
    }

    public static JDA jda;
    public final Logger logger;
    public final ProxyServer server;
    public final ConfigurationManager fileManager;
    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from(
        "silverstone:pluginmsg");

    private ConsoleErrors errors;
    private LuckPerms luckPerms;

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        luckPerms = LuckPermsProvider.get();

        // Cache users
        for (ConfigurationNode users : fileManager.files.get(USERCACHE).getNode("users").getChildrenMap()
            .values()) {
            //noinspection DataFlowIssue
            UUID uuid = UUID.fromString(users.getKey().toString());
            String username = fileManager.files.get(USERCACHE).getNode("users", users.getKey()).getString();
            UserManager.playerMap.put(uuid, username);
        }

        new Thread(() -> {
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
        }, "Discord Bot").start();

        CommandManager commandManager = server.getCommandManager();
        commandManager.register("bcnetworkrestart", new Restart(this));
        commandManager.register("chatsounds", new ChatSounds(this));
        commandManager.register("discord", new Discord());
        commandManager.register("facepalm", new FacePalm(), "ssw");
        commandManager.register("forums",
            new Forums(),
            "site",
            "bugreport",
            "reportbug",
            "report",
            "reportplayer",
            "playerreport",
            "builder");
        commandManager.register("joinleavesounds", new JoinLeaveSounds(this));
        commandManager.register("mmotd", new MiniMOTDControls(this));
        commandManager.register("mods", new Mods());
        commandManager.register("nickname", new Nickname(this), "nick");
        commandManager.register("prefixes", new Prefixes(this));
        commandManager.register("prestartwhenempty", new RestartWhenEmpty(this));
        commandManager.register("realname", new Realname(this));
        commandManager.register("relay", new Relay());
        commandManager.register("rules", new Rules(this));
        commandManager.register("shrug", new Shrug());
        commandManager.register("socialspy", new SocialSpy(this));
        commandManager.register("ssp", new BaseCommand(this), "ssw");
        commandManager.register("tableflip", new TableFlip());
        commandManager.register("warn", new Warn(this));
        commandManager.register("warnings", new Warnings(this));
        commandManager.register("warnlist", new WarnList(this));
        commandManager.register("warnqueue", new WarnQueue(this));
        commandManager.register("warnreasons", new WarnReasons(this), "categories", "reasons");

        EventManager eventManager = server.getEventManager();
        eventManager.register(this, new Chat(this));
        eventManager.register(this, new Glist(this));
        eventManager.register(this, new Join(this));
        eventManager.register(this, new Leave(this));
        eventManager.register(this, new PluginMessages(this));

        CarbonChatProvider.carbonChat().eventHandler().subscribe(CarbonChatEvent.class,
            0,
            false,
            chatEvent -> new Chat(this).onChat(chatEvent));
        CarbonChatProvider.carbonChat().eventHandler().subscribe(CarbonPrivateChatEvent.class,
            0,
            false,
            chatEvent -> new Chat(this).onPrivateChat(chatEvent));

        server.getChannelRegistrar().register(IDENTIFIER);

        new AutoBroadcast(this).scheduleBroadcasts();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        errors.remove();

        logger.info("Shutting down Discord bot...");
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

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }
}
