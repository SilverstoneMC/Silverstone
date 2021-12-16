package net.silverstonemc.silverstoneproxy;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

public class JoinEvent implements Listener {

    private final Plugin plugin = SilverstoneProxy.getInstance();

    @EventHandler
    public void onJoin(ServerConnectEvent event) {
        if (event.getPlayer().getServer() != null) return;
        int version = event.getPlayer().getPendingConnection().getVersion();
        plugin.getLogger().info(event.getPlayer().getName() + " is joining with protocol version " + version);
        if (version < 757) {
            Runnable task = () -> {
                if (event.getPlayer().getServer().isConnected())
                    event.getPlayer()
                            .sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + "The server is currently built using Minecraft 1.18.1 - please update your client to use all the features."));
            };
            plugin.getProxy().getScheduler().schedule(plugin, task, 2, TimeUnit.SECONDS);
        }
    }
}