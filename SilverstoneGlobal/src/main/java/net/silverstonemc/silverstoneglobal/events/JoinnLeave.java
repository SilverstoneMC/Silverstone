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

public class JoinnLeave implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.joinMessage(null);

        for (MetadataValue meta : event.getPlayer().getMetadata("vanished")) if (meta.asBoolean()) return;

        joinSound(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.quitMessage(null);

        for (MetadataValue meta : event.getPlayer().getMetadata("vanished")) if (meta.asBoolean()) return;

        quitSound(event.getPlayer());
    }

    public void joinSound(Player player) {
        for (Player players : Bukkit.getOnlinePlayers()) {
            if (players == player) continue;

            if (players.hasPermission("silverstone.jlsounds.enabled"))
                players.playSound(players.getLocation(), Sound.BLOCK_BELL_USE, SoundCategory.PLAYERS, 1,
                    1.5f);
        }
    }

    public void quitSound(Player player) {
        for (Player players : Bukkit.getOnlinePlayers()) {
            if (players == player) continue;

            if (players.hasPermission("silverstone.jlsounds.enabled"))
                players.playSound(players.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.PLAYERS,
                    1, 1.75f);
        }
    }
}
