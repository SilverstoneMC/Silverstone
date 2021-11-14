package me.jasonhorkles.silverstoneglobal.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class UpdateCommands implements CommandExecutor {

    private final Map<String, Long> cooldowns = new HashMap<>();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length > 0) if (sender.hasPermission("silverstone.admin")) {
            Player player = Bukkit.getPlayer(args[0]);

            // If player is null, cancel the check
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Please provide an online player!");
                return true;
            }

            player.updateCommands();
            String username = player.getName();
            sender.sendMessage(ChatColor.GREEN + "Commands updated for " + username + "!");
        } else sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
        else {
            // If console does not specify a player
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "Please provide an online player!");
                return true;
            }

            if (cooldowns.containsKey(player.getName()))
                if (cooldowns.get(player.getName()) > System.currentTimeMillis()) {
                    // Still on cooldown
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou may update your commands again in &7" + ((cooldowns
                            .get(player.getName()) - System.currentTimeMillis()) / 1000) + " &cseconds."));
                    return true;
                }

            player.updateCommands();
            cooldowns.put(player.getName(), System.currentTimeMillis() + 30000);
            sender.sendMessage(ChatColor.GREEN + "Commands updated!");
        }
        return true;
    }
}
