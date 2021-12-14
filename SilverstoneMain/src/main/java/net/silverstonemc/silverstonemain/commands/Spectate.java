package net.silverstonemc.silverstonemain.commands;

import net.silverstonemc.silverstonemain.SilverstoneMain;
import net.ess3.api.IEssentials;
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

    final IEssentials essentials = SilverstoneMain.getInstance().getEssentials();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Sorry, but only players can do that.");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("spectate")) if (args.length > 0) {
            Player player = Bukkit.getPlayer(args[0]);
            Player playerSender = (Player) sender;

            // If player is null
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Please provide an online player!");
                return true;
            }

            playerSender.setGameMode(GameMode.SPECTATOR);
            playerSender.setSpectatorTarget(null);
            essentials.getUser(playerSender)
                    .getAsyncTeleport()
                    .now(player, false, PlayerTeleportEvent.TeleportCause.COMMAND, new CompletableFuture<>());
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    playerSender.setSpectatorTarget(player);
                }
            };
            task.runTaskLater(plugin, 5);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("watch")) if (args.length > 0) {
            Player player = Bukkit.getPlayer(args[0]);
            Player playerSender = (Player) sender;

            // If player is null
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Please provide an online player!");
                return true;
            }

            playerSender.setGameMode(GameMode.SPECTATOR);
            playerSender.setSpectatorTarget(null);
            essentials.getUser(playerSender)
                    .getAsyncTeleport()
                    .now(player, false, PlayerTeleportEvent.TeleportCause.COMMAND, new CompletableFuture<>());
            return true;
        }
        return false;
    }
}
