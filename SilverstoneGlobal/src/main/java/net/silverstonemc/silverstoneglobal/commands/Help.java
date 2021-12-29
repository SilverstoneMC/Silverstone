package net.silverstonemc.silverstoneglobal.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
            case "creative" -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', """
                    &r
                    &a&lCommands:
                    &b/p claim &aon an unclaimed plot to get started
                    &b/p home [#] &ato teleport to your plot(s)
                    &b/p set &ato see multiple plot options
                    &b/p merge all &ato merge all your plots together
                    &b/p clear &ato clear your plot
                    &b/p delete &ato delete your plot
                    &b/wesui toggle &ato toggle the selection particles
                    &c&lLow-effort plots are deleted after 60 days of inactivity.
                    """));

            case "main" -> player.performCommand("gettingstarted");

            case "minigames" -> player.performCommand("htp");

            default -> player.performCommand("bukkit:help");
        }
        return true;
    }
}
