package net.silverstonemc.silverstoneglobal.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public record TPSMonitor(JavaPlugin plugin) implements CommandExecutor {
    private static BukkitRunnable task;

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length > 0) {
            double interval;
            try {
                interval = Double.parseDouble(args[0]) * 20;
            } catch (NumberFormatException ignored) {
                sender.sendMessage(ChatColor.RED + "That number ain't right");
                return true;
            }

            if (task != null && !task.isCancelled()) {
                task.cancel();
                Bukkit.dispatchCommand(sender, cmd.getName() + " " + args[0]);
                sender.sendMessage(
                    ChatColor.GREEN + "Successfully changed the interval to " + interval / 20 + " seconds.");
                return true;
            }

            task = new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(sender, "tps");
                }
            };
            task.runTaskTimer(plugin, 0, (long) interval);

        } else {
            if (task == null || task.isCancelled()) {
                sender.sendMessage(ChatColor.RED + "The monitor was already inactive.");
                return true;
            }
            task.cancel();
            sender.sendMessage(ChatColor.GREEN + "TPS monitoring deactivated.");
        }
        return true;
    }
}
