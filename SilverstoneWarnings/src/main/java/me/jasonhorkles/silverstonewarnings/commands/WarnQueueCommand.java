package me.jasonhorkles.silverstonewarnings.commands;

import me.jasonhorkles.silverstonewarnings.SilverstoneWarnings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

@SuppressWarnings("ConstantConditions")
public class WarnQueueCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lQueued warnings:"));
        for (String uuid : SilverstoneWarnings.queue.getConfig().getConfigurationSection("queue").getKeys(false))
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + Bukkit.getOfflinePlayer(UUID.fromString(uuid))
                    .getName() + " - " + SilverstoneWarnings.queue.getConfig().getString("queue." + uuid)));
        return true;
    }
}
