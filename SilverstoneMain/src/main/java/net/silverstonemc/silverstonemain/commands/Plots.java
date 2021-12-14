package net.silverstonemc.silverstonemain.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Plots implements CommandExecutor {

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Sorry, but only players can do that.");
            return true;
        }

        if (player.getWorld().getName().equalsIgnoreCase("plot")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', """
                    &3---------- &aPlots &3----------
                    &r
                    &2Default players get &b4 &2plots
                    &2Builders get &b6 &2plots
                    &2Advanced Builders get &b9 &2plots
                    &2Expert Builders get &b16 &2plots
                    &r"""));

            String n = "N/A";
            if (player.hasPermission("plots.plot.16")) n = "16";
            else if (player.hasPermission("plots.plot.9")) n = "9";
            else if (player.hasPermission("plots.plot.6")) n = "6";
            else if (player.hasPermission("plots.plot.4")) n = "4";
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3You can have &a" + n + " &3plots"));
        } else if (player.getWorld().getName().equalsIgnoreCase("shops")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', """
                    &3---------- &aPlots &3----------
                    &r
                    &2Default players get &b16 &2plots
                    &2Builders get &b20 &2plots
                    &2Advanced Builders get &b24 &2plots
                    &2Expert Builders get &b25 &2plots
                    &r"""));

            String n = "N/A";
            if (player.hasPermission("plots.plot.25")) n = "25";
            else if (player.hasPermission("plots.plot.24")) n = "24";
            else if (player.hasPermission("plots.plot.20")) n = "20";
            else if (player.hasPermission("plots.plot.16")) n = "16";
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3You can have &a" + n + " &3plots"));
        } else player.sendMessage(ChatColor.RED + "You must be in a plot world to do that!");
        return true;
    }
}
