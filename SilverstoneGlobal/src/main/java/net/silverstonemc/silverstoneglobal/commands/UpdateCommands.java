package net.silverstonemc.silverstoneglobal.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class UpdateCommands implements CommandExecutor {
    private final Map<String, Long> cooldowns = new HashMap<>();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length > 0) if (sender.hasPermission("silverstone.admin")) {
            Player player = Bukkit.getPlayer(args[0]);

            // If player is null, cancel the check
            if (player == null) {
                sender.sendMessage(
                    Component.text("Please provide an online player!").color(NamedTextColor.RED));
                return true;
            }

            player.updateCommands();
            String username = player.getName();
            sender.sendMessage(
                Component.text("Commands updated for " + username + "!").color(NamedTextColor.GREEN));

        } else sender.sendMessage(
            Component.text("You don't have permission to do that!").color(NamedTextColor.RED));

        else {
            // If console does not specify a player
            if (!(sender instanceof Player player)) {
                sender.sendMessage(
                    Component.text("Please provide an online player!").color(NamedTextColor.RED));
                return true;
            }

            if (cooldowns.containsKey(player.getName()))
                if (cooldowns.get(player.getName()) > System.currentTimeMillis()) {
                    // Still on cooldown
                    player.sendMessage(
                        Component.text("You may update your commands again in ").color(NamedTextColor.RED)
                            .append(Component.text(
                                    (cooldowns.get(player.getName()) - System.currentTimeMillis()) / 1000)
                                .color(NamedTextColor.GRAY))
                            .append(Component.text(" seconds.").color(NamedTextColor.RED)));
                    return true;
                }

            player.updateCommands();
            cooldowns.put(player.getName(), System.currentTimeMillis() + 30000);
            sender.sendMessage(Component.text("Commands updated!").color(NamedTextColor.GREEN));
        }
        return true;
    }
}
