package net.silverstonemc.silverstoneproxy.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.silverstonemc.silverstoneproxy.ConfigurationManager;
import net.silverstonemc.silverstoneproxy.UserManager;

import java.util.UUID;

public class WarnQueue extends Command {
    public WarnQueue() {
        super("warnqueue", "silverstone.moderator");
    }

    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(TextComponent.fromLegacyText(
            ChatColor.translateAlternateColorCodes('&', "&c&lQueued warnings:")));

        for (String uuid : ConfigurationManager.queue.getSection("queue").getKeys())
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                "&7" + new UserManager().getUsername(
                    UUID.fromString(uuid)) + " - " + ConfigurationManager.queue.getString("queue." + uuid))));
    }
}
