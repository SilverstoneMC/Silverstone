package net.silverstonemc.silverstoneminigames.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class MinigameManager implements CommandExecutor {
    public MinigameManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    private final JavaPlugin plugin;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return false;
    }
}
