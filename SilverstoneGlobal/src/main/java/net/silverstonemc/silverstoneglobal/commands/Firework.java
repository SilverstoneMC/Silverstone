package net.silverstonemc.silverstoneglobal.commands;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;

public class Firework implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Sorry, but only players can do that.");
            return true;
        }

        org.bukkit.entity.Firework fw = (org.bukkit.entity.Firework) player.getWorld()
            .spawnEntity(player.getLocation(), EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).withColor(Color.BLUE)
            .with(FireworkEffect.Type.BALL_LARGE).withFlicker().build());

        fw.setFireworkMeta(fwm);
        fw.detonate();
        return true;
    }
}
