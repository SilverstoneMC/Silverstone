package net.silverstonemc.silverstoneproxy.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Chat {
    public Chat(SilverstoneProxy instance) {
        i = instance;
    }

    private final SilverstoneProxy i;
    private static final HashMap<UUID, String> lastMessages = new HashMap<>();

    @Subscribe
    public void onChat(PlayerChatEvent event) {
        if (!event.getPlayer().hasPermission("silverstone.chatspam.bypass")) {
            if (lastMessages.containsKey(event.getPlayer().getUniqueId()))
                if (lastMessages.get(event.getPlayer().getUniqueId()).equalsIgnoreCase(event.getMessage())) {
                    event.getPlayer().sendMessage(
                        Component.text("Please don't spam the same message!", NamedTextColor.RED));
                    event.setResult(PlayerChatEvent.ChatResult.denied());
                    return;
                }

            lastMessages.put(event.getPlayer().getUniqueId(), event.getMessage());

            i.server.getScheduler().buildTask(i, () -> lastMessages.remove(event.getPlayer().getUniqueId())).delay(15, TimeUnit.SECONDS).schedule();
        }
        
        
    }
}
