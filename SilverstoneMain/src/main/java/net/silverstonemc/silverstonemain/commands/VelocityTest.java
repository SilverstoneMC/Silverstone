package net.silverstonemc.silverstonemain.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class VelocityTest implements CommandExecutor {
    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length < 4) return false;

        Player target = Bukkit.getPlayer(args[0]);

        // If player is null
        if (target == null) {
            sender.sendMessage(Component.text("Please provide an online player!").color(NamedTextColor.RED));
            return true;
        }

        Vector vector = target.getLocation().getDirection().setX(Double.parseDouble(args[1]))
            .setY(Double.parseDouble(args[2])).setZ(Double.parseDouble(args[3]));
        target.setVelocity(vector);
        
        return true;
    }
}
