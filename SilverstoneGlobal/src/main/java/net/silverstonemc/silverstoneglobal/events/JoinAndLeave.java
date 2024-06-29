package net.silverstonemc.silverstoneglobal.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.silverstonemc.silverstoneglobal.SilverstoneGlobal;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JoinAndLeave implements Listener {
    public JoinAndLeave(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;
    private static final Map<Player, Message> newPlayers = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.joinMessage(null);

        // #serverSpecific
        //noinspection DataFlowIssue
        if (plugin.getConfig().getString("server").equalsIgnoreCase("creative") && !event.getPlayer()
            .hasPlayedBefore()) {
            int staff = 0;
            for (Player players : plugin.getServer().getOnlinePlayers())
                if (players.hasPermission("silverstone.moderator")) staff++;
            int finalStaff = staff;

            Player player = event.getPlayer();
            String username = player.getName();
            UUID uuid = player.getUniqueId();

            new Thread(() -> {
                TextChannel channel = SilverstoneGlobal.jda.getTextChannelById(1160724667817017414L);

                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor(username + " is new", null, "https://mc-heads.net/avatar/" + uuid);
                embed.setFooter(finalStaff + " staff members online");
                embed.setColor(new Color(36, 197, 19));

                //noinspection DataFlowIssue
                Message message = channel.sendMessageEmbeds(embed.build()).complete();
                newPlayers.put(player, message);
            }, "New Player Discord").start();
        }

        // #serverSpecific
        // Add survival gang role to new survival players
        //noinspection DataFlowIssue
        if (plugin.getConfig().getString("server").equalsIgnoreCase("survival") && !event.getPlayer()
            .hasPlayedBefore()) Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(),
            "lp user " + event.getPlayer().getUniqueId() + " parent add survivalgang");
    }

    @SuppressWarnings("DataFlowIssue")
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.quitMessage(null);

        // #serverSpecific
        if (plugin.getConfig().getString("server").equalsIgnoreCase("creative")) if (newPlayers.containsKey(
            event.getPlayer())) {
            Player player = event.getPlayer();

            int x = 0;
            for (Player players : plugin.getServer().getOnlinePlayers())
                if (players.hasPermission("silverstone.moderator")) x++;

            Message message = newPlayers.get(player);
            MessageEmbed oldEmbed = message.getEmbeds().getFirst();

            EmbedBuilder embed = new EmbedBuilder(oldEmbed);
            embed.setAuthor(player.getName() + " was new", null, oldEmbed.getAuthor().getIconUrl());
            embed.setFooter("Player has left | " + x + " staff members online");
            embed.setColor(Color.RED);

            message.editMessageEmbeds(embed.build()).queue();

            newPlayers.remove(player);
        }
    }
}
