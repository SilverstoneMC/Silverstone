package me.jasonhorkles.silverstoneglobal.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LocalChat implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Sorry, but only players can do that.");
            return true;
        }

        if (args.length > 0) {
            String message = "";

            for (String arg : args) {
                message = message.concat(arg);
                message = message.concat(" ");
            }

            message = message.trim();
            player.chat(message);
            return true;
        }
        return false;
    }
}
