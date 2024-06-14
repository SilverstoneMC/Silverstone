package net.silverstonemc.silverstoneproxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;
import net.silverstonemc.silverstoneproxy.UndoWarning;
import net.silverstonemc.silverstoneproxy.UserManager;
import net.silverstonemc.silverstoneproxy.utils.NicknameUtils;
import net.silverstonemc.silverstoneproxy.utils.NoPlayerMsg;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.silverstonemc.silverstoneproxy.ConfigurationManager.FileType.*;

@SuppressWarnings("DataFlowIssue")
public class BaseCommand implements SimpleCommand {
    public BaseCommand(SilverstoneProxy instance) {
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
        String senderName = sender.get(Identity.NAME).orElse("Console");

        TextChannel warningChannel = SilverstoneProxy.jda.getTextChannelById(1075643034634563695L);

        if (args.length == 0) {
            sender.sendMessage(Component.text("/ssp <reload | remove | clear>", NamedTextColor.RED));
            return;
        }

        // reload
        try {
            if (args[0].equalsIgnoreCase("reload")) if (sender.hasPermission("silverstone.admin")) {
                i.fileManager.loadFiles();

                // Refresh user cache
                UserManager.playerMap.clear();
                for (ConfigurationNode users : i.fileManager.files.get(USERCACHE).node("users").childrenMap()
                    .values()) {
                    UUID uuid = UUID.fromString(users.key().toString());
                    String username = i.fileManager.files.get(USERCACHE).node("users", users.key())
                        .getString();
                    UserManager.playerMap.put(uuid, username);
                }

                // Update nicknames
                for (Player player : i.server.getAllPlayers())
                    new NicknameUtils(i).changeDisplayName(player,
                        new NicknameUtils(i).getDisplayName(player.getUniqueId()));

                sender.sendMessage(Component.text("SilverstoneProxy reloaded!", NamedTextColor.GREEN));
            } else sender.sendMessage(Component.text("You don't have permission to do that!",
                NamedTextColor.RED));


                // remove <reason> <player>
            else if (args[0].equalsIgnoreCase("remove")) {
                if (args.length < 3) {
                    sender.sendMessage(Component.text("/ssp remove <reason> <player>", NamedTextColor.RED));
                    return;
                }

                UUID uuid = new UserManager(i).getUUID(args[2]);
                String username = new UserManager(i).getUsername(uuid);
                Player player = i.server.getPlayer(uuid).isPresent() ? i.server.getPlayer(uuid).get() : null;

                if (uuid == null) {
                    new NoPlayerMsg().nonExistentPlayerMessage(args[2], sender);
                    return;
                }

                int count = i.fileManager.files.get(WARNDATA).node("data", uuid.toString(), args[1]).getInt();

                // Already has 0 warnings
                if ((count - 1) < 0) sender.sendMessage(Component.text(username, NamedTextColor.GRAY).append(
                        Component.text(" already has 0 ", NamedTextColor.RED))
                    .append(Component.text(args[1], NamedTextColor.GRAY))
                    .append(Component.text(" warnings.", NamedTextColor.RED)));
                else {
                    new UndoWarning(i).undoWarning(uuid, args[1], count);

                    if ((count - 1) == 0) i.fileManager.files.get(WARNDATA).node("data",
                        uuid.toString(),
                        args[1]).set(null);
                    else i.fileManager.files.get(WARNDATA).node("data", uuid.toString(), args[1])
                        .set(count - 1);
                    i.fileManager.save(WARNDATA);

                    if (i.fileManager.files.get(WARNDATA).node("data", uuid.toString()).childrenMap().values()
                        .isEmpty()) {
                        i.fileManager.files.get(WARNDATA).node("data", uuid.toString()).set(null);
                        i.fileManager.save(WARNDATA);
                    }

                    Component warningRemovedStaff = Component.text(senderName, NamedTextColor.GRAY).append(
                            Component.text(" removed a(n) ", NamedTextColor.RED)).append(Component.text(args[1],
                            NamedTextColor.GRAY)).append(Component.text(" warning from ", NamedTextColor.RED))
                        .append(Component.text(
                            username,
                            NamedTextColor.GRAY));

                    i.server.getConsoleCommandSource().sendMessage(warningRemovedStaff);
                    // Message staff
                    for (Player online : i.server.getAllPlayers())
                        if (online.hasPermission("silverstone.moderator")) online.sendMessage(
                            warningRemovedStaff);

                    Component warningRemovedPlayer = Component.text(senderName, NamedTextColor.GRAY).append(
                        Component.text(" removed a(n) ", NamedTextColor.RED)).append(Component.text(args[1],
                        NamedTextColor.GRAY)).append(Component.text(" warning from you.",
                        NamedTextColor.RED));

                    // Message player if online and command not silent
                    try {
                        if (!args[3].equalsIgnoreCase("-s")) if (player != null) player.sendMessage(
                            warningRemovedPlayer);
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                        if (player != null) player.sendMessage(warningRemovedPlayer);
                    }

                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor(senderName + " removed a(n) '" + args[1] + "' warning from " + username,
                        null,
                        "https://mc-heads.net/avatar/" + uuid);
                    embed.setColor(new Color(42, 212, 85));
                    warningChannel.sendMessageEmbeds(embed.build()).queue();
                }


                // clear <[reason]|all> <player>
            } else if (args[0].equalsIgnoreCase("clear")) {
                if (args.length < 3) {
                    sender.sendMessage(Component.text("/ssp clear <[reason]|all> <player>",
                        NamedTextColor.RED));
                    return;
                }

                UUID uuid = new UserManager(i).getUUID(args[2]);
                String username = new UserManager(i).getUsername(uuid);
                Player player = i.server.getPlayer(uuid).isPresent() ? i.server.getPlayer(uuid).get() : null;

                if (uuid == null) {
                    new NoPlayerMsg().nonExistentPlayerMessage(args[2], sender);
                    return;
                }

                // If all
                if (args[1].equalsIgnoreCase("all")) {
                    new UndoWarning(i).undoWarning(uuid, args[1], null);

                    i.fileManager.files.get(WARNDATA).node("data", uuid.toString()).set(null);
                    i.fileManager.save(WARNDATA);

                    Component warningsClearedStaff = Component.text(senderName, NamedTextColor.GRAY).append(
                            Component.text(" cleared all of ", NamedTextColor.RED))
                        .append(Component.text(username, NamedTextColor.GRAY)).append(Component.text("'s warnings.",
                            NamedTextColor.RED));

                    i.server.getConsoleCommandSource().sendMessage(warningsClearedStaff);
                    // Message staff
                    for (Player online : i.server.getAllPlayers())
                        if (online.hasPermission("silverstone.moderator")) online.sendMessage(
                            warningsClearedStaff);

                    Component warningsClearedPlayer = Component.text(senderName, NamedTextColor.GRAY).append(
                        Component.text(" cleared all of your warnings.", NamedTextColor.RED));

                    // Message player if online and command not silent
                    try {
                        if (!args[3].equalsIgnoreCase("-s")) if (player != null) player.sendMessage(
                            warningsClearedPlayer);
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                        if (player != null) player.sendMessage(warningsClearedPlayer);
                    }

                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor(senderName + " cleared all of " + username + "'s warnings",
                        null,
                        "https://mc-heads.net/avatar/" + uuid);
                    embed.setColor(new Color(42, 212, 85));
                    warningChannel.sendMessageEmbeds(embed.build()).queue();

                    // If a reason is defined
                } else {
                    new UndoWarning(i).undoWarning(uuid, args[1], null);

                    i.fileManager.files.get(WARNDATA).node("data", uuid.toString(), args[1]).set(null);
                    i.fileManager.save(WARNDATA);

                    Component warningsClearedStaff = Component.text(senderName, NamedTextColor.GRAY).append(
                            Component.text(" cleared all ", NamedTextColor.RED)).append(Component.text(args[1],
                            NamedTextColor.GRAY)).append(Component.text(" warnings from ", NamedTextColor.RED))
                        .append(Component.text(
                            username,
                            NamedTextColor.GRAY));

                    i.server.getConsoleCommandSource().sendMessage(warningsClearedStaff);
                    // Message staff
                    for (Player online : i.server.getAllPlayers())
                        if (online.hasPermission("silverstone.moderator")) online.sendMessage(
                            warningsClearedStaff);

                    Component warningsClearedPlayer = Component.text(senderName, NamedTextColor.GRAY).append(
                            Component.text(" cleared all your ", NamedTextColor.RED))
                        .append(Component.text(args[1], NamedTextColor.GRAY)).append(Component.text(" warnings.",
                            NamedTextColor.RED));

                    // Message player if online and command not silent
                    try {
                        if (!args[3].equalsIgnoreCase("-s")) if (player != null) player.sendMessage(
                            warningsClearedPlayer);
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                        if (player != null) player.sendMessage(warningsClearedPlayer);
                    }

                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor(senderName + " cleared all '" + args[1] + "' warnings from " + username,
                        null,
                        "https://mc-heads.net/avatar/" + uuid);
                    embed.setColor(new Color(42, 212, 85));
                    warningChannel.sendMessageEmbeds(embed.build()).queue();
                }
            }
        } catch (SerializationException e) {
            e.printStackTrace();
        }
    }

    final List<String> arguments = new ArrayList<>();

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (sender.hasPermission("silverstone.admin")) {
            if (!arguments.contains("reload")) arguments.add("reload");
        } else arguments.remove("reload");

        if (!arguments.contains("remove")) arguments.add("remove");
        if (!arguments.contains("clear")) arguments.add("clear");

        if (args.length == 0) return CompletableFuture.completedFuture(arguments);

        List<String> arguments2 = new ArrayList<>();
        if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("clear"))
            for (ConfigurationNode reason : i.fileManager.files.get(CONFIG).node("reasons").childrenMap()
                .values())
                arguments2.add(reason.key().toString());

        if (args[0].equalsIgnoreCase("clear")) arguments2.add("all");

        List<String> arguments3 = new ArrayList<>(UserManager.playerMap.values());

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

            case 3 -> {
                for (String a : arguments3)
                    if (a.toLowerCase().startsWith(args[2].toLowerCase())) result.add(a);
            }

            case 4 -> result.add("-s");
        }
        return CompletableFuture.completedFuture(result);
    }
}
