package net.silverstonemc.silverstoneminigames;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.silverstonemc.silverstoneminigames.commands.Back;
import net.silverstonemc.silverstoneminigames.commands.Minigames;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"DataFlowIssue", "unused"})
public class SilverstoneMinigames extends JavaPlugin implements CommandExecutor {
    public static DataManager data;

    // Startup
    @Override
    public void onEnable() {
        data = new DataManager(this);

        saveDefaultConfig();
        data.saveDefaultConfig();

        getCommand("doublejump").setExecutor(new DoubleJump(this));
        getCommand("flyingcourse").setExecutor(new FlyingCourse(this));
        getCommand("hsheartbeat").setExecutor(new HideSeek(this));
        getCommand("hsrandomtaunt").setExecutor(new HideSeek(this));
        getCommand("hsresettauntpoints").setExecutor(new HideSeek(this));
        getCommand("htp").setExecutor(new Minigames(this));
        getCommand("minigame").setExecutor(new Minigames(this));
        getCommand("taunt").setExecutor(new HideSeek(this));
        getCommand("tntrun").setExecutor(new TNTRun(this));
        getCommand("fcfinish").setExecutor(new FlyingCourse(this));

        PluginManager pluginManager = this.getServer().getPluginManager();

        pluginManager.registerEvents(new Back(), this);
        pluginManager.registerEvents(new DoubleJump(this), this);
        pluginManager.registerEvents(new HideSeek(this), this);
        pluginManager.registerEvents(new LockInv(), this);
        pluginManager.registerEvents(new Minigames(this), this);
        pluginManager.registerEvents(new PvP(), this);
        pluginManager.registerEvents(new Void(), this);
        
        new FlyingCourse(this).updateFCScoreboard();
        new HideSeek(this).createInv();
        new Minigames(this).createGameInv();
        new Minigames(this).createHtpInv();
    }

    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        saveDefaultConfig();
        reloadConfig();
        data.saveDefaultConfig();
        data.reloadConfig();
        new FlyingCourse(this).updateFCScoreboard();
        sender.sendMessage(Component.text("SilverstoneMinigames reloaded!").color(NamedTextColor.GREEN));
        return true;
    }
}