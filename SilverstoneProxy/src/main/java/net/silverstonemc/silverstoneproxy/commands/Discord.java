package net.silverstonemc.silverstoneproxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

public class Discord implements SimpleCommand {
    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();

        sender.sendMessage(Component.text("Join the Discord server at ", NamedTextColor.DARK_AQUA).append(
            Component.text("discord.gg/VVSUEPd", NamedTextColor.GREEN)
                .clickEvent(ClickEvent.openUrl("https://discord.gg/VVSUEPd"))));
    }
}
