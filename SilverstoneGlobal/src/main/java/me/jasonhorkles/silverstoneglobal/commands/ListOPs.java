package me.jasonhorkles.silverstoneglobal.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("ConstantConditions")
public record ListOPs(JavaPlugin plugin) implements CommandExecutor {

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        for (OfflinePlayer player : plugin.getServer().getOperators()) sender.sendMessage(player.getName());
        return true;
    }
}
