package net.silverstonemc.silverstoneglobal.commands;

import net.ess3.api.IEssentials;
import net.kyori.adventure.text.Component;
import net.silverstonemc.silverstoneglobal.SilverstoneGlobal;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("ConstantConditions")
public class TP implements CommandExecutor {

    final IEssentials essentials = SilverstoneGlobal.getInstance().getEssentials();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Sorry, but only players can do that.");
            return true;
        }

        int multiplier = 16;

        if (cmd.getName().equalsIgnoreCase("tpregion")) multiplier = 512;

        if (args.length > 1) {
            Player player = (Player) sender;

            player.sendMessage(ChatColor.DARK_AQUA + "Teleporting...");

            // If player specifies world
            if (args.length > 2) if (Bukkit.getWorld(args[2]) != null) {
                Location loc = Bukkit.getWorld(args[2])
                        .getHighestBlockAt(Integer.parseInt(args[0]) * multiplier, Integer.parseInt(args[1]) * multiplier)
                        .getLocation()
                        .add(0, 1, 0);

                if (player.isFlying()) essentials.getUser(player)
                        .getAsyncTeleport()
                        .now(loc.add(0, 3, 0), false, PlayerTeleportEvent.TeleportCause.COMMAND, new CompletableFuture<>());
                else essentials.getUser(player)
                        .getAsyncTeleport()
                        .now(loc, false, PlayerTeleportEvent.TeleportCause.COMMAND, new CompletableFuture<>());
            } else player.sendMessage(Component.text(ChatColor.RED + "That world doesn't exist!"));
            else {
                Location loc = player.getWorld()
                        .getHighestBlockAt(Integer.parseInt(args[0]) * multiplier, Integer.parseInt(args[1]) * multiplier)
                        .getLocation()
                        .add(0, 1, 0);

                if (player.isFlying()) essentials.getUser(player)
                        .getAsyncTeleport()
                        .now(loc.add(0, 3, 0), false, PlayerTeleportEvent.TeleportCause.COMMAND, new CompletableFuture<>());
                else essentials.getUser(player)
                        .getAsyncTeleport()
                        .now(loc, false, PlayerTeleportEvent.TeleportCause.COMMAND, new CompletableFuture<>());
            }
            return true;
        }
        return false;
    }
}
