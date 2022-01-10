package net.silverstonemc.silverstoneproxy.commands;

import net.silverstonemc.silverstoneproxy.SilverstoneProxy;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

public class GlobalMsg extends Command {

    private final Plugin plugin = SilverstoneProxy.getPlugin();

    public GlobalMsg() {
        super("globalmsg", "silverstone.console");
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            String message = "";
            for (String arg : args) {
                message = message.concat(arg);
                message = message.concat(" ");
            }

            message = message.trim();

            for (ProxiedPlayer player : plugin.getProxy().getPlayers())
                player.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
            plugin.getProxy().getLogger().info(ChatColor.translateAlternateColorCodes('&', message));

        } else sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: /globalmsg <message>"));
    }
}
