package net.silverstonemc.silverstoneglobal;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.MetadataValue;

public class TimeOut implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerQuitEvent.QuitReason reason = event.getReason();

        if (reason == PlayerQuitEvent.QuitReason.TIMED_OUT) {
            for (MetadataValue meta : player.getMetadata("vanished")) if (meta.asBoolean()) return;

            for (Player online : Bukkit.getOnlinePlayers())
                online.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + player.getName() + " &ctimed out."));

            Bukkit.getLogger().info(ChatColor.GOLD + "Sending timeout message (quit)");
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        Player player = event.getPlayer();

        if (PlainTextComponentSerializer.plainText().serialize(event.reason()).equalsIgnoreCase("disconnect.timeout")) {
            for (MetadataValue meta : player.getMetadata("vanished")) if (meta.asBoolean()) return;

            for (Player online : Bukkit.getOnlinePlayers())
                online.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + player.getName() + " &ctimed out."));

            Bukkit.getLogger().info(ChatColor.GOLD + "Sending timeout message (kick)");
        }
    }
}
