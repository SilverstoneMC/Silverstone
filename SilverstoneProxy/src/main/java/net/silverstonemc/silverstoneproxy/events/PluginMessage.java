package net.silverstonemc.silverstoneproxy.events;

import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;
import net.silverstonemc.silverstoneproxy.WarnPlayer;

public class PluginMessage {
    public PluginMessage(SilverstoneProxy instance) {
        i = instance;
    }

    private final SilverstoneProxy i;

    @Subscribe
    public void onMessageReceive(PluginMessageEvent event) {
        if (event.getIdentifier() != SilverstoneProxy.IDENTIFIER) return;

        // Return if not from a player
        if (!(event.getSource() instanceof Player sender)) return;

        String input = ByteStreams.newDataInput(event.getData()).readUTF();

        if (input.startsWith("chatcolor-")) {
            String colorCode = input.replace("chatcolor-", "");

            if (colorCode.equals("reset")) i.server.getCommandManager()
                .executeAsync(i.server.getConsoleCommandSource(),
                    "lpb user " + sender.getUsername() + " meta removesuffix 500");
            else i.server.getCommandManager().executeAsync(i.server.getConsoleCommandSource(),
                "lpb user " + sender.getUsername() + " meta setsuffix 500 &" + colorCode);
            return;
        }

        switch (input) {
            case "warn-admin" -> new WarnPlayer(i).warn(sender.getUniqueId(), "admin");

            case "rules" -> i.server.getCommandManager().executeAsync(sender, "rules");

            case "live" -> {
                i.server.getCommandManager().executeAsync(sender, "socialspy");

                if (sender.hasPermission("silverstone.live")) i.server.getCommandManager()
                    .executeAsync(i.server.getConsoleCommandSource(),
                        "lpb user " + sender.getUsername() + " parent remove live");
                else {
                    i.server.getCommandManager().executeAsync(i.server.getConsoleCommandSource(),
                        "lpb user " + sender.getUsername() + " parent add live");

                    for (int x = 0; x < 50; x++) sender.sendMessage(Component.empty());
                    sender.sendMessage(Component.text("You are now live!", NamedTextColor.LIGHT_PURPLE));
                }
            }
        }
    }
}
