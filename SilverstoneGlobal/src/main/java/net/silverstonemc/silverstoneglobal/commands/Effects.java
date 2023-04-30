package net.silverstonemc.silverstoneglobal.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

public class Effects implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length > 0) {
            Player player = Bukkit.getPlayer(args[0]);

            // If player is null, cancel the command
            if (player == null) {
                sender.sendMessage(
                    Component.text("Please provide an online player!").color(NamedTextColor.RED));
                return true;
            }

            String username = player.getName();

            if (player.getActivePotionEffects().isEmpty()) {
                sender.sendMessage(Component.text(username).color(NamedTextColor.AQUA)
                    .append(Component.text(" has no active effects.").color(NamedTextColor.GREEN)));
                return true;
            }

            sender.sendMessage(Component.text("\n" + username).color(NamedTextColor.AQUA)
                .append(Component.text(" has the following effects:\n ").color(NamedTextColor.GREEN)));

            for (PotionEffect effect : player.getActivePotionEffects())
                sender.sendMessage(
                    Component.text(effect.getType().getName() + " " + (effect.getAmplifier() + 1))
                        .color(NamedTextColor.AQUA)
                        .append(Component.text(" | ").color(NamedTextColor.DARK_AQUA)).append(
                            Component.text((effect.getDuration() / 20) + "s").color(NamedTextColor.AQUA)));

        } else sender.sendMessage(Component.text("Please provide a player!").color(NamedTextColor.RED));
        return true;
    }
}
