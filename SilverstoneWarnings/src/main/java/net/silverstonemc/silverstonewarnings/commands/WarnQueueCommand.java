package net.silverstonemc.silverstonewarnings.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.silverstonemc.silverstonewarnings.SilverstoneWarnings;

import java.util.UUID;

public class WarnQueueCommand extends Command {
    public WarnQueueCommand() {
        super("warnqueue", "silverstone.warnings.list");
    }

    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(TextComponent.fromLegacyText(
            ChatColor.translateAlternateColorCodes('&', "&c&lQueued warnings:")));
        for (String uuid : SilverstoneWarnings.queue.getSection("queue").getKeys())
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                "&7" + SilverstoneWarnings.getPlugin()
                    .getPlayerName(UUID.fromString(uuid)) + " - " + SilverstoneWarnings.queue.getString(
                    "queue." + uuid))));
    }
}
