package net.silverstonemc.silverstoneproxy.events;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.draycia.carbon.api.event.events.CarbonChatEvent;
import net.draycia.carbon.api.event.events.CarbonPrivateChatEvent;
import net.draycia.carbon.api.users.CarbonPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;
import net.silverstonemc.silverstoneproxy.utils.NicknameUtils;

import java.util.HashMap;
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

        CarbonPlayer player = event.sender();

        if (!player.hasPermission("silverstone.chatspam.bypass")) {
            if (lastMessages.containsKey(player.uuid()))
                if (lastMessages.get(player.uuid()).equals(event.message())) {
                    player.sendMessage(Component.text("Please don't spam the same message!",
                        NamedTextColor.RED));
                    event.cancelled(true);
                    return;
                }

            lastMessages.put(player.uuid(), event.message());

            i.server.getScheduler().buildTask(i, () -> lastMessages.remove(player.uuid())).delay(15,
                TimeUnit.SECONDS).schedule();
        }

        // Filter URLs
        if (!player.hasPermission("silverstone.chatfilter.bypass")) {
            Component replacedMessage = replaceUrl(event.message());
            if (!event.message().equals(replacedMessage)) event.message(replacedMessage);
        }

        // Send chat sounds
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

        // Socialspy
        for (Player player : i.server.getAllPlayers())
            if (player.hasPermission("silverstone.socialspy.enabled"))
                if (!player.getUniqueId().toString().equals(event.sender().uuid().toString()) && !player
                    .getUniqueId().toString().equals(event.recipient().uuid().toString())) player.sendMessage(
                    Component.text().append(Component.text("SPY ", NamedTextColor.BLUE, TextDecoration.BOLD))
                        .append(Component.text("> ", NamedTextColor.AQUA, TextDecoration.BOLD))
                        .append(new NicknameUtils(i).getDisplayName(event.sender().uuid())
                            .colorIfAbsent(NamedTextColor.GOLD)).append(Component.text(" ➡ ",
                            NamedTextColor.DARK_GRAY)).append(new NicknameUtils(i)
                            .getDisplayName(event.recipient().uuid()).colorIfAbsent(NamedTextColor.GRAY)).append(
                            Component.text(" | ", NamedTextColor.DARK_AQUA))
                        .append(event.message().colorIfAbsent(NamedTextColor.GRAY)).build());
    }

    private Component replaceUrl(Component message) {
        //noinspection DataFlowIssue
        Pattern pattern = Pattern.compile(i.fileManager.files.get(CONFIG).node("url-filter").getString());

        return message.replaceText(builder -> builder.match(pattern)
            .replacement(match -> Component.text("[URL REDACTED]", NamedTextColor.DARK_GRAY)
                .clickEvent(ClickEvent.openUrl(""))));
    }
}
