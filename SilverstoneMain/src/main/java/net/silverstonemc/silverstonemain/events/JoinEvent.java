package net.silverstonemc.silverstonemain.events;

import github.scarsz.discordsrv.DiscordSRV;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.Button;
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

public final class JoinEvent implements Listener {
    public JoinEvent(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static final Map<Player, Message> newPlayers = new HashMap<>();

    private final JavaPlugin plugin;

    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            int x = 0;
            for (Player players : Bukkit.getOnlinePlayers())
                if (players.hasPermission("silverstone.moderator")) x++;
            int finalX = x;
            
            new BukkitRunnable() {
                @Override
                public void run() {
                    TextChannel discord = DiscordSRV.getPlugin()
                        .getDestinationTextChannelForGameChannelName("newplayers");

                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor(player.getName() + " is new", null,
                        "https://crafatar.com/avatars/" + player.getUniqueId() + "?overlay=true");
                    embed.setImage(
                        "https://crafatar.com/renders/body/" + player.getUniqueId() + "?overlay=true");
                    embed.setFooter(finalX + " staff members online");
                    embed.setColor(new Color(36, 197, 19));

                    Message message = discord.sendMessageEmbeds(embed.build()).setActionRow(
                        Button.danger("warnskin:" + player.getName(), "Warn for inappropriate skin")
                            .withEmoji(Emoji.fromUnicode("⚠"))).complete();
                    newPlayers.put(player, message);
                }
            }.runTaskAsynchronously(plugin);
        }

        for (MetadataValue meta : player.getMetadata("vanished"))
            if (meta.asBoolean()) {
                int nonStaff = 0;
                for (Player players : Bukkit.getOnlinePlayers())
                    if (!players.hasPermission("silverstone.moderator")) nonStaff++;
                if (nonStaff == 0) return;

                TextChannel discord = DiscordSRV.getPlugin()
                    .getDestinationTextChannelForGameChannelName("silentjoins");
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor(player.getName() + " silently joined the server", null,
                    "https://crafatar.com/avatars/" + player.getUniqueId() + "?overlay=true");
                embed.setColor(new Color(36, 197, 19));

                discord.sendMessageEmbeds(embed.build()).queue();
                break;
            }
    }
}