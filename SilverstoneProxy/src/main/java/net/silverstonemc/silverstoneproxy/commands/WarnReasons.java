package net.silverstonemc.silverstoneproxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;
import ninja.leaping.configurate.ConfigurationNode;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static net.silverstonemc.silverstoneproxy.ConfigurationManager.FileType.CONFIG;

public class WarnReasons implements SimpleCommand {
    public WarnReasons(SilverstoneProxy instance) {
        i = instance;
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("silverstone.moderator");
    }

    private final SilverstoneProxy i;

    @Override
    public void execute(final Invocation invocation) {
        sendReasonList(false, invocation.source(), null);
    }

    public void sendReasonList(boolean isWarning, CommandSource sender, @Nullable String username) {
        String header = "Available warning reasons:";
        if (isWarning) header = "Warn " + username + ":";

        Builder footer = Component.text();
        if (isWarning) footer.append(Component.text("\n\nClick to warn " + username,
            NamedTextColor.GRAY,
            TextDecoration.ITALIC));

        Builder message = Component.text();
        message.append(Component.text(header, NamedTextColor.RED, TextDecoration.BOLD));

        ArrayList<String> reasonList = new ArrayList<>();
        for (ConfigurationNode reasons : i.fileManager.files.get(CONFIG).getNode("reasons").getChildrenMap()
            .values())
            //noinspection DataFlowIssue
            reasonList.add(reasons.getKey().toString());
        reasonList.sort(String.CASE_INSENSITIVE_ORDER);

        // Loop through the reasonList in increments of 3
        for (int i = 0; i < reasonList.size(); i += 3) {
            // Get the current reasons to display on this line, up to 3 reasons
            List<String> currentReasons = new ArrayList<>(reasonList.subList(i,
                Math.min(i + 3, reasonList.size())));

            // Append a new line to the message
            message.append(Component.text("\n"));

            // Loop through the current reasons to add them to the message
            for (int j = 0; j < currentReasons.size(); j++) {
                // Get the current reason to add to the message
                String currentReason = currentReasons.get(j);

                // Create a command to use if this is a warning message
                String command = null;
                if (isWarning) command = "/warn " + username + " " + currentReason;

                // Add the current reason to the message
                message.append(Component.text(currentReason, NamedTextColor.GRAY)
                    .clickEvent(command != null ? ClickEvent.runCommand(command) : null)
                    .hoverEvent(createHoverEvent(currentReason, footer)));

                // If this is not the last reason, add a separator to the message
                if (j < currentReasons.size() - 1) message.append(Component.text(" | ",
                    NamedTextColor.DARK_GRAY,
                    TextDecoration.BOLD));
            }
        }

        sender.sendMessage(message);
    }

    private HoverEvent<?> createHoverEvent(String reason, Builder footer) {
        return HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text().append(Component.text(reason + ":",
                NamedTextColor.RED,
                TextDecoration.BOLD)).append(Component.text("\n" + i.fileManager.files.get(CONFIG)
                .getNode("reasons", reason, "description").getString(), NamedTextColor.GRAY)).append(footer)
            .build());
    }
}