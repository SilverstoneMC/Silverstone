package net.silverstonemc.silverstoneproxy.commands;

import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;

public class ChatSounds extends Command {
    public ChatSounds() {
        super("chatsounds", "silverstone.chatsounds");
    }

    private final BungeeAudiences audience = SilverstoneProxy.getAdventure();
    private final SilverstoneProxy plugin = SilverstoneProxy.getPlugin();

    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            audience.sender(sender)
                .sendMessage(Component.text("Sorry, but only players can do that.", NamedTextColor.RED));
            return;
        }

        if (!sender.hasPermission("silverstone.chatsounds.enabled")) {
            plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(),
                "lpb user " + sender.getName() + " permission set silverstone.chatsounds.enabled");
            audience.sender(sender).sendMessage(Component.text("Chat sounds enabled.", NamedTextColor.GREEN));
        } else {
            plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(),
                "lpb user " + sender.getName() + " permission unset silverstone.chatsounds.enabled");
            audience.sender(sender).sendMessage(Component.text("Chat sounds disabled.", NamedTextColor.RED));
        }
    }
}