package net.silverstonemc.silverstoneglobal.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Freeze implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length > 1) {
            int x = Integer.parseInt(args[0]);
            sender.sendMessage(Component.text("Freezing server for " + x + " seconds...",
                NamedTextColor.RED,
                TextDecoration.BOLD));
            try {
                Thread.sleep(x * 1000L);
            } catch (InterruptedException ignored) {
            }
            sender.sendMessage(Component.text("Done freezing!", NamedTextColor.GREEN));
            return true;
        }
        return false;
    }
}
