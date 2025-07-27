package net.silverstonemc.silverstoneminigames.events;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldChange implements Listener {
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.SPECTATOR && event.getPlayer()
            .getGameMode() != GameMode.CREATIVE) event.getPlayer().clearActivePotionEffects();

        event.getPlayer().resetPlayerTime();
        event.getPlayer().resetPlayerWeather();
    }
}
