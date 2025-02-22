package net.silverstonemc.silverstoneglobal.events;

import com.earth2me.essentials.User;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.ess3.api.IEssentials;
import net.ess3.api.events.AfkStatusChangeEvent;
import net.silverstonemc.silverstoneglobal.SilverstoneGlobal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class AFK implements Listener {
    public AFK(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;
    private final IEssentials essentials = SilverstoneGlobal.getInstance().getEssentials();

    @EventHandler(ignoreCancelled = true)
    public void afkStatusChange(AfkStatusChangeEvent event) {
        // Run the task later to ensure accurate AFK status
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = event.getAffected().getBase();
                User essUser = essentials.getUser(player);
                ByteArrayDataOutput out = ByteStreams.newDataOutput();

                if (essUser.isAfk()) {
                    out.writeUTF("afk-on");
                    String afkMessage = essUser.getAfkMessage();
                    if (afkMessage == null) afkMessage = "";
                    out.writeUTF(afkMessage);
                } else out.writeUTF("afk-off");

                player.sendPluginMessage(plugin, "silverstone:pluginmsg", out.toByteArray());
            }
        }.runTaskLaterAsynchronously(plugin, 1L);
    }
}
