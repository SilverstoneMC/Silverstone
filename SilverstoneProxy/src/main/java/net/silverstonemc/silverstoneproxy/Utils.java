package net.silverstonemc.silverstoneproxy;

import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.CommandSender;

public class Utils {
    private final BungeeAudiences audience = SilverstoneProxy.getAdventure();
    
    public void nonexistentPlayerMessage(String username, CommandSender sender) {
        audience.sender(sender).sendMessage(Component.text("Couldn't find player ").color(
            NamedTextColor.RED).append(Component.text(username).color(NamedTextColor.GRAY)).append(Component.text(" in the user cache!").color(NamedTextColor.RED)));
    }
}
