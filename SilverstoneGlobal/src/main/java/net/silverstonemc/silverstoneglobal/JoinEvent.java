package net.silverstonemc.silverstoneglobal;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings({"unchecked", "rawtypes"})
public record JoinEvent(JavaPlugin plugin) implements Listener {

    // Not much else I can do besides use a deprecated method
    @SuppressWarnings("deprecation")
    private static final int serverVersion = Bukkit.getUnsafe().getProtocolVersion();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                Player player = event.getPlayer();
                if (!player.isOnline()) return;

                if (plugin.getServer().getPluginManager().getPlugin("ViaVersion") != null) {
                    ViaAPI via = Via.getAPI();
                    int clientVersion = via.getPlayerVersion(player);
                    if (clientVersion < serverVersion) if (player.hasPermission("vive.vivegroup"))
                        player.sendMessage(Component.text("Outdated VR client detected! Please update to the latest version to prevent false positives with the anti cheat!"));
                }
            }
        };
        task.runTaskLater(plugin, 300);
    }
}
