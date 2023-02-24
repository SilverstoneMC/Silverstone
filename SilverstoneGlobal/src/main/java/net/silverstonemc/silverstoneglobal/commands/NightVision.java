package net.silverstonemc.silverstoneglobal.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class NightVision implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to do that!");
            return true;
        }

        if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION))
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        else player.addPotionEffect(
            new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 255, false, false, false));
        return true;
    }
}
