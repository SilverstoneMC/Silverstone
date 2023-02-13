package net.silverstonemc.silverstoneproxy.commands;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.sound.Sound;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;

public class Restart extends Command {

    private final BungeeAudiences audience = SilverstoneProxy.getAdventure();

    public Restart() {
        super("bcnetworkrestart", "silverstone.admin");
    }

    public void execute(CommandSender sender, String[] args) {
        for (ProxiedPlayer player : SilverstoneProxy.getPlugin().getProxy().getPlayers()) {
            player.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&c&lWARNING &b&l> &aThe network will restart soon!")));
            audience.player(player)
                    .playSound(Sound.sound(Key.key("block.note_block.harp"), Sound.Source.MASTER, 100, 1.6f));
        }

        if (!(sender instanceof ProxiedPlayer))
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Restart broadcast sent!"));
    }
}
