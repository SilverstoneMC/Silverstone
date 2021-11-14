package me.jasonhorkles.silverstoneproxy.commands;

import me.jasonhorkles.silverstoneproxy.SilverstoneProxy;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Restart extends Command {

    public Restart() {
        super("bcnetworkrestart", "silverstone.admin");
    }

    public void execute(CommandSender sender, String[] args) {
        for (ProxiedPlayer player : SilverstoneProxy.getInstance().getProxy().getPlayers())
            player.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&c&lWARNING &b&l> &aThe network will restart soon!")));

        if (!(sender instanceof ProxiedPlayer))
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Restart broadcast sent!"));
    }
}
