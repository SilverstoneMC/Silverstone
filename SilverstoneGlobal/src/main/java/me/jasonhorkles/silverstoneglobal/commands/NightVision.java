package me.jasonhorkles.silverstoneglobal.commands;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NightVision implements CommandExecutor, Listener {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to do that!");
            return true;
        }

        if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION))
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        else
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 255, false, false, false));
        return true;
    }

    @EventHandler
    public void spectator(PlayerGameModeChangeEvent event) {
        if (event.getNewGameMode().equals(GameMode.SPECTATOR)) event.getPlayer()
                .addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 255, false, false, false));
        else if (event.getPlayer().getGameMode().equals(GameMode.SPECTATOR))
            event.getPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);
    }
}
