package net.silverstonemc.silverstonewarnings;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.awt.*;

@SuppressWarnings("DataFlowIssue")
public class QuitEvent implements Listener {
    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (JoinEvent.newPlayers.containsKey(player)) {
            int x = 0;
            for (ProxiedPlayer players : SilverstoneWarnings.getPlugin().getProxy().getPlayers())
                if (players.hasPermission("silverstone.moderator")) x++;

            Message message = JoinEvent.newPlayers.get(player);
            MessageEmbed oldEmbed = message.getEmbeds().get(0);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor(player.getName() + " was new", null, oldEmbed.getAuthor().getIconUrl());
            embed.setImage(oldEmbed.getImage().getUrl());
            embed.setFooter("Player has left | " + x + " staff members online");
            embed.setColor(Color.RED);

            message.editMessageEmbeds(embed.build()).queue();

            JoinEvent.newPlayers.remove(player);
        }
    }
}
