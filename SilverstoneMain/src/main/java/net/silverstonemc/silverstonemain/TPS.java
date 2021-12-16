package net.silverstonemc.silverstonemain;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TPS {

    private static final JavaPlugin plugin = SilverstoneMain.getInstance();

    private static boolean messageSent = false;
    private static final ArrayList<Integer> tickList = new ArrayList<>();

    public static void checkTPS() {
        if (messageSent) return;

        long now = System.currentTimeMillis();
        BukkitRunnable task = new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks++;
                if (now + 1000 <= System.currentTimeMillis()) {
                    this.cancel();
                    averageTPS(ticks);
                }
            }
        };
        task.runTaskTimerAsynchronously(plugin, 0, 1);
    }

    private static void averageTPS(int tps) {
        tickList.add(tps);
        if (tickList.size() > 10) tickList.remove(0);
        else return;

        int sum = 0;
        double average;
        for (int x : tickList) sum += x;
        average = (double) sum / tickList.size();
        sendMessage(average);
    }

    private static void sendMessage(double tps) {
        if (tps < 17 && tps > 12) {
            TextChannel discord = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("tps");
            EmbedBuilder embed = new EmbedBuilder();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("h:mm:ss a");
            LocalDateTime time = LocalDateTime.now();
            embed.setTitle("⚠ TPS low! (" + tps + ") ⚠");
            embed.setDescription("Players: " + Bukkit.getOnlinePlayers().size());
            embed.setColor(new Color(245, 167, 0));
            embed.setFooter(dtf.format(time));
            discord.sendMessageEmbeds(embed.build()).queue();
            cooldown();
        } else if (tps <= 12) {
            TextChannel discord = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("tps");
            EmbedBuilder embed = new EmbedBuilder();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("h:mm:ss a");
            LocalDateTime time = LocalDateTime.now();
            embed.setTitle("⚠⚠⚠ TPS very low! (" + tps + ") ⚠⚠⚠");
            embed.setDescription("Players: " + Bukkit.getOnlinePlayers().size());
            embed.setColor(new Color(204, 27, 53));
            embed.setFooter(dtf.format(time));
            discord.sendMessageEmbeds(embed.build()).queue();
            cooldown();
        }
    }

    private static void cooldown() {
        messageSent = true;
        tickList.clear();
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                messageSent = false;
            }
        };
        task.runTaskLaterAsynchronously(plugin, 300);
    }
}