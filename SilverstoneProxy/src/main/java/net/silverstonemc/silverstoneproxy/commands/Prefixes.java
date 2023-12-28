package net.silverstonemc.silverstoneproxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.NodeType;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Prefixes implements SimpleCommand {
    public Prefixes(SilverstoneProxy instance) {
        luckPerms = instance.getLuckPerms();
    }

    private final LuckPerms luckPerms;

    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();

        ComponentBuilder<TextComponent, TextComponent.Builder> prefixList = Component.text();
        prefixList.append(Component.text("\nPrefixes\n", NamedTextColor.AQUA, TextDecoration.BOLD));
        prefixList.append(
            Component.text("             ", NamedTextColor.DARK_AQUA, TextDecoration.STRIKETHROUGH));

        ArrayList<Group> sortedGroups = new ArrayList<>(luckPerms.getGroupManager().getLoadedGroups().stream()
            .sorted(Comparator.comparingInt(group -> group.getWeight().orElse(0))).toList());
        Collections.reverse(sortedGroups);

        for (Group group : sortedGroups) {
            if (group.getNodes(NodeType.PREFIX).isEmpty()) continue;
            String rawPrefix = group.getCachedData().getMetaData().getPrefix();
            if (rawPrefix == null) return;

            TextComponent prefix = LegacyComponentSerializer.legacy('&').deserialize(rawPrefix);
            prefixList.append(Component.text("\n")).append(prefix)
                .append(Component.text("- ", NamedTextColor.DARK_AQUA))
                .append(Component.text(group.getName(), NamedTextColor.DARK_GREEN));
        }

        sender.sendMessage(prefixList.build());
    }
}
