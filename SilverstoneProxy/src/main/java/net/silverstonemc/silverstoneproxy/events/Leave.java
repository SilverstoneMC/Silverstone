package net.silverstonemc.silverstoneproxy.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.sound.Sound;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;
import net.silverstonemc.silverstoneproxy.ConfigurationManager;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("DataFlowIssue")
public class Leave implements Listener {
    public static final Map<UUID, Integer> leaves = new HashMap<>();
    public static final ArrayList<ScheduledTask> leaveTasks = new ArrayList<>();

    private final BungeeAudiences audience = SilverstoneProxy.getAdventure();
    private final SilverstoneProxy plugin = SilverstoneProxy.getPlugin();

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        boolean isVanished = player.hasPermission("silverstone.vanished");

        // Leave sounds
        for (ProxiedPlayer players : plugin.getProxy().getPlayers()) {
            if (players == player) continue;
            // If original player is vanished, only play sound to moderators
            if (isVanished) if (!players.hasPermission("silverstone.moderator")) continue;

            if (players.hasPermission("silverstone.jlsounds.enabled")) audience.player(players)
                .playSound(Sound.sound(Key.key("block.beacon.deactivate"), Sound.Source.PLAYER, 100, 1.75f));
        }

        if (Join.newPlayers.containsKey(player)) {
            int x = 0;
            for (ProxiedPlayer players : SilverstoneProxy.getPlugin().getProxy().getPlayers())
                if (players.hasPermission("silverstone.moderator")) x++;

            Message message = Join.newPlayers.get(player);
            MessageEmbed oldEmbed = message.getEmbeds().get(0);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor(player.getName() + " was new", null, oldEmbed.getAuthor().getIconUrl());
            embed.setImage(oldEmbed.getImage().getUrl());
            embed.setFooter("Player has left | " + x + " staff members online");
            embed.setColor(Color.RED);

            message.editMessageEmbeds(embed.build()).queue();

            Join.newPlayers.remove(player);
        }

        // Join/leave spam
        if (player.hasPermission("silverstone.joinleavespam.bypass")) return;
        if (event.getPlayer().getServer() == null) return;

        if (leaves.containsKey(player.getUniqueId()))
            if (leaves.get(player.getUniqueId()) >= ConfigurationManager.config.getInt(
                "join-leave-spam.leaves")) plugin.getProxy().getPluginManager()
                .dispatchCommand(plugin.getProxy().getConsole(),
                    "warn " + player.getName() + " " + ConfigurationManager.config.getString(
                        "join-leave-spam.warn"));
            else {
                leaves.put(player.getUniqueId(), leaves.get(player.getUniqueId()) + 1);
                removeLeave(player);
            }
        else {
            leaves.put(player.getUniqueId(), 1);
            removeLeave(player);
        }
    }

    private void removeLeave(ProxiedPlayer player) {
        Runnable task = () -> {
            leaves.put(player.getUniqueId(), leaves.get(player.getUniqueId()) - 1);
            if (leaves.get(player.getUniqueId()) <= 0) leaves.remove(player.getUniqueId());
        };

        leaveTasks.add(plugin.getProxy().getScheduler()
            .schedule(plugin, task, ConfigurationManager.config.getInt("join-leave-spam.expire-after"),
                TimeUnit.SECONDS));
    }
}
