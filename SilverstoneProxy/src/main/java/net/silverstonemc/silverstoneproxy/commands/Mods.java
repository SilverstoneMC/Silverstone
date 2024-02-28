package net.silverstonemc.silverstoneproxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

public class Mods implements SimpleCommand {
    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();

        sender.sendMessage(Component
            .text("Click here for modifications we recommend", NamedTextColor.DARK_AQUA)
            .clickEvent(ClickEvent.openUrl(
                "https://gist.github.com/JasonHorkles/bcf3532a9fbda145fd0a0b172ec8fc2d")));
    }
}
