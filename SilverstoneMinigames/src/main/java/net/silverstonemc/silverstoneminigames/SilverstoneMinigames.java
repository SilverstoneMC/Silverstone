package net.silverstonemc.silverstoneminigames;

import net.silverstonemc.silverstoneminigames.commands.Back;
import net.silverstonemc.silverstoneminigames.commands.Minigames;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"ConstantConditions", "unused"})
public class SilverstoneMinigames extends JavaPlugin implements CommandExecutor {

    public static DataManager data;

    // Startup
    @Override
    public void onEnable() {
        saveDefaultConfig();

        getCommand("doublejump").setExecutor(new DoubleJump(this));
        getCommand("flyingcourse").setExecutor(new FlyingCourse(this));
        getCommand("hsheartbeat").setExecutor(new HideSeek(this));
        getCommand("hsrandomtaunt").setExecutor(new HideSeek(this));
        getCommand("hsresettauntpoints").setExecutor(new HideSeek(this));
        getCommand("kitpvp").setExecutor(new KitPvPGUI(this));
        getCommand("taunt").setExecutor(new HideSeek(this));
        getCommand("tntrun").setExecutor(new TNTRun(this));
        getCommand("zfcfinish").setExecutor(new FlyingCourse(this));
        getCommand("htp").setExecutor(new Minigames(this));
        getCommand("minigame").setExecutor(new Minigames(this));

        PluginManager pluginManager = this.getServer().getPluginManager();

        pluginManager.registerEvents(new Back(), this);
        pluginManager.registerEvents(new Minigames(this), this);
        pluginManager.registerEvents(new DoubleJump(this), this);
        pluginManager.registerEvents(new HideSeek(this), this);
        pluginManager.registerEvents(new KitPvPGUI(this), this);
        pluginManager.registerEvents(new LockInv(), this);
        pluginManager.registerEvents(new PvP(), this);
        pluginManager.registerEvents(new Void(), this);

        Minigames.createGameInv();
        Minigames.createHtpInv();
        HideSeek.createInv();
        KitPvPGUI.createInv();

        new FlyingCourse(this).updateFCScoreboard();
    }

    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        saveDefaultConfig();
        reloadConfig();
        new FlyingCourse(this).updateFCScoreboard();
        sender.sendMessage(ChatColor.GREEN + "SilverstoneMinigames reloaded!");
        return true;
    }
}