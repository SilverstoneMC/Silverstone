package me.jasonhorkles.silverstonemain;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import me.rerere.matrix.api.HackType;
import me.rerere.matrix.api.MatrixAPI;
import me.rerere.matrix.api.MatrixAPIProvider;
import me.rerere.matrix.api.events.PlayerViolationCommandEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AntiCheatDiscord implements Listener {

    private final MatrixAPI matrix = MatrixAPIProvider.getAPI();

    @EventHandler
    public void violation(PlayerViolationCommandEvent event) {
        Player player = event.getPlayer();
        if (matrix.isBypass(player)) return;
        if (matrix.getLatency(player) >= 750) return;
        if (matrix.getTPS() <= 17) return;
        if (event.getHackType().equals(HackType.KILLAURA) && matrix.getViolations(player, HackType.KILLAURA) < 30)
            return;

        int x = 0;
        for (Player players : Bukkit.getOnlinePlayers()) if (players.hasPermission("silverstone.trialmod")) x++;

        TextChannel discord = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("matrix");

        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(player.getName() + " may be hacking (" + event.getHackType() + ") | Violations: " + matrix.getViolations(player, event
                .getHackType()) + " | Ping: " + matrix.getLatency(player) + "ms | TPS: " + matrix.getTPS(), null, "https://crafatar.com/avatars/" + player
                .getUniqueId() + "?overlay=true");
        embed.setColor(new Color(204, 27, 53));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("h:mm:ss.SSS a");
        LocalDateTime time = LocalDateTime.now();
        embed.setFooter(dtf.format(time) + " | " + x + " staff members online");
        discord.sendMessageEmbeds(embed.build()).queue();
    }
}
