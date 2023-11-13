package net.silverstonemc.silverstoneproxy.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;

import java.util.concurrent.TimeUnit;

public class RestartWhenEmpty implements SimpleCommand {
    public RestartWhenEmpty(SilverstoneProxy instance) {
        i = instance;
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("silverstone.admin");
    }

    private final SilverstoneProxy i;

    @Override
    public void execute(final Invocation invocation) {
        for (Player player : i.server.getAllPlayers())
            player.sendActionBar(Component.text("NETWORK SCHEDULED TO RESTART WHEN EMPTY", NamedTextColor.RED,
                TextDecoration.BOLD));
        i.logger.info("Server scheduled to restart when empty.");

        i.server.getScheduler().buildTask(i, () -> {
            if (i.server.getAllPlayers().isEmpty()) i.server.shutdown();
        }).repeat(15, TimeUnit.SECONDS).schedule();
    }
}
