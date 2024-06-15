package net.silverstonemc.silverstoneglobal.discord;

import me.rerere.matrix.api.HackType;
import me.rerere.matrix.api.events.PlayerViolationCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.silverstonemc.silverstoneglobal.SilverstoneGlobal;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AntiCheat implements Listener {
    @SuppressWarnings("DataFlowIssue")
    @EventHandler
    public void violation(PlayerViolationCommandEvent event) {
        Player player = event.getPlayer();
        if (SilverstoneGlobal.matrix.isBypass(player)) return;
        if (SilverstoneGlobal.matrix.getLatency(player) >= 750) return;
        if (SilverstoneGlobal.matrix.getTPS() <= 17) return;
        if (event.getHackType() == HackType.KILLAURA && SilverstoneGlobal.matrix.getViolations(player,
            HackType.KILLAURA) < 30) return;

        int x = 0;
        for (Player players : Bukkit.getOnlinePlayers())
            if (players.hasPermission("silverstone.moderator")) x++;

        TextChannel discord = SilverstoneGlobal.jda.getTextChannelById(1075643318265974864L);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(
            player.getName() + " may be hacking (" + event.getHackType() + ") | Violations: " + SilverstoneGlobal.matrix.getViolations(
                player,
                event.getHackType()) + " | Ping: " + SilverstoneGlobal.matrix.getLatency(player) + "ms | TPS: " + SilverstoneGlobal.matrix.getTPS(),
            null,
            "https://mc-heads.net/avatar/" + player.getUniqueId());
        embed.setColor(new Color(204, 27, 53));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("h:mm:ss.SSS a", Locale.US);
        LocalDateTime time = LocalDateTime.now();
        embed.setFooter(dtf.format(time) + " | " + x + " staff members online");
        discord.sendMessageEmbeds(embed.build()).queue();
    }
}
