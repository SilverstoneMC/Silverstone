package net.silverstonemc.silverstoneglobal.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public record Help(JavaPlugin plugin) implements CommandExecutor {

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "?");
            return true;
        }

        // #serverSpecific
        //noinspection ConstantConditions
        switch (plugin.getConfig().getString("server")) {
            case "main" -> player.performCommand("gettingstarted");

            case "minigames" -> player.performCommand("htp");

            default -> player.performCommand("bukkit:help");
        }
        return true;
    }
}
