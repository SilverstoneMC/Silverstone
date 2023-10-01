package net.silverstonemc.silverstoneproxy.events;

import com.google.common.io.ByteStreams;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;
import net.silverstonemc.silverstoneproxy.WarnPlayer;

public class PluginMessage implements Listener {
    private final BungeeAudiences audience = SilverstoneProxy.getAdventure();
    private final SilverstoneProxy plugin = SilverstoneProxy.getPlugin();

    @EventHandler
    public void onMessageReceive(PluginMessageEvent event) {
        if (!event.getTag().equals("silverstone:pluginmsg")) return;

        String input = ByteStreams.newDataInput(event.getData()).readUTF();
        CommandSender sender = (CommandSender) event.getReceiver();

        if (input.startsWith("chatcolor-")) {
            String colorCode = input.replace("chatcolor-", "");

            if (colorCode.equals("reset")) plugin.getProxy().getPluginManager()
                .dispatchCommand(plugin.getProxy().getConsole(),
                    "lpb user " + sender.getName() + " meta removesuffix 500");
            else plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(),
                "lpb user " + sender.getName() + " meta setsuffix 500 &" + colorCode);
            return;
        }

        switch (input) {
            case "warn-admin" -> {
                ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();
                new WarnPlayer().warn(player.getUniqueId(), "admin");
            }

            case "rules" -> plugin.getProxy().getPluginManager().dispatchCommand(sender, "rules");

            case "live" -> {
                plugin.getProxy().getPluginManager().dispatchCommand(sender, "socialspy");

                if (sender.hasPermission("silverstone.live")) plugin.getProxy().getPluginManager()
                    .dispatchCommand(plugin.getProxy().getConsole(),
                        "lpb user " + sender.getName() + " parent remove live");
                else {
                    plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(),
                        "lpb user " + sender.getName() + " parent add live");

                    for (int x = 0; x < 50; x++) audience.sender(sender).sendMessage(Component.empty());
                    audience.sender(sender)
                        .sendMessage(Component.text("You are now live!", NamedTextColor.LIGHT_PURPLE));
                }
            }
        }
    }
}
