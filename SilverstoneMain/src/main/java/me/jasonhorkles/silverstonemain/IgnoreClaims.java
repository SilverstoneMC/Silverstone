package me.jasonhorkles.silverstonemain;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class IgnoreClaims implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("silverstone.ignoreclaims"))
            event.getPlayer().performCommand("ignoreclaims");
    }
}
