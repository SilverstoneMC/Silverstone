package net.silverstonemc.silverstonewarnings.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.silverstonemc.silverstonewarnings.SilverstoneWarnings;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class ReasonsCommand extends Command {
    public ReasonsCommand() {
        super("reasons", "silverstone.moderator", "categories");
    }

    public void execute(CommandSender sender, String[] args) {
        sendReasonList(false, sender, null);
    }

    //todo update deprecated methods
    public void sendReasonList(boolean isWarning, CommandSender sender, @Nullable String username) {
        String header = "Available warning reasons:";
        if (isWarning) header = "Warn " + username + ":";

        String footer = "";
        if (isWarning) footer = "\n\n&r&7&oClick to warn " + username;

        ComponentBuilder message = new ComponentBuilder(header).color(ChatColor.RED).bold(true);

        ArrayList<String> reasonList = new ArrayList<>(
            SilverstoneWarnings.config.getSection("reasons").getKeys());
        reasonList.sort(String.CASE_INSENSITIVE_ORDER);
        for (int x = 0; x < reasonList.size(); x = x + 3) {
            message.append("\n");

            try {
                String reason1 = reasonList.get(x);
                String reason2 = reasonList.get(x + 1);
                String reason3 = reasonList.get(x + 2);

                String command1 = "/zblankcmd";
                String command2 = "/zblankcmd";
                String command3 = "/zblankcmd";
                if (isWarning) {
                    command1 = "/warn " + username + " " + reason1;
                    command2 = "/warn " + username + " " + reason2;
                    command3 = "/warn " + username + " " + reason3;
                }

                message.append(reason1).reset().color(ChatColor.GRAY)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command1)).event(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                            ChatColor.translateAlternateColorCodes('&',
                                "&c&l" + reason1 + ":\n&7" + SilverstoneWarnings.config.getString(
                                    "reasons." + reason1 + ".description") + footer)))).append(" | ")
                    .color(ChatColor.DARK_GRAY).bold(true).append(reason2).reset().color(ChatColor.GRAY)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command2)).event(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                            ChatColor.translateAlternateColorCodes('&',
                                "&c&l" + reason2 + ":\n&7" + SilverstoneWarnings.config.getString(
                                    "reasons." + reason2 + ".description") + footer)))).append(" | ")
                    .color(ChatColor.DARK_GRAY).bold(true).append(reason3).reset().color(ChatColor.GRAY)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command3)).event(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                            ChatColor.translateAlternateColorCodes('&',
                                "&c&l" + reason3 + ":\n&7" + SilverstoneWarnings.config.getString(
                                    "reasons." + reason3 + ".description") + footer))));
            } catch (IndexOutOfBoundsException e1) {
                try {
                    String reason1 = reasonList.get(x);
                    String reason2 = reasonList.get(x + 1);

                    String command1 = "/zblankcmd";
                    String command2 = "/zblankcmd";
                    if (isWarning) {
                        command1 = "/warn " + username + " " + reason1;
                        command2 = "/warn " + username + " " + reason2;
                    }

                    message.append(reason1).reset().color(ChatColor.GRAY)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command1)).event(
                            new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                                ChatColor.translateAlternateColorCodes('&',
                                    "&c&l" + reason1 + ":\n&7" + SilverstoneWarnings.config.getString(
                                        "reasons." + reason1 + ".description") + footer)))).append(" | ")
                        .color(ChatColor.DARK_GRAY).bold(true).append(reason2).reset().color(ChatColor.GRAY)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command2)).event(
                            new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                                ChatColor.translateAlternateColorCodes('&',
                                    "&c&l" + reason2 + ":\n&7" + SilverstoneWarnings.config.getString(
                                        "reasons." + reason2 + ".description") + footer))));
                } catch (IndexOutOfBoundsException e2) {
                    String reason1 = reasonList.get(x);

                    String command1 = "/zblankcmd";
                    if (isWarning) command1 = "/warn " + username + " " + reason1;

                    message.append(reason1).reset().color(ChatColor.GRAY)
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command1)).event(
                            new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                                ChatColor.translateAlternateColorCodes('&',
                                    "&c&l" + reason1 + ":\n&7" + SilverstoneWarnings.config.getString(
                                        "reasons." + reason1 + ".description") + footer))));
                }
            }

        }

        sender.sendMessage(message.create());
    }
}