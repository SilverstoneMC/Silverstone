package net.silverstonemc.silverstonewarnings.commands;

import net.silverstonemc.silverstonewarnings.SilverstoneWarnings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@SuppressWarnings("ConstantConditions")
public class WarnQueueCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lQueued warnings:"));
        for (String uuid : SilverstoneWarnings.queue.getConfig().getConfigurationSection("queue").getKeys(false))
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + Bukkit.getOfflinePlayer(UUID.fromString(uuid))
                    .getName() + " - " + SilverstoneWarnings.queue.getConfig().getString("queue." + uuid)));
        return true;
    }
}