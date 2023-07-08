package net.silverstonemc.silverstoneglobal.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public record Help(JavaPlugin plugin) implements CommandExecutor {
    @SuppressWarnings("DataFlowIssue")
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        // #serverSpecific
        switch (plugin.getConfig().getString("server").toLowerCase()) {
            case "minigames" -> plugin.getServer().dispatchCommand(sender, "htp");
            case "creative" -> {
                //todo creative help
            }
            case "survival" -> plugin.getServer().dispatchCommand(sender, "tips");
        }
        return true;
    }
}