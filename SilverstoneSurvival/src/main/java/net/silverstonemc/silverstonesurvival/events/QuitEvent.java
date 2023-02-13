package net.silverstonemc.silverstonesurvival.events;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.awt.*;

@SuppressWarnings("DataFlowIssue")
public class QuitEvent implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (JoinEvent.newPlayers.containsKey(player)) {
            int x = 0;
            for (Player players : Bukkit.getOnlinePlayers()) if (players.hasPermission("silverstone.trialmod")) x++;

            Message message = JoinEvent.newPlayers.get(player);
            MessageEmbed oldEmbed = message.getEmbeds().get(0);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor(player.getName() + " was new this season", null, oldEmbed.getAuthor().getIconUrl());
            embed.setImage(oldEmbed.getImage().getUrl());
            embed.setFooter("Player has left | " + x + " staff members online");
            embed.setColor(Color.RED);

            message.editMessageEmbeds(embed.build()).queue();

            JoinEvent.newPlayers.remove(player);
        }
    }
}
