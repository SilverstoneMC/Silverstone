package net.silverstonemc.silverstonewarnings;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.ArrayList;

public record UndoWarning(JavaPlugin plugin) {

    @SuppressWarnings("ConstantConditions")
    public void undoWarning(OfflinePlayer player, String reason, @Nullable Integer currentWarningCount) {
        if (currentWarningCount != null) {
            // Grab the amount of un-punishments in the config
            int unpunishmentCount = plugin.getConfig()
                    .getConfigurationSection("reasons." + reason + ".remove")
                    .getKeys(false)
                    .toArray().length;

            // Get the correct warning number
            int unpunishmentNumber = currentWarningCount % unpunishmentCount;
            if (unpunishmentNumber == 0) unpunishmentNumber = unpunishmentCount;

            // Un-warn the player
            ArrayList<String> cmdList = new ArrayList<>(plugin.getConfig()
                    .getStringList("reasons." + reason + ".remove." + unpunishmentNumber));
            for (String s : cmdList)
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("{player}", player.getName()));
        } else if (reason.equals("all")) {
            plugin.getLogger().info("=============================================");
            plugin.getLogger().info("Clearing all of " + player.getName() + "'s warnings...");
            plugin.getLogger().info("=============================================");
            // For each warn reason in the config
            // For each number in said reason
            // Run each command in each number
            for (String configReasons : plugin.getConfig().getConfigurationSection("reasons").getKeys(false))
                for (String configReasonNumbers : plugin.getConfig()
                        .getConfigurationSection("reasons." + configReasons + ".remove")
                        .getKeys(false))
                    for (String configReasonCommands : plugin.getConfig()
                            .getStringList("reasons." + configReasons + ".remove." + configReasonNumbers))
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), configReasonCommands.replace("{player}", player.getName()));
        } else {
            plugin.getLogger().info("=============================================");
            plugin.getLogger().info("Clearing all of " + player.getName() + "'s '" + reason + "' warnings...");
            plugin.getLogger().info("=============================================");

            // For each number in reason
            // Run each command in each number
            for (String configReasonNumbers : plugin.getConfig()
                    .getConfigurationSection("reasons." + reason + ".remove")
                    .getKeys(false))
                for (String configReasonCommands : plugin.getConfig()
                        .getStringList("reasons." + reason + ".remove." + configReasonNumbers))
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), configReasonCommands.replace("{player}", player.getName()));
        }
    }
}
