package net.silverstonemc.silverstoneproxy.events;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.sound.Sound;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;

public class Chat implements Listener {
    private final BungeeAudiences audience = SilverstoneProxy.getAdventure();
    private final SilverstoneProxy plugin = SilverstoneProxy.getPlugin();

    @EventHandler
    public void onChat(ChatEvent event) {
        if (event.isCommand()) return;
        if (event.isCancelled()) return;

        for (ProxiedPlayer player : plugin.getProxy().getPlayers())
            if (player.hasPermission("silverstone.chatsounds.enabled")) if (player != event.getReceiver())
                audience.player(player).playSound(
                    Sound.sound(Key.key("entity.experience.orb.pickup"), Sound.Source.PLAYER, 0.5f, 1));
    }
}
