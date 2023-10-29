package net.silverstonemc.silverstoneminigames;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldChange implements Listener {
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        if (!event.getPlayer().getGameMode().equals(GameMode.SPECTATOR) && !event.getPlayer().getGameMode()
            .equals(GameMode.CREATIVE)) event.getPlayer().clearActivePotionEffects();
    }
}
