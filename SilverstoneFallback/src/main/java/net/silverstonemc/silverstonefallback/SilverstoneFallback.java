package net.silverstonemc.silverstonefallback;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"ConstantConditions", "unused"})
public class SilverstoneFallback extends JavaPlugin implements CommandExecutor {

    // Startup
    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        getCommand("exit").setExecutor(new Exit(this));

        PluginManager pluginManager = this.getServer().getPluginManager();

        pluginManager.registerEvents(new JoinLeave(this), this);
        pluginManager.registerEvents(new Chat(), this);
        pluginManager.registerEvents(new TPEvent(), this);
    }

    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        saveDefaultConfig();
        reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "SilverstoneFallback reloaded!");
        return true;
    }
}