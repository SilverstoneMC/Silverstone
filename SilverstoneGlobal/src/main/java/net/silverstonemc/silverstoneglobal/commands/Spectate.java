package net.silverstonemc.silverstoneglobal.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class Spectate implements CommandExecutor {
    public Spectate(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(
                Component.text("Sorry, but only players can do that.").color(NamedTextColor.RED));
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("spectate")) if (args.length > 0) {
            Player player = (Player) sender;
            Player target = Bukkit.getPlayer(args[0]);

            // If player is null
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Please provide an online player!");
                return true;
            }

            player.setGameMode(GameMode.SPECTATOR);
            player.setSpectatorTarget(null);
            player.setInvisible(true);

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.teleportAsync(target.getLocation());
                }
            }.runTaskLater(plugin, 5);

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setSpectatorTarget(target);

                    if (player.getGameMode() != GameMode.SPECTATOR) {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.setSpectatorTarget(target);
                    }
                    player.setInvisible(false);
                }
            }.runTaskLater(plugin, 10);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("watch")) if (args.length > 0) {
            Player player = (Player) sender;
            Player target = Bukkit.getPlayer(args[0]);

            // If player is null
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Please provide an online player!");
                return true;
            }

            player.setGameMode(GameMode.SPECTATOR);
            player.setSpectatorTarget(null);
            player.setInvisible(true);

            new BukkitRunnable() {
                @Override
                public void run() {
                    player.teleportAsync(target.getLocation());

                    if (player.getGameMode() != GameMode.SPECTATOR) player.setGameMode(GameMode.SPECTATOR);
                    player.setInvisible(false);
                }
            }.runTaskLater(plugin, 5);
            return true;
        }
        return false;
    }
}
