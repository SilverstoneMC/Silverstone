package net.silverstonemc.silverstoneglobal.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.silverstonemc.silverstoneglobal.SilverstoneGlobal;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TPS {
    private static boolean messageSent = false;
    private static final ArrayList<Integer> tickList = new ArrayList<>();
    private static final JavaPlugin plugin = SilverstoneGlobal.getInstance();

    public static void checkTPS() {
        if (messageSent) return;

        long now = System.currentTimeMillis();
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks++;
                if (now + 1000 <= System.currentTimeMillis()) {
                    this.cancel();
                    averageTPS(ticks);
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 1);
    }

    private static void averageTPS(int tps) {
        tickList.add(tps);
        if (tickList.size() > 10) tickList.remove(0);
        else return;

        int sum = 0;
        double average;
        //bug ConcurrentModificationException, NPE, not rounded to nearest tenth
        for (int x : tickList) sum += x;
        average = (double) sum / tickList.size();
        sendMessage(average);
    }

    @SuppressWarnings("DataFlowIssue")
    private static void sendMessage(double tps) {
        TextChannel channel = SilverstoneGlobal.jda.getTextChannelById(1075643352814456892L);

        if (tps < 17 && tps > 12) {
            EmbedBuilder embed = new EmbedBuilder();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("h:mm:ss a");
            LocalDateTime time = LocalDateTime.now();
            embed.setTitle("⚠ TPS low! (" + tps + ") ⚠");
            embed.setDescription("Players: " + Bukkit.getOnlinePlayers().size());
            embed.setColor(new Color(245, 167, 0));
            embed.setFooter(dtf.format(time));

            channel.sendMessageEmbeds(embed.build()).queue();
            cooldown();

        } else if (tps <= 10) {
            EmbedBuilder embed = new EmbedBuilder();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("h:mm:ss a");
            LocalDateTime time = LocalDateTime.now();
            embed.setTitle("⚠⚠⚠ TPS very low! (" + tps + ") ⚠⚠⚠");
            embed.setDescription("Players: " + Bukkit.getOnlinePlayers().size());
            embed.setColor(new Color(204, 27, 53));
            embed.setFooter(dtf.format(time));

            channel.sendMessage(":warning: <@&667793980318154783>, the server is crashing! :warning:")
                .setEmbeds(embed.build()).queue();
            cooldown();
        }
    }

    private static void cooldown() {
        messageSent = true;
        tickList.clear();
        new BukkitRunnable() {
            @Override
            public void run() {
                messageSent = false;
            }
        }.runTaskLaterAsynchronously(plugin, 300);
    }
}
