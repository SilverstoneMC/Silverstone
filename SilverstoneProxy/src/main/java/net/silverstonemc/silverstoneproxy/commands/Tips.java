package net.silverstonemc.silverstoneproxy.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class Tips extends Command {
    public Tips() {
        super("tips");
    }

    //todo add tips
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
            "&7See &cgithub.com/SilverstoneMC/Silverstone/wiki/Tips &7to see all tips.")));
    }
}
