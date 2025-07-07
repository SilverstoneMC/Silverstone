package net.silverstonemc.silverstoneglobal.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CenterOnBlock implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Sorry, but only players can do that.", NamedTextColor.RED));
            return true;
        }

        player.sendMessage(Component.text("Teleporting...", NamedTextColor.DARK_AQUA));

        Location playerLoc = player.getLocation();
        Location loc = playerLoc.getBlock().getLocation().add(0.5, 0, 0.5);
        loc.setPitch(playerLoc.getPitch());
        loc.setYaw(playerLoc.getYaw());

        player.teleportAsync(loc);
        return true;
    }
}
