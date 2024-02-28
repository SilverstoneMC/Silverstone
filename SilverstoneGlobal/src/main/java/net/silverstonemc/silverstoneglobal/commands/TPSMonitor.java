package net.silverstonemc.silverstoneglobal.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public record TPSMonitor(JavaPlugin plugin) implements CommandExecutor {
    private static BukkitRunnable task;

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length > 0) {
            double interval;
            try {
                interval = Double.parseDouble(args[0]) * 20;
            } catch (NumberFormatException ignored) {
                sender.sendMessage(Component.text("That number ain't right", NamedTextColor.RED));
                return true;
            }

            if (task != null && !task.isCancelled()) {
                task.cancel();
                Bukkit.dispatchCommand(sender, cmd.getName() + " " + args[0]);
                sender.sendMessage(Component.text(
                    "Successfully changed the interval to " + interval / 20 + " seconds.",
                    NamedTextColor.GREEN));
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
                sender.sendMessage(Component.text("The monitor was already inactive.", NamedTextColor.RED));
                return true;
            }
            task.cancel();
            sender.sendMessage(Component.text("TPS monitoring deactivated.", NamedTextColor.GREEN));
        }
        return true;
    }
}
