package net.silverstonemc.silverstoneglobal.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ListOPs implements CommandExecutor {
    public ListOPs(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        for (OfflinePlayer player : plugin.getServer().getOperators())
            sender.sendMessage(player.getName() != null ? player.getName() : "null");
        return true;
    }
}
