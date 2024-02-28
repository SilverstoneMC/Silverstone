package net.silverstonemc.silverstoneglobal.events;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Gamemode implements Listener {
    @EventHandler
    public void gamemodeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        // Night vision
        if (event.getNewGameMode().equals(GameMode.SPECTATOR)) player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,
            -1,
            255,
            false,
            false,
            false));
        else if (player.getGameMode().equals(GameMode.SPECTATOR))
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
    }
}
