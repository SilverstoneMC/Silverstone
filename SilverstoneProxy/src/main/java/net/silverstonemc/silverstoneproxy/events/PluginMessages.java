package net.silverstonemc.silverstoneproxy.events;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.SuffixNode;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;
import net.silverstonemc.silverstoneproxy.WarnPlayer;

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
            case "chatcolor" -> {
                User user = luckPerms.getPlayerAdapter(Player.class).getUser(sender);
                char value = in.readChar();

                if (value == 'r') luckPerms.getUserManager().modifyUser(user.getUniqueId(), u -> {
                    Node node = SuffixNode.builder().priority(500).build();
                    u.data().remove(node);
                });
                    
                else luckPerms.getUserManager().modifyUser(user.getUniqueId(), u -> {
                    Node node = SuffixNode.builder("&" + value, 500).build();
                    u.data().add(node);
                });
            }

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
                    i.server.getCommandManager().executeAsync(sender, "socialspy enable");
                    
                    luckPerms.getUserManager().modifyUser(user.getUniqueId(), u -> {
                        Node node = InheritanceNode.builder(liveGroup).value(true).build();
                        u.data().remove(node);
                    });

                    sender.sendMessage(Component.text("You are no longer live.", NamedTextColor.GRAY));

                } else {
                    i.server.getCommandManager().executeAsync(sender, "socialspy disable");
                    
                    luckPerms.getUserManager().modifyUser(user.getUniqueId(), u -> {
                        Node node = InheritanceNode.builder(liveGroup).value(true).build();
                        u.data().add(node);
                    });

                    for (int x = 0; x < 50; x++) sender.sendMessage(Component.empty());
                    sender.sendMessage(Component.text("You are now live!", NamedTextColor.LIGHT_PURPLE));
                }

                luckPerms.getUserManager().saveUser(user);
            }
        }
    }
}
