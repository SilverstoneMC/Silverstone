package net.silverstonemc.silverstoneminigames;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.silverstonemc.silverstoneminigames.commands.*;
import net.silverstonemc.silverstoneminigames.commands.minigames.HideSeekCmd;
import net.silverstonemc.silverstoneminigames.events.Join;
import net.silverstonemc.silverstoneminigames.events.LockInv;
import net.silverstonemc.silverstoneminigames.events.Void;
import net.silverstonemc.silverstoneminigames.events.WorldChange;
import net.silverstonemc.silverstoneminigames.managers.BossBarManager;
import net.silverstonemc.silverstoneminigames.managers.DataManager;
import net.silverstonemc.silverstoneminigames.managers.MinigameManager;
import net.silverstonemc.silverstoneminigames.minigames.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class SilverstoneMinigames extends JavaPlugin implements CommandExecutor {
    public static DataManager data;
    public static JDA jda;
    public static final String MINIGAME_WORLD = "minigames";

    private static SilverstoneMinigames instance;

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onEnable() {
        instance = this;
        data = new DataManager(this);

        data.saveDefaultConfig();

        new BukkitRunnable() {
            @Override
            public void run() {
                getLogger().info("Starting Discord bot...");
                JDABuilder builder = JDABuilder.createLight(new Secrets().botToken());
                builder.setEnableShutdownHook(false);
                jda = builder.build();

                try {
                    jda.awaitReady();
                    new MinigameManager().updateDiscordMessage();

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskAsynchronously(this);

        getCommand("doublejump").setExecutor(new DoubleJump());
        getCommand("clearbossbars").setExecutor(new BossBarManager());
        getCommand("corruptedtag").setExecutor(new CorruptedTag(this));
        getCommand("fcfinish").setExecutor(new FlyingCourse(this));
        getCommand("flyingcourse").setExecutor(new FlyingCourse(this));
        getCommand("hideseek").setExecutor(new HideSeekCmd(this));
        getCommand("htp").setExecutor(new HowToPlay());
        getCommand("minigame").setExecutor(new RandomGame());
        getCommand("minigamemanager").setExecutor(new MinigameManager());
        getCommand("tntrun").setExecutor(new TNTRun(this));

        getCommand("corruptedtag").setTabCompleter(new TabComplete());
        getCommand("hideseek").setTabCompleter(new TabComplete());
        getCommand("minigamemanager").setTabCompleter(new MinigameManager());

        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new Back(), this);
        pluginManager.registerEvents(new Disguise(), this);
        pluginManager.registerEvents(new DoubleJump(), this);
        pluginManager.registerEvents(new HideSeek(this), this);
        pluginManager.registerEvents(new Join(), this);
        pluginManager.registerEvents(new LockInv(), this);
        pluginManager.registerEvents(new PvP(), this);
        pluginManager.registerEvents(new Void(), this);
        pluginManager.registerEvents(new WorldChange(), this);

        new FlyingCourse(this).updateFCScoreboard();
    }

    @Override
    public void onDisable() {
        getLogger().info("Shutting down Discord bot...");
        try {
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
        }
    }

    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        data.saveDefaultConfig();
        data.reloadConfig();
        new MinigameManager().updateDiscordMessage();

        new FlyingCourse(this).updateFCScoreboard();
        sender.sendMessage(Component.text("SilverstoneMinigames reloaded!", NamedTextColor.GREEN));
        return true;
    }

    public static SilverstoneMinigames getInstance() {
        return instance;
    }
}