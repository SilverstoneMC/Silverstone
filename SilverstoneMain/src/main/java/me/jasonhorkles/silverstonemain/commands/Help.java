package me.jasonhorkles.silverstonemain.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Help implements CommandExecutor {

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "?");
            return true;
        }

        switch (player.getWorld().getName()) {
            case "plot" -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', """
                    &r
                    &a&lCommands:
                    &b/p claim &aon an unclaimed plot to get started
                    &b/p home [##] &ato teleport to your plot(s)
                    &b/p set &ato see multiple plot options
                    &b/p merge all &ato merge all your plots together
                    &b/p clear &ato clear your plot
                    &b/p delete &ato delete your plot
                    &b/wesui toggle &ato toggle the selection particles
                    &c&lLow-effort plots are deleted after 60 days of inactivity.
                    """));
            case "shops" -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', """
                    &r
                    &a&lCommands:
                    &b/p claim &aon a red plot to get started &7($100 per plot - non-refundable)
                    &b/p home [##] &ato teleport to your plot(s)
                    &b/p set <wall | border> <block> &ato customize your plot
                    &b/p clear &ato clear your plot
                    &b/p delete &ato delete your plot
                    &c&lRemember to grab your resources back before clearing / deleting your plot!
                    &c&lPlots are deleted after 60 days of inactivity with no refunds.
                    """));

            case "survival_nether", "survival_the_end", "utility" -> player.performCommand("tips");
            case "mini_empty", "mini_terrain", "mini_amplified" -> player.performCommand("htp");
            default -> player.performCommand("gettingstarted");
        }
        return true;
    }
}
