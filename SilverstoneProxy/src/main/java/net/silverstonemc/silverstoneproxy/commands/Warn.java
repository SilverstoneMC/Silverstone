package net.silverstonemc.silverstoneproxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;
import net.silverstonemc.silverstoneproxy.UserManager;
import net.silverstonemc.silverstoneproxy.WarnPlayer;
import net.silverstonemc.silverstoneproxy.utils.NoPlayerMsg;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.silverstonemc.silverstoneproxy.ConfigurationManager.FileType.CONFIG;

public class Warn implements SimpleCommand {
    public Warn(SilverstoneProxy instance) {
        i = instance;
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("silverstone.moderator");
    }

    private final SilverstoneProxy i;

    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (sender instanceof Player) switch (args.length) {
            case 0 -> sender.sendMessage(Component.text("/warn <player> <reason>", NamedTextColor.RED));

            case 1 -> {
                UUID uuid = new UserManager(i).getUUID(args[0]);
                String username = new UserManager(i).getUsername(uuid);

                if (uuid == null) {
                    new NoPlayerMsg().nonExistentPlayerMessage(args[0], sender);
                    return;
                }

                new WarnReasons(i).sendReasonList(true, sender, username);
            }

            case 2 -> {
                // Warn the targeted player
                if (checkIfValidReason(args[1])) {
                    i.server.getCommandManager().executeAsync(sender, "warn " + args[0]);
                    return;
                }

                UUID uuid = new UserManager(i).getUUID(args[0]);
                String username = new UserManager(i).getUsername(uuid);

                if (uuid == null) {
                    new NoPlayerMsg().nonExistentPlayerMessage(args[0], sender);
                    return;
                }

                for (int x = 0; x < 50; x++) sender.sendMessage(Component.empty());
                sender.sendMessage(Component.text("Warning ", NamedTextColor.RED).append(Component.text(username,
                        NamedTextColor.GRAY)).append(Component.text(" for reason: ", NamedTextColor.RED))
                    .append(Component.text(args[1], NamedTextColor.GRAY)));

                new WarnPlayer(i).warn(uuid, args[1]);
            }
        }
        else // If console does the command
            switch (args.length) {
                case 0, 1 -> sender.sendMessage(Component.text("/warn <player> <reason>",
                    NamedTextColor.RED));

                case 2 -> {
                    // Warn the targeted player
                    if (checkIfValidReason(args[1])) {
                        i.server.getCommandManager().executeAsync(i.server.getConsoleCommandSource(),
                            "reasons");
                        return;
                    }

                    UUID uuid = new UserManager(i).getUUID(args[0]);
                    String username = new UserManager(i).getUsername(uuid);

                    if (uuid == null) {
                        new NoPlayerMsg().nonExistentPlayerMessage(args[0], sender);
                        return;
                    }

                    sender.sendMessage(Component.text("Warning ", NamedTextColor.RED).append(Component.text(username,
                            NamedTextColor.GRAY)).append(Component.text(" for reason: ", NamedTextColor.RED))
                        .append(Component.text(args[1], NamedTextColor.GRAY)));

                    new WarnPlayer(i).warn(uuid, args[1]);
                }
            }
    }

    private boolean checkIfValidReason(String reason) {
        List<String> reasonList = new ArrayList<>();
        for (ConfigurationNode reasons : i.fileManager.files.get(CONFIG).node("reasons").childrenMap()
            .values())
            //noinspection DataFlowIssue
            reasonList.add(reasons.key().toString());
        return !reasonList.contains(reason);
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {
        String[] args = invocation.arguments();

        List<String> arguments = new ArrayList<>(UserManager.playerMap.values());

        if (args.length == 0) return CompletableFuture.completedFuture(arguments);

        List<String> arguments2 = new ArrayList<>();
        for (ConfigurationNode reasons : i.fileManager.files.get(CONFIG).node("reasons").childrenMap()
            .values())
            //noinspection DataFlowIssue
            arguments2.add(reasons.key().toString());

        List<String> result = new ArrayList<>();
        switch (args.length) {
            case 1 -> {
                for (String a : arguments)
                    if (a.toLowerCase().startsWith(args[0].toLowerCase())) result.add(a);
            }

            case 2 -> {
                for (String a : arguments2)
                    if (a.toLowerCase().startsWith(args[1].toLowerCase())) result.add(a);
            }
        }
        return CompletableFuture.completedFuture(result);
    }
}
