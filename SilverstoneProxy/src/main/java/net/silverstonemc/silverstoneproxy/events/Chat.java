package net.silverstonemc.silverstoneproxy.events;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.draycia.carbon.api.event.events.CarbonChatEvent;
import net.draycia.carbon.api.event.events.CarbonPrivateChatEvent;
import net.draycia.carbon.api.users.CarbonPlayer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static net.silverstonemc.silverstoneproxy.ConfigurationManager.FileType.CONFIG;

public class Chat {
    public Chat(SilverstoneProxy instance) {
        i = instance;
    }

    private final SilverstoneProxy i;
    public static final HashMap<UUID, Component> lastMessages = new HashMap<>();

    public void onChat(CarbonChatEvent event) {
        if (event.cancelled()) return;

        CarbonPlayer sender = event.sender();

        if (!sender.hasPermission("silverstone.chatspam.bypass")) {
            if (lastMessages.containsKey(sender.uuid()))
                if (lastMessages.get(sender.uuid()).equals(event.message())) {
                    sender.sendMessage(Component.text(
                        "Please don't spam the same message!",
                        NamedTextColor.RED));
                    event.cancelled(true);
                    return;
                }

            lastMessages.put(sender.uuid(), event.message());

            i.server.getScheduler().buildTask(i, () -> lastMessages.remove(sender.uuid())).delay(
                15,
                TimeUnit.SECONDS).schedule();
        }

        // Filter URLs
        if (!sender.hasPermission("silverstone.chatfilter.bypass")) {
            Component replacedMessage = replaceUrl(event.message());
            if (!event.message().equals(replacedMessage)) event.message(replacedMessage);
        }

        // Send chat sounds
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        switch (event.chatChannel().key().toString()) {
            case "carbon:global", "carbon:jsay" -> {
                out.writeUTF("globalchatsound");
                out.writeUTF(sender.uuid().toString());
            }

            case "carbon:staffchat" -> {
                out.writeUTF("staffchatsound");
                out.writeUTF(sender.uuid().toString());
            }

            case "carbon:broadcast" -> out.writeUTF("broadcastsound");

            case "carbon:partychat" -> {
                // Using usernames instead of UUIDs to shorten the message
                List<String> playerNames = new ArrayList<>();

                for (Audience players : event.chatChannel().recipients(sender))
                    players.forEachAudience(audience -> {
                        if (audience instanceof Player player) playerNames.add(player.getUsername());
                    });

                out.writeUTF("partychatsound");
                out.writeUTF(sender.uuid().toString());
                out.writeUTF(String.join(",", playerNames));
            }

            default -> {
                return;
            }
        }

        for (RegisteredServer servers : i.server.getAllServers())
            servers.sendPluginMessage(SilverstoneProxy.IDENTIFIER, out.toByteArray());
    }

    public void onPrivateChat(CarbonPrivateChatEvent event) {
        if (event.cancelled()) return;

        // Filter URLs
        if (!event.sender().hasPermission("silverstone.chatfilter.bypass")) {
            Component replacedMessage = replaceUrl(event.message());
            if (!event.message().equals(replacedMessage)) event.message(replacedMessage);
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("privatesound");
        out.writeUTF(event.recipient().uuid().toString());

        for (RegisteredServer servers : i.server.getAllServers())
            servers.sendPluginMessage(SilverstoneProxy.IDENTIFIER, out.toByteArray());
    }

    private Component replaceUrl(Component message) {
        //noinspection DataFlowIssue
        Pattern pattern = Pattern.compile(i.fileManager.files.get(CONFIG).node("url-filter").getString());

        return message.replaceText(builder -> builder.match(pattern)
            .replacement(match -> Component.text("[URL REDACTED]", NamedTextColor.DARK_GRAY)
                .clickEvent(ClickEvent.openUrl(""))));
    }
}
