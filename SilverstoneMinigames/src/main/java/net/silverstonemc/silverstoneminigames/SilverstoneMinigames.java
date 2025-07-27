package net.silverstonemc.silverstoneminigames;

import dev.triumphteam.gui.paper.Gui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.silverstonemc.silverstoneminigames.commands.*;
import net.silverstonemc.silverstoneminigames.commands.minigames.HideSeekCmd;
import net.silverstonemc.silverstoneminigames.events.Join;
import net.silverstonemc.silverstoneminigames.events.LockInv;
import net.silverstonemc.silverstoneminigames.events.Void;
import net.silverstonemc.silverstoneminigames.events.WorldChange;
import net.silverstonemc.silverstoneminigames.minigames.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({
    "DataFlowIssue",
    "unused"
})
public class SilverstoneMinigames extends JavaPlugin implements CommandExecutor {
    public static DataManager data;
    public static final String MINIGAME_WORLD = "minigames";

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
        getCommand("htp").setExecutor(new Minigames(this));
        getCommand("minigame").setExecutor(new Minigames(this));
        getCommand("minigamemanager").setExecutor(new MinigameManager(this));
        getCommand("tntrun").setExecutor(new TNTRun(this));

        getCommand("corruptedtag").setTabCompleter(new TabComplete());
        getCommand("hideseek").setTabCompleter(new TabComplete());
        getCommand("minigamemanager").setTabCompleter(new TabComplete());

        Gui.of(1);

        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new Back(), this);
        pluginManager.registerEvents(new CorruptedTag(this), this);
        pluginManager.registerEvents(new Disguise(), this);
        pluginManager.registerEvents(new DoubleJump(), this);
        pluginManager.registerEvents(new HideSeek(this), this);
        pluginManager.registerEvents(new Join(), this);
        pluginManager.registerEvents(new LockInv(), this);
        pluginManager.registerEvents(new Minigames(this), this);
        pluginManager.registerEvents(new PvP(), this);
        pluginManager.registerEvents(new Void(), this);
        pluginManager.registerEvents(new WorldChange(), this);

        new FlyingCourse(this).updateFCScoreboard();
        new Minigames(this).createInventories();
    }

    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        data.saveDefaultConfig();
        data.reloadConfig();
        new FlyingCourse(this).updateFCScoreboard();
        sender.sendMessage(Component.text("SilverstoneMinigames reloaded!", NamedTextColor.GREEN));
        return true;
    }
}