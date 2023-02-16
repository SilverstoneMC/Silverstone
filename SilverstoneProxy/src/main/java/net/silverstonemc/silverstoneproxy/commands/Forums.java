package net.silverstonemc.silverstoneproxy.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class Forums extends Command {
    public Forums() {
        super("forums", null, "site", "bugreport", "reportbug", "report", "reportplayer", "playerreport");
    }

    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(TextComponent.fromLegacyText(
            ChatColor.translateAlternateColorCodes('&', "&aVisit the forums at &bsilverstonemc.net")));
    }
}
