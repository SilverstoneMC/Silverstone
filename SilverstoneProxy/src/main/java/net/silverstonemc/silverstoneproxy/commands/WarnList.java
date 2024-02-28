package net.silverstonemc.silverstoneproxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;
import net.silverstonemc.silverstoneproxy.UserManager;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.UUID;

import static net.silverstonemc.silverstoneproxy.ConfigurationManager.FileType.WARNDATA;

public class WarnList implements SimpleCommand {
    public WarnList(SilverstoneProxy instance) {
        i = instance;
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("silverstone.moderator");
    }

    private final SilverstoneProxy i;

    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();
        sender.sendMessage(Component.text("All warnings:", NamedTextColor.RED, TextDecoration.BOLD));

        for (ConfigurationNode uuid : i.fileManager.files.get(WARNDATA).getNode("data").getChildrenMap()
            .values())
            for (ConfigurationNode warning : uuid.getChildrenMap().values())
                //noinspection DataFlowIssue
                sender.sendMessage(Component.text(new UserManager(i).getUsername(UUID.fromString(uuid.getKey()
                    .toString())) + " - " + warning + " - " + warning.getInt(), NamedTextColor.GRAY));
    }
}
