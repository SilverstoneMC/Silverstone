package net.silverstonemc.silverstoneproxy;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.concurrent.TimeUnit;

public class AutoBroadcast {
    public AutoBroadcast(SilverstoneProxy instance) {
        i = instance;
    }

    private final SilverstoneProxy i;
    private static int lastMessage;

    private final TextComponent[] messages = {
        Component.text("Love the server? Help keep it going with ", NamedTextColor.DARK_GREEN).append(
            Component.text("/buy", NamedTextColor.DARK_AQUA).clickEvent(ClickEvent.runCommand("/buy"))),

        Component.text("Have a suggestion or bug report? Submit it at ", NamedTextColor.GRAY).append(Component
            .text("/forums", NamedTextColor.AQUA).clickEvent(ClickEvent.runCommand("/forums"))),

        Component.text("Report misbehaving players with ", NamedTextColor.RED).append(Component
            .text("/report", NamedTextColor.GRAY).clickEvent(ClickEvent.runCommand("/report")))
    };

    public void scheduleBroadcasts() {
        i.server.getScheduler().buildTask(
            i, () -> {
                if (lastMessage == messages.length) lastMessage = 0;
                for (Player players : i.server.getAllPlayers()) {
                    if (players.getCurrentServer().isEmpty()) continue;

                    String serverName = players.getCurrentServer().get().getServerInfo().getName();
                    if (serverName.equalsIgnoreCase("survival") || serverName.equalsIgnoreCase("events"))
                        continue;

                    players.sendMessage(messages[lastMessage]);
                }
                lastMessage++;
            }).delay(60, TimeUnit.MINUTES).repeat(60, TimeUnit.MINUTES).schedule();
    }
}
