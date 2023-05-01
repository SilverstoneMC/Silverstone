package net.silverstonemc.silverstonewarnings;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class JoinEvent implements Listener {
    public static final Map<ProxiedPlayer, Message> newPlayers = new HashMap<>();

    private final SilverstoneWarnings plugin = SilverstoneWarnings.getPlugin();

    @EventHandler
    public void onJoin(ServerConnectedEvent event) {
        if (event.getPlayer().getServer() != null) return;

        UUID uuid = event.getPlayer().getUniqueId();
        String username = event.getPlayer().getName();
        boolean userExists = UserManager.playerMap.containsKey(uuid);

        // Silent join message
        if (event.getPlayer().hasPermission("silverstone.vanished")) {
            int nonStaff = 0;
            for (ProxiedPlayer players : plugin.getProxy().getPlayers())
                if (!players.hasPermission("silverstone.moderator")) nonStaff++;
            if (nonStaff == 0) return;

            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor(username + " silently joined the server", null,
                "https://crafatar.com/avatars/" + uuid + "?overlay=true");
            embed.setColor(new Color(0x2b2d31));
            //noinspection DataFlowIssue
            SilverstoneWarnings.jda.getTextChannelById(1075643381734195210L).sendMessageEmbeds(embed.build())
                .queue();
        }

        // Update the username if it has changed
        if (userExists && !UserManager.playerMap.get(uuid).equals(username)) {
            // Notify everyone online if not vanished
            if (!event.getPlayer().hasPermission("silverstone.vanished")) {
                Runnable task = () -> {
                    for (ProxiedPlayer player : plugin.getProxy().getPlayers())
                        SilverstoneWarnings.getAdventure().player(player).sendMessage(
                            Component.text(username).color(NamedTextColor.AQUA).append(
                                    Component.text(" was previously known as ").color(NamedTextColor.GRAY))
                                .append(Component.text(new UserManager().getUsername(uuid))
                                    .color(NamedTextColor.AQUA)));
                };
                plugin.getProxy().getScheduler().schedule(plugin, task, 1, TimeUnit.SECONDS);
            }

            // Notify the Discord
            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor(username + " was previously known as: " + new UserManager().getUsername(uuid),
                null, "https://crafatar.com/avatars/" + uuid + "?overlay=true");
            embed.setColor(new Color(0x2b2d31));
            //noinspection DataFlowIssue
            SilverstoneWarnings.jda.getTextChannelById(1075680288841138257L).sendMessageEmbeds(embed.build())
                .queue();

            UserManager.playerMap.remove(uuid);
            new UserManager().addUser(uuid, username);
        }

        // Add the user if they don't exist and send a notification
        if (!userExists) {
            int staff = 0;
            for (ProxiedPlayer player : plugin.getProxy().getPlayers())
                if (player.hasPermission("silverstone.moderator")) staff++;
            int finalStaff = staff;

            new Thread(() -> {
                TextChannel discord = SilverstoneWarnings.jda.getTextChannelById(1075643222832984075L);

                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor(username + " is new", null,
                    "https://crafatar.com/avatars/" + uuid + "?overlay=true");
                embed.setImage("https://crafatar.com/renders/body/" + uuid + "?overlay=true");
                embed.setFooter(finalStaff + " staff members online");
                embed.setColor(new Color(36, 197, 19));

                //noinspection DataFlowIssue
                Message message = discord.sendMessageEmbeds(embed.build()).setActionRow(
                    Button.danger("warnskin:" + username, "Warn for inappropriate skin")
                        .withEmoji(Emoji.fromUnicode("⚠"))).complete();
                newPlayers.put(event.getPlayer(), message);
            }, "New Player Discord").start();

            new UserManager().addUser(uuid, username);
        }

        if (!ConfigurationManager.queue.getSection("queue").getKeys().contains(uuid.toString())) return;

        Runnable task = () -> {
            if (plugin.getProxy().getPlayer(uuid) != null) {
                new WarnPlayer().warn(uuid, ConfigurationManager.queue.getString("queue." + uuid));
                ConfigurationManager.queue.set("queue." + uuid, null);
                new ConfigurationManager().saveQueue();
            }
        };
        plugin.getProxy().getScheduler().schedule(plugin, task, 3, TimeUnit.SECONDS);
    }
}
