package net.silverstonemc.silverstonewarnings.commands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.silverstonemc.silverstonewarnings.SilverstoneWarnings;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ReasonsCommand extends Command {
    public ReasonsCommand() {
        super("reasons", "silverstone.moderator", "categories");
    }

    public void execute(CommandSender sender, String[] args) {
        sendReasonList(false, SilverstoneWarnings.getAdventure().sender(sender), null);
    }

    public void sendReasonList(boolean isWarning, Audience sender, @Nullable String username) {
        String header = "Available warning reasons:";
        if (isWarning) header = "Warn " + username + ":";

        Builder footer = Component.text();
        if (isWarning) footer.append(
            Component.text("\n\nClick to warn " + username).color(NamedTextColor.GRAY)
                .decorate(TextDecoration.ITALIC));

        Builder message = Component.text();
        message.append(Component.text(header).color(NamedTextColor.RED).decorate(TextDecoration.BOLD));

        ArrayList<String> reasonList = new ArrayList<>(
            SilverstoneWarnings.config.getSection("reasons").getKeys());
        reasonList.sort(String.CASE_INSENSITIVE_ORDER);

        // Courtesy of ChatGPT ^_^
        // Loop through the reasonList in increments of 3
        for (int i = 0; i < reasonList.size(); i += 3) {
            // Get the current reasons to display on this line, up to 3 reasons
            List<String> currentReasons = new ArrayList<>(reasonList.subList(i, Math.min(i + 3, reasonList.size())));

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
                message.append(Component.text(currentReason).color(NamedTextColor.GRAY)
                    .clickEvent(command != null ? ClickEvent.runCommand(command) : null)
                    .hoverEvent(createHoverEvent(currentReason, footer)));

                // If this is not the last reason, add a separator to the message
                if (j < currentReasons.size() - 1) message.append(
                    Component.text(" | ").color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.BOLD));
            }
        }

        sender.sendMessage(message);
    }

    private HoverEvent<?> createHoverEvent(String reason, Builder footer) {
        return HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text()
            .append(Component.text(reason + ":").color(NamedTextColor.RED).decorate(TextDecoration.BOLD))
            .append(Component.text(
                    "\n" + SilverstoneWarnings.config.getString("reasons." + reason + ".description"))
                .color(NamedTextColor.GRAY)).append(footer).build());
    }
}