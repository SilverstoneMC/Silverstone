package net.silverstonemc.silverstoneglobal.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class UpdateCommands implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NonNull [] args) {
        if (args.length == 0 && !(sender instanceof Player)) return false;

        Player target;
        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Component.text("Please provide an online player!", NamedTextColor.RED));
                return true;
            }
        } else target = (Player) sender;

        target.updateCommands();
        String username = target.getName();
        sender.sendMessage(Component.text("Commands updated for " + username + "!", NamedTextColor.GREEN));

        return true;
    }
}
