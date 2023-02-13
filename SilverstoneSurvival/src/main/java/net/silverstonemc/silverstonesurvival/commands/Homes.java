package net.silverstonemc.silverstonesurvival.commands;

import net.silverstonemc.silverstonesurvival.SilverstoneMain;
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

public class Homes implements CommandExecutor {

    private final JavaPlugin plugin;

    public Homes(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    final IEssentials essentials = SilverstoneMain.getInstance().getEssentials();

    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("homes?")) {
            int homes = 0;
            if (sender.hasPermission("essentials.sethome.multiple.default")) homes = 10;
            if (sender.hasPermission("essentials.sethome.multiple.member")) homes = 15;
            if (sender.hasPermission("essentials.sethome.multiple.vip")) homes = 25;
            if (sender.hasPermission("essentials.sethome.multiple.vip+")) homes = 50;
            if (sender.hasPermission("essentials.sethome.multiple.mvp")) homes = 100;
            if (sender.hasPermission("essentials.sethome.multiple.unlimited")) homes = -1;

            if (homes > -1)
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3You can set up to &a" + homes + " &3homes."));
            else
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3You can set &aunlimited &3homes."));
        }

        if (cmd.getName().equalsIgnoreCase("homec")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "Sorry, but only players can do that.");
                return true;
            }

            player.sendMessage(ChatColor.DARK_AQUA + "Teleporting...");
            if (player.getGameMode().equals(GameMode.SPECTATOR))
                player.setSpectatorTarget(null);
            essentials.getUser(player)
                    .getAsyncTeleport()
                    .now(new Location(Bukkit.getWorld("utility"), -211.3, 38, -122.3, 107.2f, 9.5f), false, PlayerTeleportEvent.TeleportCause.COMMAND, new CompletableFuture<>());

            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    player.setGameMode(GameMode.CREATIVE);
                }
            };
            task.runTaskLater(plugin, 10);
        }
        return true;
    }
}
