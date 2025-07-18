package net.silverstonemc.silverstoneminigames;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.silverstonemc.silverstoneminigames.commands.*;
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

    @Override
    public void onEnable() {
        data = new DataManager(this);

        saveDefaultConfig();
        data.saveDefaultConfig();

        getCommand("doublejump").setExecutor(new DoubleJump(this));
        getCommand("clearbossbars").setExecutor(new BossBarManager());
        getCommand("corruptedtag").setExecutor(new CorruptedTag(this));
        getCommand("fcfinish").setExecutor(new FlyingCourse(this));
        getCommand("flyingcourse").setExecutor(new FlyingCourse(this));
        getCommand("hsassignblock").setExecutor(new HideSeek(this));
        getCommand("hsheartbeat").setExecutor(new HideSeek(this));
        getCommand("hsrandomtaunt").setExecutor(new HideSeek(this));
        getCommand("hsresettauntpoints").setExecutor(new HideSeek(this));
        getCommand("htp").setExecutor(new Minigames(this));
        getCommand("minigame").setExecutor(new Minigames(this));
        getCommand("miniholo").setExecutor(new Holograms());
        getCommand("tntrun").setExecutor(new TNTRun(this));

        getCommand("corruptedtag").setTabCompleter(new TabComplete());
        getCommand("miniholo").setTabCompleter(new TabComplete());

        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new Back(), this);
        pluginManager.registerEvents(new CorruptedTag(this), this);
        pluginManager.registerEvents(new Disguise(), this);
        pluginManager.registerEvents(new DoubleJump(this), this);
        pluginManager.registerEvents(new HideSeek(this), this);
        pluginManager.registerEvents(new LockInv(), this);
        pluginManager.registerEvents(new Minigames(this), this);
        pluginManager.registerEvents(new PvP(), this);
        pluginManager.registerEvents(new Void(), this);
        pluginManager.registerEvents(new WorldChange(), this);

        new CorruptedTag(this).createInventories();
        new FlyingCourse(this).updateFCScoreboard();
        new HideSeek(this).createInventory();
        new Minigames(this).createInventories();
    }

    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        saveDefaultConfig();
        reloadConfig();
        data.saveDefaultConfig();
        data.reloadConfig();
        new FlyingCourse(this).updateFCScoreboard();
        sender.sendMessage(Component.text("SilverstoneMinigames reloaded!", NamedTextColor.GREEN));
        return true;
    }
}