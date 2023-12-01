package net.silverstonemc.silverstoneproxy.events;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.draycia.carbon.api.event.events.CarbonChatEvent;
import net.draycia.carbon.api.event.events.CarbonPrivateChatEvent;
import net.draycia.carbon.api.users.CarbonPlayer;
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
    private static final HashMap<UUID, Component> lastMessages = new HashMap<>();

    public void onChat(CarbonChatEvent event) {
        CarbonPlayer player = event.sender();

        if (!player.hasPermission("silverstone.chatspam.bypass")) {
            if (lastMessages.containsKey(player.uuid()))
                if (lastMessages.get(player.uuid()).equals(event.message())) {
                    player.sendMessage(
                        Component.text("Please don't spam the same message!", NamedTextColor.RED));
                    event.cancelled(true);
                    return;
                }

            lastMessages.put(player.uuid(), event.message());

            i.server.getScheduler().buildTask(i, () -> lastMessages.remove(player.uuid()))
                .delay(15, TimeUnit.SECONDS).schedule();
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        switch (event.chatChannel().key().toString()) {
            case "carbon:global", "carbon:jsay" -> {
                out.writeUTF("globalchatsound");
                out.writeUTF(player.uuid().toString());
            }
            
            case "carbon:staffchat" -> {
                out.writeUTF("staffchatsound");
                out.writeUTF(player.uuid().toString());
            }
            
            case "carbon:broadcast" -> out.writeUTF("broadcastsound");
        }
        
        for (RegisteredServer servers : i.server.getAllServers())
            servers.sendPluginMessage(SilverstoneProxy.IDENTIFIER, out.toByteArray());
    }
    
    public void onPrivateChat(CarbonPrivateChatEvent event) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("privatesound");
        out.writeUTF(event.recipient().uuid().toString());
        
        for (RegisteredServer servers : i.server.getAllServers())
            servers.sendPluginMessage(SilverstoneProxy.IDENTIFIER, out.toByteArray());
    }
}
