package net.silverstonemc.silverstoneproxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;

public class ChatSounds implements SimpleCommand {
    public ChatSounds(SilverstoneProxy instance) {
        luckPerms = instance.getLuckPerms();
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("silverstone.chatsounds");
    }

    private final LuckPerms luckPerms;

    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Sorry, but only players can do that.", NamedTextColor.RED));
            return;
        }

        boolean value = !sender.hasPermission("silverstone.chatsounds.enabled");
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        if (value) {
            user.data().add(Node.builder("silverstone.chatsounds.enabled").build());
            sender.sendMessage(Component.text("Chat sounds enabled.", NamedTextColor.GREEN));
        } else {
            user.data().remove(Node.builder("silverstone.chatsounds.enabled").build());
            sender.sendMessage(Component.text("Chat sounds disabled.", NamedTextColor.RED));
        }
        luckPerms.getUserManager().saveUser(user);
    }
}
