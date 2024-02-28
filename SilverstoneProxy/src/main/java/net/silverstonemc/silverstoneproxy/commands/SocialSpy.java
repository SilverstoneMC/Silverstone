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

public class SocialSpy implements SimpleCommand {
    public SocialSpy(SilverstoneProxy instance) {
        luckPerms = instance.getLuckPerms();
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("silverstone.socialspy");
    }

    private final LuckPerms luckPerms;

    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Sorry, but only players can do that.", NamedTextColor.RED));
            return;
        }

        boolean isEnabled = sender.hasPermission("silverstone.socialspy.enabled");

        if (args.length > 0) if (args[0].equalsIgnoreCase("enabled")) isEnabled = false;
        else if (args[0].equalsIgnoreCase("disabled")) isEnabled = true;

        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        if (isEnabled) {
            user.data().remove(Node.builder("silverstone.socialspy.enabled").build());
            sender.sendMessage(Component.text("Socialspy disabled.", NamedTextColor.RED));
        } else {
            user.data().add(Node.builder("silverstone.socialspy.enabled").build());
            sender.sendMessage(Component.text("Socialspy enabled.", NamedTextColor.GREEN));
        }
        luckPerms.getUserManager().saveUser(user);
    }
}
