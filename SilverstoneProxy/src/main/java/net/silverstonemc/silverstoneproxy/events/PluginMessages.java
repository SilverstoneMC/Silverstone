package net.silverstonemc.silverstoneproxy.events;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;
import net.silverstonemc.silverstoneproxy.WarnPlayer;

import java.util.concurrent.TimeUnit;

public class PluginMessages {
    public PluginMessages(SilverstoneProxy instance) {
        i = instance;
        luckPerms = instance.getLuckPerms();
    }

    private final LuckPerms luckPerms;
    private final SilverstoneProxy i;

    @Subscribe
    public void onMessageReceive(PluginMessageEvent event) {
        if (event.getIdentifier() != SilverstoneProxy.IDENTIFIER) return;
        if (!(event.getSource() instanceof ServerConnection connection)) return;
        Player sender = connection.getPlayer();

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String subchannel = in.readUTF();

        switch (subchannel) {
            case "warn" -> new WarnPlayer(i).warn(sender.getUniqueId(), in.readUTF());

            case "rules" -> i.server.getCommandManager().executeAsync(sender, "rules");

            case "live" -> {
                Group liveGroup = luckPerms.getGroupManager().getGroup("live");
                if (liveGroup == null) {
                    sender.sendMessage(Component.text("The live group doesn't exist!", NamedTextColor.RED));
                    return;
                }

                User user = luckPerms.getPlayerAdapter(Player.class).getUser(sender);

                if (sender.hasPermission("silverstone.live")) {
                    i.server.getCommandManager().executeAsync(sender, "socialspy true");

                    luckPerms.getUserManager().modifyUser(user.getUniqueId(), u -> {
                        Node node = InheritanceNode.builder(liveGroup).value(true).build();
                        u.data().remove(node);
                    });

                    sender.sendMessage(Component.text("You are no longer live.", NamedTextColor.GRAY));

                } else {
                    i.server.getCommandManager().executeAsync(sender, "socialspy false");

                    luckPerms.getUserManager().modifyUser(user.getUniqueId(), u -> {
                        Node node = InheritanceNode.builder(liveGroup).value(true).build();
                        u.data().add(node);
                    });

                    i.server.getScheduler().buildTask(i, () -> {
                        for (int x = 0; x < 50; x++) sender.sendMessage(Component.empty());
                        sender.sendMessage(Component.text("You are now live!", NamedTextColor.LIGHT_PURPLE));
                    }).delay(100, TimeUnit.MILLISECONDS).schedule();
                }

                luckPerms.getUserManager().saveUser(user);
            }

            case "kick" -> sender.disconnect(GsonComponentSerializer.gson().deserialize(in.readUTF())
                .colorIfAbsent(NamedTextColor.GRAY));
        }
    }
}
