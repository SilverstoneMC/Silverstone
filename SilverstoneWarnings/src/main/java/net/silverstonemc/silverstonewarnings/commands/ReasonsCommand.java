package net.silverstonemc.silverstonewarnings.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.silverstonemc.silverstonewarnings.SilverstoneWarnings;

import java.util.ArrayList;

public class ReasonsCommand extends Command {

    public ReasonsCommand() {
        super("reasons", "silverstone.trialmod", "categories");
    }

    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&c&lAvailable warning reasons:")));

        ArrayList<String> reasonList = new ArrayList<>(SilverstoneWarnings.config
                .getSection("reasons")
                .getKeys());
        for (int x = 0; x < reasonList.size(); x = x + 3)
            try {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&7" + reasonList.get(x) + "  &8&l|&7  " + reasonList
                        .get(x + 1) + "  &8&l|&7  " + reasonList.get(x + 2))));
            } catch (IndexOutOfBoundsException e1) {
                try {
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&7" + reasonList.get(x) + "  &8&l|&7  " + reasonList
                            .get(x + 1))));
                } catch (IndexOutOfBoundsException e2) {
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + reasonList.get(x)));
                }
            }
    }
}