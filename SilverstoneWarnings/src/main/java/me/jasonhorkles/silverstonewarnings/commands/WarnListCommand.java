package me.jasonhorkles.silverstonewarnings.commands;

import me.jasonhorkles.silverstonewarnings.SilverstoneWarnings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

@SuppressWarnings("ConstantConditions")
public class WarnListCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lAll warnings:"));

        for (String uuid : SilverstoneWarnings.data.getConfig().getConfigurationSection("data").getKeys(false))
            for (String warning : SilverstoneWarnings.data.getConfig()
                    .getConfigurationSection("data." + uuid)
                    .getKeys(false))
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + Bukkit.getOfflinePlayer(UUID.fromString(uuid))
                        .getName() + " - " + warning + " - " + SilverstoneWarnings.data.getConfig()
                        .getInt("data." + uuid + "." + warning)));
        return true;
    }
}
