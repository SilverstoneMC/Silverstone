package net.silverstonemc.silverstonemain.events;

import github.scarsz.discordsrv.DiscordSRV;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.MetadataValue;

import java.awt.*;

public final class JoinEvent implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
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