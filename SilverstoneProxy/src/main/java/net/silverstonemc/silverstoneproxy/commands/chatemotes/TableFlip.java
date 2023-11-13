package net.silverstonemc.silverstoneproxy.commands.chatemotes;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

public class TableFlip implements SimpleCommand {
    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Sorry, but only players can do that.", NamedTextColor.RED));
            return;
        }

        player.sendMessage(Component.text("Click ", NamedTextColor.GREEN).append(
                Component.text("here", NamedTextColor.AQUA).clickEvent(ClickEvent.suggestCommand("╯°□°）╯︵ ┻━┻")))
            .append(Component.text(" to tableflip", NamedTextColor.AQUA)));
    }
}
