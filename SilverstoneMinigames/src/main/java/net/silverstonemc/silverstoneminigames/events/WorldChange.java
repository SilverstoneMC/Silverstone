package net.silverstonemc.silverstoneminigames.events;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldChange implements Listener {
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.SPECTATOR && player.getGameMode() != GameMode.CREATIVE)
            player.clearActivePotionEffects();

        player.resetPlayerTime();
        player.resetPlayerWeather();
    }
}
