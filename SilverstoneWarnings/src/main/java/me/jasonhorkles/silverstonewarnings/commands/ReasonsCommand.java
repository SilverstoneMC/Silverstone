package me.jasonhorkles.silverstonewarnings.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

@SuppressWarnings("ConstantConditions")
public record ReasonsCommand(JavaPlugin plugin) implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lAvailable warning reasons:"));

        ArrayList<String> reasonList = new ArrayList<>(plugin.getConfig()
                .getConfigurationSection("reasons")
                .getKeys(false));
        for (int x = 0; x < reasonList.size(); x = x + 3)
            try {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + reasonList.get(x) + "  &8&l|&7  " + reasonList
                        .get(x + 1) + "  &8&l|&7  " + reasonList.get(x + 2)));
            } catch (IndexOutOfBoundsException e1) {
                try {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + reasonList.get(x) + "  &8&l|&7  " + reasonList
                            .get(x + 1)));
                } catch (IndexOutOfBoundsException e2) {
                    sender.sendMessage(ChatColor.GRAY + reasonList.get(x));
                }
            }
        return true;
    }
}