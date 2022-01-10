package net.silverstonemc.silverstoneglobal.commands;

import net.ess3.api.IEssentials;
import net.silverstonemc.silverstoneglobal.SilverstoneGlobal;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class Spectate implements CommandExecutor {

    private final JavaPlugin plugin;

    public Spectate(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    final IEssentials essentials = SilverstoneGlobal.getInstance().getEssentials();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Sorry, but only players can do that.");
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

            BukkitRunnable delayTP = new BukkitRunnable() {
                @Override
                public void run() {
                    essentials.getUser(player)
                            .getAsyncTeleport()
                            .now(target.getLocation(), false, PlayerTeleportEvent.TeleportCause.COMMAND, new CompletableFuture<>());
                }
            };
            delayTP.runTaskLater(plugin, 5);

            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    player.setSpectatorTarget(target);

                    if (player.getGameMode() != GameMode.SPECTATOR) {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.setSpectatorTarget(target);
                    }
                    player.setInvisible(false);
                }
            };
            task.runTaskLater(plugin, 10);
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

            BukkitRunnable delayTP = new BukkitRunnable() {
                @Override
                public void run() {
                    essentials.getUser(player)
                            .getAsyncTeleport()
                            .now(target.getLocation(), false, PlayerTeleportEvent.TeleportCause.COMMAND, new CompletableFuture<>());

                    if (player.getGameMode() != GameMode.SPECTATOR) player.setGameMode(GameMode.SPECTATOR);
                    player.setInvisible(false);
                }
            };
            delayTP.runTaskLater(plugin, 5);
            return true;
        }
        return false;
    }
}
