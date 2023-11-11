package net.silverstonemc.silverstoneproxy.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;

public class Discord extends Command {
    public Discord() {
        super("discord");
    }

    public void execute(CommandSender sender, String[] args) {
        SilverstoneProxy.getAdventure().sender(sender).sendMessage(
            Component.text("Join the Discord server at ").color(NamedTextColor.DARK_AQUA).append(
                Component.text("discord.gg/VVSUEPd").color(NamedTextColor.GREEN)
                    .clickEvent(ClickEvent.openUrl("https://discord.gg/VVSUEPd"))));
    }
}
