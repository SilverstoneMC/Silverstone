package net.silverstonemc.silverstoneminigames;

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
import org.jetbrains.annotations.NotNull;

public class SilverstoneMinigames extends JavaPlugin implements CommandExecutor {
    public static DataManager data;
    public static final String MINIGAME_WORLD = "minigames";

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onEnable() {
        data = new DataManager(this);

        data.saveDefaultConfig();

        getCommand("doublejump").setExecutor(new DoubleJump());
        getCommand("clearbossbars").setExecutor(new BossBarManager());
        getCommand("corruptedtag").setExecutor(new CorruptedTag(this));
        getCommand("fcfinish").setExecutor(new FlyingCourse(this));
        getCommand("flyingcourse").setExecutor(new FlyingCourse(this));
        getCommand("hideseek").setExecutor(new HideSeekCmd(this));
        getCommand("htp").setExecutor(new HowToPlay());
        getCommand("minigame").setExecutor(new RandomGame(this));
        getCommand("minigamemanager").setExecutor(new MinigameManager(this));
        getCommand("tntrun").setExecutor(new TNTRun(this));

        getCommand("corruptedtag").setTabCompleter(new TabComplete());
        getCommand("hideseek").setTabCompleter(new TabComplete());
        getCommand("minigamemanager").setTabCompleter(new TabComplete());

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

    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        data.saveDefaultConfig();
        data.reloadConfig();
        new FlyingCourse(this).updateFCScoreboard();
        sender.sendMessage(Component.text("SilverstoneMinigames reloaded!", NamedTextColor.GREEN));
        return true;
    }
}