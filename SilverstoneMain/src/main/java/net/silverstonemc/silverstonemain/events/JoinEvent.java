package net.silverstonemc.silverstonemain.events;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Emoji;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public record JoinEvent(JavaPlugin plugin) implements Listener {

    public static final Map<Player, Message> newPlayers = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            int x = 0;
            for (Player players : Bukkit.getOnlinePlayers()) if (players.hasPermission("silverstone.trialmod")) x++;
            int finalX = x;
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    TextChannel discord = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("newplayers");

                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor(player.getName() + " is new this season", null, "https://crafatar.com/avatars/" + player
                            .getUniqueId() + "?overlay=true");
                    embed.setImage("https://crafatar.com/renders/body/" + player.getUniqueId() + "?overlay=true");
                    embed.setFooter(finalX + " staff members online");
                    embed.setColor(new Color(36, 197, 19));

                    Message message = discord.sendMessageEmbeds(embed.build())
                            .setActionRow(Button.danger("warnskin:" + player.getName(), "Warn for inappropriate skin")
                                    .withEmoji(Emoji.fromUnicode("⚠")))
                            .complete();
                    newPlayers.put(player, message);
                }
            };
            task.runTaskAsynchronously(plugin);
        }

        for (MetadataValue meta : player.getMetadata("vanished"))
            if (meta.asBoolean()) {
                int nonStaff = 0;
                for (Player players : Bukkit.getOnlinePlayers())
                    if (!players.hasPermission("silverstone.trialmod")) nonStaff++;
                if (nonStaff == 0) return;

                TextChannel discord = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("silentjoins");
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor(player.getName() + " silently joined the server", null, "https://crafatar.com/avatars/" + player
                        .getUniqueId() + "?overlay=true");
                embed.setColor(new Color(36, 197, 19));

                discord.sendMessageEmbeds(embed.build()).queue();
                break;
            }
    }
}