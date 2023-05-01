package net.silverstonemc.silverstoneglobal.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("DataFlowIssue")
public class TP implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(
                Component.text("Sorry, but only players can do that.").color(NamedTextColor.RED));
            return true;
        }

        int multiplier = 16;

        if (cmd.getName().equalsIgnoreCase("tpregion")) multiplier = 512;

        if (args.length > 1) {
            Player player = (Player) sender;

            player.sendMessage(Component.text("Teleporting...").color(NamedTextColor.DARK_AQUA));

            // If player specifies world
            if (args.length > 2) if (Bukkit.getWorld(args[2]) != null) {
                Location loc = Bukkit.getWorld(args[2])
                    .getHighestBlockAt(Integer.parseInt(args[0]) * multiplier,
                        Integer.parseInt(args[1]) * multiplier).getLocation().add(0, 1, 0);

                if (player.isFlying()) player.teleportAsync(loc.add(0, 3, 0));
                else player.teleportAsync(loc);

            } else player.sendMessage(Component.text("That world doesn't exist!").color(NamedTextColor.RED));

            else {
                Location loc = player.getWorld().getHighestBlockAt(Integer.parseInt(args[0]) * multiplier,
                    Integer.parseInt(args[1]) * multiplier).getLocation().add(0, 1, 0);

                if (player.isFlying()) player.teleportAsync(loc.add(0, 3, 0));
                else player.teleportAsync(loc);
            }
            return true;
        }
        return false;
    }
}
