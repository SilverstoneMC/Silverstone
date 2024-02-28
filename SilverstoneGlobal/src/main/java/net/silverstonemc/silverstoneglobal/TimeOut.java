package net.silverstonemc.silverstoneglobal;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
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

        if (reason == PlayerQuitEvent.QuitReason.TIMED_OUT) sendTimeoutMessage(player);
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        Player player = event.getPlayer();

        if (PlainTextComponentSerializer.plainText().serialize(event.reason()).equalsIgnoreCase(
            "disconnect.timeout")) sendTimeoutMessage(player);
    }

    private void sendTimeoutMessage(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) if (meta.asBoolean()) return;

        for (Player online : Bukkit.getOnlinePlayers())
            online.sendMessage(Component.text(player.getName(), NamedTextColor.GRAY)
                .append(Component.text(" timed out.", NamedTextColor.RED)));

        Bukkit.getConsoleSender().sendMessage(Component.text(
            "Sending timeout message (kick)",
            NamedTextColor.GOLD));
    }
}
