package net.silverstonemc.silverstoneproxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

public class Forums implements SimpleCommand {
    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();

        sender.sendMessage(Component.text("Visit the forums at ", NamedTextColor.GREEN)
            .append(Component.text("silverstonemc.net", NamedTextColor.AQUA)
                .clickEvent(ClickEvent.openUrl("https://silverstonemc.net"))));
    }
}
