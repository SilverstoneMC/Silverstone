package me.jasonhorkles.silverstonemain;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;

public record QuitEvent(JavaPlugin plugin) implements Listener {

    @SuppressWarnings("ConstantConditions")
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (JoinEvent.newPlayers.containsKey(event.getPlayer())) {
            int x = 0;
            for (Player players : Bukkit.getOnlinePlayers()) if (players.hasPermission("silverstone.trialmod")) x++;

            Message message = JoinEvent.newPlayers.get(event.getPlayer());
            MessageEmbed oldEmbed = message.getEmbeds().get(0);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor(event.getPlayer().getName() + " was new this season", null, oldEmbed.getAuthor().getIconUrl());
            embed.setImage(oldEmbed.getImage().getUrl());
            embed.setFooter("Player has left | " + x + " staff members online");
            embed.setColor(Color.RED);

            message.editMessageEmbeds(embed.build()).queue();

            JoinEvent.newPlayers.remove(event.getPlayer());
        }
    }
}
