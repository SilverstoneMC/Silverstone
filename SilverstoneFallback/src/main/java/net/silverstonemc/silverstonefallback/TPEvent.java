package net.silverstonemc.silverstonefallback;

import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TPEvent implements Listener {

    @EventHandler
    public void tpEvent(PlayerTeleportEvent event) {
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_PLAYER_HURT, 10, 1);
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_PLAYER_DEATH, 10, 1);
    }
}
