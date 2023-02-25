package net.silverstonemc.silverstonemain.commands;

import net.silverstonemc.silverstonemain.SilverstoneMain;
import net.ess3.api.IEssentials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class Home implements CommandExecutor {
    public Home(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;
    private final IEssentials essentials = SilverstoneMain.getInstance().getEssentials();

    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Sorry, but only players can do that.");
            return true;
        }

        player.sendMessage(ChatColor.DARK_AQUA + "Teleporting...");
        if (player.getGameMode().equals(GameMode.SPECTATOR)) player.setSpectatorTarget(null);
        essentials.getUser(player).getAsyncTeleport()
            .now(new Location(Bukkit.getWorld("mini_empty"), -211.3, 38, -122.3, 107.2f, 9.5f), false,
                PlayerTeleportEvent.TeleportCause.COMMAND, new CompletableFuture<>());

        new BukkitRunnable() {
            @Override
            public void run() {
                player.setGameMode(GameMode.CREATIVE);
            }
        }.runTaskLater(plugin, 10);
        return true;
    }
}