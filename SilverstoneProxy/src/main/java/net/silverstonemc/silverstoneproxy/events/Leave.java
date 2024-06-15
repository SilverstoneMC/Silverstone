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
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;
import net.silverstonemc.silverstoneproxy.utils.NicknameUtils;

import java.awt.*;

import static com.velocitypowered.api.event.connection.DisconnectEvent.LoginStatus.CONFLICTING_LOGIN;
import static com.velocitypowered.api.event.connection.DisconnectEvent.LoginStatus.SUCCESSFUL_LOGIN;

@SuppressWarnings("DataFlowIssue")
public class Leave {
    public Leave(SilverstoneProxy instance) {
        i = instance;
    }

    private final SilverstoneProxy i;

    @Subscribe
    public void onQuit(DisconnectEvent event) {
        if (event.getLoginStatus() != SUCCESSFUL_LOGIN && event.getLoginStatus() != CONFLICTING_LOGIN) return;

        Player player = event.getPlayer();
        boolean isVanished = player.hasPermission("silverstone.vanished");

        if (!isVanished) {
            for (Player players : i.server.getAllPlayers())
                players.sendMessage(Component.text().append(Component.text("- ",
                        NamedTextColor.RED,
                        TextDecoration.BOLD)).append(new NicknameUtils(i).getDisplayName(player.getUniqueId()))
                    .colorIfAbsent(NamedTextColor.AQUA));

            i.server.getConsoleCommandSource().sendMessage(Component.text().append(Component.text("- ",
                    NamedTextColor.RED,
                    TextDecoration.BOLD)).append(new NicknameUtils(i).getDisplayName(player.getUniqueId()))
                .colorIfAbsent(NamedTextColor.AQUA));
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
    }
}
