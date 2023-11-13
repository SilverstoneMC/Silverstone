package net.silverstonemc.silverstoneproxy.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;

import java.awt.*;

@SuppressWarnings("DataFlowIssue")
public class Leave {
    public Leave(SilverstoneProxy instance) {
        i = instance;
    }

    private final SilverstoneProxy i;

    @Subscribe
    public void onQuit(DisconnectEvent event) {
        Player player = event.getPlayer();

        if (Join.newPlayers.containsKey(player)) {
            int x = 0;
            for (Player players : i.server.getAllPlayers())
                if (players.hasPermission("silverstone.moderator")) x++;

            Message message = Join.newPlayers.get(player);
            MessageEmbed oldEmbed = message.getEmbeds().get(0);

            EmbedBuilder embed = new EmbedBuilder(oldEmbed);
            embed.setAuthor(player.getUsername() + " was new", null, oldEmbed.getAuthor().getIconUrl());
            embed.setFooter("Player has left | " + x + " staff members online");
            embed.setColor(Color.RED);

            message.editMessageEmbeds(embed.build()).queue();

            Join.newPlayers.remove(player);
        }
    }
}
