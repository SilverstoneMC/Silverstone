package net.silverstonemc.silverstoneglobal.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.silverstonemc.silverstoneglobal.SilverstoneGlobal;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TPS {
    private static boolean messageSent;
    private static final JavaPlugin plugin = SilverstoneGlobal.getInstance();

    public void checkTPS() {
        if (messageSent) return;

        double tps = Double.parseDouble(new DecimalFormat("0.0").format(Bukkit.getServer().getTPS()[0]));
        sendMessage(tps);
    }

    @SuppressWarnings("DataFlowIssue")
    private static void sendMessage(double tps) {
        if (tps > 18) return;

        TextChannel channel = SilverstoneGlobal.jda.getTextChannelById(1075643352814456892L);

        // Get the amount of players on the server, or the player's name if there is only one online
        int size = Bukkit.getOnlinePlayers().size();
        String players = size == 1 ? Bukkit.getOnlinePlayers().iterator().next().getName() : String.valueOf(
            size);

        if (tps < 18 && tps > 10) {
            EmbedBuilder embed = new EmbedBuilder();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("h:mm:ss a", Locale.US);
            LocalDateTime time = LocalDateTime.now();
            embed.setTitle("⚠ TPS low! (" + tps + ") ⚠");
            embed.setDescription("Players: " + players);
            embed.setColor(new Color(245, 167, 0));
            embed.setFooter(dtf.format(time));

            channel.sendMessageEmbeds(embed.build()).queue();
            cooldown();

        } else if (tps <= 10) {
            EmbedBuilder embed = new EmbedBuilder();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("h:mm:ss a", Locale.US);
            LocalDateTime time = LocalDateTime.now();
            embed.setTitle("⚠⚠⚠ TPS very low! (" + tps + ") ⚠⚠⚠");
            embed.setDescription("Players: " + players);
            embed.setColor(new Color(204, 27, 53));
            embed.setFooter(dtf.format(time));

            channel.sendMessage(":warning: <@&667793980318154783>, the server is crashing! :warning:")
                .setEmbeds(embed.build()).queue();
            cooldown();
        }
    }

    private static void cooldown() {
        messageSent = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                messageSent = false;
            }
        }.runTaskLaterAsynchronously(plugin, 600);
    }
}
