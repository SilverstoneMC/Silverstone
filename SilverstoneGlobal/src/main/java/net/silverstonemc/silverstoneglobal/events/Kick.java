package net.silverstonemc.silverstoneglobal.events;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.silverstonemc.silverstoneglobal.SilverstoneGlobal;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

public class Kick implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onKick(PlayerKickEvent event) {
        event.setCancelled(true);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("kick");
        out.writeUTF(GsonComponentSerializer.gson().serialize(event.reason()));
        event.getPlayer().sendPluginMessage(
            SilverstoneGlobal.getInstance(),
            "silverstone:pluginmsg",
            out.toByteArray());
    }
}
