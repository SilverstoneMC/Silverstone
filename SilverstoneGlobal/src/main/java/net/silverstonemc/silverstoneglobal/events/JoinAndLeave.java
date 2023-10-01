package net.silverstonemc.silverstoneglobal.events;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.MetadataValue;

public class JoinAndLeave implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.joinMessage(null);

        boolean isVanished = false;
        for (MetadataValue meta : event.getPlayer().getMetadata("vanished"))
            if (meta.asBoolean()) {
                isVanished = true;
                break;
            }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player == event.getPlayer()) continue;
            if (isVanished) if (!player.hasPermission("silverstone.moderator")) continue;

            if (player.hasPermission("silverstone.jlsounds.enabled"))
                player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, SoundCategory.PLAYERS, 1, 1.5f);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.quitMessage(null);

        boolean isVanished = false;
        for (MetadataValue meta : event.getPlayer().getMetadata("vanished"))
            if (meta.asBoolean()) {
                isVanished = true;
                break;
            }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player == event.getPlayer()) continue;
            if (isVanished) if (!player.hasPermission("silverstone.moderator")) continue;

            if (player.hasPermission("silverstone.jlsounds.enabled"))
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.PLAYERS,
                    1, 1.75f);
        }
    }
}
