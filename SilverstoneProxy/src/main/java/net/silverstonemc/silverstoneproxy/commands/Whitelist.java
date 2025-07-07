package net.silverstonemc.silverstoneproxy.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;
import org.spongepowered.configurate.serialize.SerializationException;

import static net.silverstonemc.silverstoneproxy.ConfigurationManager.FileType.WHITELIST;

public class Whitelist implements SimpleCommand {
    public Whitelist(SilverstoneProxy instance) {
        i = instance;
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("silverstone.admin");
    }

    private final SilverstoneProxy i;

    @Override
    public void execute(final Invocation invocation) {
        boolean whitelistEnabled = i.fileManager.files.get(WHITELIST).node("enabled").getBoolean();

        try {
            if (whitelistEnabled) {
                i.fileManager.files.get(WHITELIST).node("enabled").set(false);
                i.server.getCommandManager().executeAsync(i.server.getConsoleCommandSource(), "mmotd enable");
                invocation.source().sendMessage(Component.text(
                    "The proxy whitelist has been disabled.",
                    NamedTextColor.GREEN));

            } else {
                i.fileManager.files.get(WHITELIST).node("enabled").set(true);
                i.server.getCommandManager().executeAsync(
                    i.server.getConsoleCommandSource(),
                    "mmotd disable");

                // Kick all players without silverstone.admin permission
                for (Player player : i.server.getAllPlayers())
                    if (!player.hasPermission("silverstone.admin")) player.disconnect(Component
                        .text("The server is now closed for maintenance!", NamedTextColor.RED)
                        .append(Component.text(
                            "\n\nSee status.silverstonemc.net for more details.",
                            NamedTextColor.GRAY)));

                invocation.source().sendMessage(Component.text(
                    "The proxy whitelist has been enabled.",
                    NamedTextColor.GOLD));
            }
            i.fileManager.save(WHITELIST);

        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }
}