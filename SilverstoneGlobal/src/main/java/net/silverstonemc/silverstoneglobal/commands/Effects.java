package net.silverstonemc.silverstoneglobal.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

public class Effects implements CommandExecutor {

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length > 0) {
            Player player = Bukkit.getPlayer(args[0]);

            // If player is null, cancel the command
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Please provide an online player!");
                return true;
            }

            String username = player.getName();

            if (player.getActivePotionEffects().isEmpty()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b" + username + " &ahas no active effects."));
                return true;
            }

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "\n&b" + username + " &ahas the following effects:\n "));
            for (PotionEffect effect : player.getActivePotionEffects())
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b" + effect.getType()
                        .getName() + " " + (effect.getAmplifier() + 1) + " &3| &b" + (effect.getDuration() / 20) + "s"));
        } else sender.sendMessage(ChatColor.RED + "Please provide a player!");
        return true;
    }
}
