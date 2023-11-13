package net.silverstonemc.silverstoneproxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;

public class Restart implements SimpleCommand {
    public Restart(SilverstoneProxy instance) {
        i = instance;
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("silverstone.admin");
    }

    private final SilverstoneProxy i;

    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();

        for (Player player : i.server.getAllPlayers())
            player.sendMessage(Component.text().append(
                Component.text("WARNING ", NamedTextColor.RED, TextDecoration.BOLD)
                    .append(Component.text("> ", NamedTextColor.AQUA, TextDecoration.BOLD))
                    .append(Component.text("The network will restart soon!", NamedTextColor.GREEN))));

        if (!(sender instanceof Player))
            sender.sendMessage(Component.text("Restart broadcast sent!", NamedTextColor.GREEN));
    }
}
