package net.silverstonemc.silverstonewarnings.commands;

import net.silverstonemc.silverstonewarnings.SilverstoneWarnings;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@SuppressWarnings("ConstantConditions")
public class WarningsCommand implements CommandExecutor {

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        String arg0;
        // If player specified but sender has no permisison
        if (args.length < 1) { // If no player specified
            // If console didn't specify a player
            if (!(sender instanceof Player)) return false;
            arg0 = sender.getName();
        } else if (!sender.hasPermission("sswarnings.warn")) arg0 = sender.getName();
        else arg0 = args[0];

        OfflinePlayer offlinePlayer = SilverstoneWarnings.getOfflinePlayer(sender, arg0);
        if (offlinePlayer == null) return true;
        UUID uuid = offlinePlayer.getUniqueId();

        // If not in queue
        // And has no warnings
        if (!SilverstoneWarnings.queue.getConfig().contains("queue." + uuid))
            if (!SilverstoneWarnings.data.getConfig().contains("data." + uuid)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b" + offlinePlayer.getName() + " &ahas no warnings!"));
                return true;
            }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l" + offlinePlayer.getName() + "'s warnings:"));
        // If any warnings already exist
        if (SilverstoneWarnings.data.getConfig().contains("data." + uuid))
            for (String reasonList : SilverstoneWarnings.data.getConfig()
                    .getConfigurationSection("data." + uuid)
                    .getKeys(false))
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + reasonList + " - " + SilverstoneWarnings.data
                        .getConfig()
                        .getInt("data." + uuid + "." + reasonList)));
        // If in queue
        if (SilverstoneWarnings.queue.getConfig().contains("queue." + uuid))
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + SilverstoneWarnings.queue.getConfig()
                    .getString("queue." + uuid) + " (Queued)"));
        return true;
    }
}
