package me.jasonhorkles.silverstoneglobal.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Freeze implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 1) {
            int x = Integer.parseInt(args[0]);
            sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Freezing server for " + x + " seconds...");
            try {
                Thread.sleep(x * 1000L);
            } catch (InterruptedException ignored) {
            }
            sender.sendMessage(ChatColor.GREEN + "Done freezing!");
            return true;
        }
        return false;
    }
}
