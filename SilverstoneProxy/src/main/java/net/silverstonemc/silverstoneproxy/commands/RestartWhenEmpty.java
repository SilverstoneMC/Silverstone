package net.silverstonemc.silverstoneproxy.commands;

import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;

import java.util.concurrent.TimeUnit;

public class RestartWhenEmpty extends Command {

    private final Plugin plugin = SilverstoneProxy.getPlugin();
    private final BungeeAudiences audience = SilverstoneProxy.getAdventure();

    public RestartWhenEmpty() {
        super("brestartwhenempty", "silverstone.admin");
    }

    public void execute(CommandSender sender, String[] args) {
        for (ProxiedPlayer player : plugin.getProxy().getPlayers())
            audience.player(player)
                    .sendActionBar(Component.text(ChatColor.translateAlternateColorCodes('&', "&c&lSERVER SCHEDULED TO RESTART WHEN EMPTY")));
        plugin.getLogger().info("Server scheduled to restart when empty.");

        Runnable task = () -> {
            if (plugin.getProxy().getPlayers().size() == 0) plugin.getProxy().stop();
        };
        plugin.getProxy().getScheduler().schedule(plugin, task, 0, 15, TimeUnit.SECONDS);
    }
}
