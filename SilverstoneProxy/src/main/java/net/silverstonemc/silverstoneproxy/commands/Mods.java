package net.silverstonemc.silverstoneproxy.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class Mods extends Command {

    public Mods() {
        super("mods");
    }

    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&2Modifications we recommend: &3&ngithub.com/SilverstoneMC/Silverstone/wiki/Modifications#mods-we-recommend")));
    }
}
