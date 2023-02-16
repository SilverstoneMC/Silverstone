package net.silverstonemc.silverstoneminigames;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PvP implements Listener {
    @EventHandler
    public void onKill(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();

        if (killer != null && (killer != event.getEntity()) && killer.hasPermission(
            "silverstone.minigames.pvp")) killer.setHealth(20);
    }
}
