package net.silverstonemc.silverstoneproxy.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class Tips extends Command {

    public Tips() {
        super("tips");
    }

    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(TextComponent.fromLegacyText("&7See &c&ngithub.com/SilverstoneMC/Silverstone/wiki/Tips&7 to see all tips."));
    }
}