package net.silverstonemc.silverstonemain.commands;

import net.ess3.api.IEssentials;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.silverstonemc.silverstonemain.SilverstoneMain;
import org.bukkit.Bukkit;
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

public class JHome implements CommandExecutor {
    public JHome(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;
    private final IEssentials essentials = SilverstoneMain.getInstance().getEssentials();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Sorry, but only players can do that.", NamedTextColor.RED));
            return true;
        }

        player.sendMessage(Component.text("Teleporting...", NamedTextColor.DARK_AQUA));
        if (player.getGameMode() == GameMode.SPECTATOR) player.setSpectatorTarget(null);
        essentials.getUser(player).getAsyncTeleport().now(new Location(Bukkit.getWorld("utility"),
                79.5,
                71,
                135.5,
                0,
                0),
            false,
            PlayerTeleportEvent.TeleportCause.COMMAND,
            new CompletableFuture<>());

        new BukkitRunnable() {
            @Override
            public void run() {
                player.setGameMode(GameMode.CREATIVE);
            }
        }.runTaskLater(plugin, 10);
        return true;
    }
}