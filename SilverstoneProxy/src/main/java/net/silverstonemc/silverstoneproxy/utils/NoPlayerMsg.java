package net.silverstonemc.silverstoneproxy.utils;

import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class NoPlayerMsg {
    public void nonExistentPlayerMessage(String username, CommandSource sender) {
        sender.sendMessage(Component.text("Couldn't find player ", NamedTextColor.RED)
            .append(Component.text(username, NamedTextColor.GRAY))
            .append(Component.text(" in the user cache!", NamedTextColor.RED)));
    }
}
