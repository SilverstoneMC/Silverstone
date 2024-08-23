package net.silverstonemc.silverstoneproxy.events;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;
import net.silverstonemc.silverstoneproxy.utils.NicknameUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("DataFlowIssue")
public class Leave {
    public Leave(SilverstoneProxy instance) {
        i = instance;
    }

    public static final Map<String, UUID> recentlyQuit = new HashMap<>();

    private final SilverstoneProxy i;

    @Subscribe
    public void onQuit(DisconnectEvent event) {
        if (event.getLoginStatus() == DisconnectEvent.LoginStatus.CONFLICTING_LOGIN) return;

        Player player = event.getPlayer();
        boolean isVanished = player.hasPermission("silverstone.vanished");

        if (!isVanished) {
            // Maybe send message only if player's not vanished
            recentlyQuit.put(player.getUsername(), player.getUniqueId());
            // Then remove them from the cache after 5 seconds
            i.server.getScheduler().buildTask(i, () -> recentlyQuit.remove(player.getUsername())).delay(5,
                TimeUnit.SECONDS).schedule();

            TextComponent.Builder message = Component.text().append(Component.text("- ",
                    NamedTextColor.RED,
                    TextDecoration.BOLD)).append(new NicknameUtils(i).getDisplayName(player.getUniqueId()))
                .colorIfAbsent(NamedTextColor.AQUA);

            for (Player players : i.server.getAllPlayers()) players.sendMessage(message);

            i.server.getConsoleCommandSource().sendMessage(message);
        }

        // Vanish status handled on backend
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("leavesound");
        out.writeUTF(event.getPlayer().getUniqueId().toString());
        out.writeBoolean(isVanished);
        for (RegisteredServer servers : i.server.getAllServers())
            servers.sendPluginMessage(SilverstoneProxy.IDENTIFIER, out.toByteArray());

        if (Join.newPlayers.containsKey(player)) {
            int x = 0;
            for (Player players : i.server.getAllPlayers())
                if (players.hasPermission("silverstone.moderator")) x++;

            Message message = Join.newPlayers.get(player);
            MessageEmbed oldEmbed = message.getEmbeds().getFirst();

            EmbedBuilder embed = new EmbedBuilder(oldEmbed);
            embed.setAuthor(player.getUsername() + " was new", null, oldEmbed.getAuthor().getIconUrl());
            embed.setFooter("Player has left | " + x + " staff members online");
            embed.setColor(Color.RED);

            message.editMessageEmbeds(embed.build()).queue();

            Join.newPlayers.remove(player);
        }

        Chat.lastMessages.remove(player.getUniqueId());
    }
}
