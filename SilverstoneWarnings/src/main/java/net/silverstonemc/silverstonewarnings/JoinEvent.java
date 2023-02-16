package net.silverstonemc.silverstonewarnings;

import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class JoinEvent implements Listener {
    private final SilverstoneWarnings plugin = SilverstoneWarnings.getPlugin();

    @EventHandler
    public void onJoin(ServerConnectedEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        String username = event.getPlayer().getName();

        if (plugin.getPlayerUUID(username) != null) return;

        SilverstoneWarnings.userCache.set("uuids." + uuid.toString(), username);
        SilverstoneWarnings.userCache.set("usernames." + username.toLowerCase(), uuid.toString());
        plugin.saveUserCache();

        if (!SilverstoneWarnings.queue.contains("queue." + uuid)) return;

        Runnable task = () -> {
            if (plugin.getOnlinePlayer(uuid) != null) {
                new WarnPlayer().warn(uuid, SilverstoneWarnings.queue.getString("queue." + uuid));
                SilverstoneWarnings.queue.set("queue." + uuid, null);
                SilverstoneWarnings.getPlugin().saveQueue();
            }
        };
        plugin.getProxy().getScheduler().schedule(plugin, task, 3, TimeUnit.SECONDS);
    }
}
