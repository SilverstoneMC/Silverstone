package net.silverstonemc.silverstoneproxy.events;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;
import net.silverstonemc.silverstoneproxy.WarnPlayer;

public class PluginMessages {
    public PluginMessages(SilverstoneProxy instance) {
        i = instance;
    }

    private final SilverstoneProxy i;

    @Subscribe
    public void onMessageReceive(PluginMessageEvent event) {
        if (event.getIdentifier() != SilverstoneProxy.IDENTIFIER) return;
        if (!(event.getSource() instanceof ServerConnection connection)) return;
        Player sender = connection.getPlayer();

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String subchannel = in.readUTF();

        switch (subchannel) {
            case "chatcolor" -> {
                char value = in.readChar();

                if (value == 'r') i.server.getCommandManager()
                    .executeAsync(i.server.getConsoleCommandSource(),
                        "lpv user " + sender.getUsername() + " meta removesuffix 500");
                else i.server.getCommandManager().executeAsync(i.server.getConsoleCommandSource(),
                    "lpv user " + sender.getUsername() + " meta setsuffix 500 &" + value);
            }

            case "warn" -> new WarnPlayer(i).warn(sender.getUniqueId(), in.readUTF());

            case "rules" -> i.server.getCommandManager().executeAsync(sender, "rules");

            case "live" -> {
                i.server.getCommandManager().executeAsync(sender, "socialspy");

                if (sender.hasPermission("silverstone.live")) i.server.getCommandManager()
                    .executeAsync(i.server.getConsoleCommandSource(),
                        "lpv user " + sender.getUsername() + " parent remove live");
                else {
                    i.server.getCommandManager().executeAsync(i.server.getConsoleCommandSource(),
                        "lpv user " + sender.getUsername() + " parent add live");

                    for (int x = 0; x < 50; x++) sender.sendMessage(Component.empty());
                    sender.sendMessage(Component.text("You are now live!", NamedTextColor.LIGHT_PURPLE));
                }
            }
        }
    }
}
