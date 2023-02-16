package net.silverstonemc.silverstonewarnings.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.silverstonemc.silverstonewarnings.SilverstoneWarnings;

import java.util.UUID;

public class WarnListCommand extends Command {

    public WarnListCommand() {
        super("warnlist", "silverstone.moderator");
    }

    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&c&lAll warnings:")));

        for (String uuid : SilverstoneWarnings.data.getSection("data").getKeys())
            for (String warning : SilverstoneWarnings.data.getSection("data." + uuid).getKeys())
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&7" + SilverstoneWarnings.getPlugin()
                        .getPlayerName(UUID.fromString(uuid)) + " - " + warning + " - " + SilverstoneWarnings.data.getInt("data." + uuid + "." + warning))));
    }
}
