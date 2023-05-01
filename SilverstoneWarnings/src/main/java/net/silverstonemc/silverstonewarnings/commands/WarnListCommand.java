package net.silverstonemc.silverstonewarnings.commands;

import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.silverstonemc.silverstonewarnings.ConfigurationManager;
import net.silverstonemc.silverstonewarnings.SilverstoneWarnings;
import net.silverstonemc.silverstonewarnings.UserManager;

import java.util.UUID;

public class WarnListCommand extends Command {
    public WarnListCommand() {
        super("warnlist", "silverstone.moderator");
    }

    private final BungeeAudiences adventure = SilverstoneWarnings.getAdventure();

    public void execute(CommandSender sender, String[] args) {
        adventure.sender(sender).sendMessage(
            Component.text("All warnings:").color(NamedTextColor.RED).decorate(TextDecoration.BOLD));

        for (String uuid : ConfigurationManager.data.getSection("data").getKeys())
            for (String warning : ConfigurationManager.data.getSection("data." + uuid).getKeys())
                adventure.sender(sender).sendMessage(Component.text(new UserManager().getUsername(
                    UUID.fromString(uuid)) + " - " + warning + " - " + ConfigurationManager.data.getInt(
                    "data." + uuid + "." + warning)).color(NamedTextColor.GRAY));
    }
}
