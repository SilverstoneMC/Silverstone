package net.silverstonemc.silverstoneproxy.commands.chatemotes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;

public class FacePalm extends Command {
    public FacePalm() {
        super("facepalm");
    }

    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer player)) {
            SilverstoneProxy.getAdventure().sender(sender).sendMessage(
                Component.text("Sorry, but only players can do that.").color(NamedTextColor.RED));
            return;
        }

        player.chat("/)_-)");
    }
}
