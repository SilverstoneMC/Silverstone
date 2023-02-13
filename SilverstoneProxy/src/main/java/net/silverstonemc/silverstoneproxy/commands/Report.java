package net.silverstonemc.silverstoneproxy.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class Report extends Command {

    public Report() {
        super("report", null, "reportplayer", "playerreport");
    }

    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&cYou may make player reports at &7github.com/SilverstoneMC/Silverstone/issues/new/choose")));
    }
}
