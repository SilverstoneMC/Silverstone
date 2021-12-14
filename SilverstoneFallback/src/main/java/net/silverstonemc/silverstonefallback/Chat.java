package net.silverstonemc.silverstonefallback;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class Chat implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void cancelChat(AsyncChatEvent event) {
        event.setCancelled(true);
    }
}