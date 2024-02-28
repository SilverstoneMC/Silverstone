package net.silverstonemc.silverstoneproxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;
import net.silverstonemc.silverstoneproxy.UserManager;
import net.silverstonemc.silverstoneproxy.utils.NoPlayerMsg;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.silverstonemc.silverstoneproxy.ConfigurationManager.FileType.WARNDATA;
import static net.silverstonemc.silverstoneproxy.ConfigurationManager.FileType.WARNQUEUE;

public class Warnings implements SimpleCommand {
    public Warnings(SilverstoneProxy instance) {
        i = instance;
    }

    private final SilverstoneProxy i;

    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();
        String senderName = sender.get(Identity.NAME).orElse("Console");

        String arg0;
        // If no player specified
        if (args.length < 1) {
            // If console didn't specify a player
            if (!(sender instanceof Player)) {
                sender.sendMessage(Component.text("Please specify a player!", NamedTextColor.RED));
                return;
            }
            arg0 = senderName;
            // If player specified but sender has no permission
        } else if (!sender.hasPermission("silverstone.moderator")) arg0 = senderName;
            // If player specified and sender does have permission
        else arg0 = args[0];

        UUID uuid = new UserManager(i).getUUID(arg0);
        String username = new UserManager(i).getUsername(uuid);

        if (uuid == null) {
            new NoPlayerMsg().nonExistentPlayerMessage(arg0, sender);
            return;
        }

        ConfigurationNode warnData = i.fileManager.files.get(WARNDATA).getNode("data", uuid.toString());
        ConfigurationNode warnQueue = i.fileManager.files.get(WARNQUEUE).getNode("queue", uuid.toString());

        // If not in queue
        if (warnQueue.isVirtual())
            // And has no warnings
            if (warnData.isVirtual()) {
                sender.sendMessage(Component.text(username, NamedTextColor.AQUA)
                    .append(Component.text(" has no warnings!", NamedTextColor.GREEN)));
                return;
            }

        sender.sendMessage(Component.text(username + "'s warnings:",
            NamedTextColor.RED,
            TextDecoration.BOLD));

        // If any warnings already exist
        if (!warnData.isVirtual()) for (ConfigurationNode reasonList : warnData.getChildrenMap().values())
            //noinspection DataFlowIssue
            sender.sendMessage(Component.text(reasonList.getKey().toString() + " - " + reasonList.getInt(),
                NamedTextColor.GRAY));

        // If in queue
        if (!warnQueue.isVirtual()) sender.sendMessage(Component.text(warnQueue.getString() + " (Queued)",
            NamedTextColor.GRAY));
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (!sender.hasPermission("silverstone.moderator")) return new CompletableFuture<>();

        List<String> arguments = new ArrayList<>(UserManager.playerMap.values());

        if (args.length == 0) return CompletableFuture.completedFuture(arguments);

        List<String> result = new ArrayList<>();
        for (String a : arguments)
            if (a.toLowerCase().startsWith(args[0].toLowerCase())) result.add(a);
        return CompletableFuture.completedFuture(result);
    }
}
