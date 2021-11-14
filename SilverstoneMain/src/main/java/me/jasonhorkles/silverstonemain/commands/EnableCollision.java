package me.jasonhorkles.silverstonemain.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("ConstantConditions")
public class EnableCollision implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Sorry, but only players can do that.");
            return true;
        }

        if (player.getWorld().getName().toLowerCase().startsWith("mini")) return false;

        player.sendMessage(ChatColor.GREEN + "Collision enabled. It will disable again when you go to Spawn or the Minigame warp.");
        Bukkit.getScoreboardManager().getMainScoreboard().getTeam("NoCollision").removeEntry(player.getName());
        return true;
    }
}
