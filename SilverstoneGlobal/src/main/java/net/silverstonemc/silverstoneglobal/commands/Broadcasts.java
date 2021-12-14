package net.silverstonemc.silverstoneglobal.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Broadcasts implements CommandExecutor {

    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        switch (cmd.getName().toLowerCase()) {
            case "bclag" -> {
                for (Player player : Bukkit.getOnlinePlayers())
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lWARNING &b&l> &aThe server may experience lag for a moment!"));
                if (!(sender instanceof Player)) sender.sendMessage(ChatColor.GREEN + "Lag broadcast sent!");
            }

            case "bcnolag" -> {
                for (Player player : Bukkit.getOnlinePlayers())
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lNOTICE &b&l> &aThe server should no longer lag."));
                if (!(sender instanceof Player)) sender.sendMessage(ChatColor.GREEN + "No more lag broadcast sent!");
            }

            case "bcrestart" -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lWARNING &b&l> &aThe server will restart soon!"));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.MASTER, 100, 1.6f);
                }
                if (!(sender instanceof Player)) sender.sendMessage(ChatColor.GREEN + "Restart broadcast sent!");
            }

            case "bcshutdown" -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lWARNING &b&l> &aThe server will shut down soon!"));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.MASTER, 100, 1.6f);
                }
                if (!(sender instanceof Player)) sender.sendMessage(ChatColor.GREEN + "Shutdown broadcast sent!");
            }
        }
        return true;
    }
}
