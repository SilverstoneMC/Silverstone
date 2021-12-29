package net.silverstonemc.silverstoneproxy.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

@SuppressWarnings("unused")
public class Link extends Command {

    public Link() {
        super("link", null, "linkbedrocktojava");
    }

    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&7Link your Bedrock account to your Java one at &3link.geysermc.org")));
    }
}
