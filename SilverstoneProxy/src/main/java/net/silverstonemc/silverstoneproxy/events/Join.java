package net.silverstonemc.silverstoneproxy.events;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.util.ServerLink;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;
import net.silverstonemc.silverstoneproxy.UserManager;
import net.silverstonemc.silverstoneproxy.WarnPlayer;
import net.silverstonemc.silverstoneproxy.utils.NicknameUtils;
import org.geysermc.floodgate.api.FloodgateApi;
import org.spongepowered.configurate.serialize.SerializationException;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static net.silverstonemc.silverstoneproxy.ConfigurationManager.FileType.*;

public class Join {
    public Join(SilverstoneProxy instance) {
        i = instance;
    }

    public static final Map<Player, Message> newPlayers = new HashMap<>();

    private final SilverstoneProxy i;

    @Subscribe
    public void onServerConnect(ServerPreConnectEvent event) {
        if (!event.getResult().isAllowed()) return;

        // Prevent Bedrock players from joining servers other than survival
        if (!event.getOriginalServer().getServerInfo().getName().equals("survival"))
            if (FloodgateApi.getInstance().isFloodgatePlayer(event.getPlayer().getUniqueId())) {
                event.setResult(ServerPreConnectEvent.ServerResult.denied());

                // Kick if not in a server already
                if (event.getPreviousServer() == null) event.getPlayer().disconnect(Component.text("Sorry!\n\n",
                        NamedTextColor.RED).append(Component.text(
                        "Bedrock clients aren't supported on the server!\nIf you're part of the survival group, join directly via ",
                        NamedTextColor.GRAY))
                    .append(Component.text("survival.silverstonemc.net", NamedTextColor.AQUA)));

                else event.getPlayer().sendMessage(Component.text("I'm sorry, but Bedrock clients aren't supported on that server!",
                    NamedTextColor.RED));

                return;
            }

        // Don't allow players in if whitelist is on
        if (i.fileManager.files.get(WHITELIST).node("enabled").getBoolean() && !event.getPlayer()
            .hasPermission("silverstone.admin")) {
            event.setResult(ServerPreConnectEvent.ServerResult.denied());

            event.getPlayer().disconnect(Component
                .text("The server is currently closed for maintenance!", NamedTextColor.RED)
                .append(Component.text(
                    "\n\nSee status.silverstonemc.net for more details.",
                    NamedTextColor.GRAY)));
            return;
        }

        if (event.getPreviousServer() != null) return;

        int version = event.getPlayer().getProtocolVersion().getProtocol();
        i.logger.info("{} is joining with protocol version {}", event.getPlayer().getUsername(), version);

        // Anything less than current version and not with perm
        if (version < i.fileManager.files.get(CONFIG).node("current-protocol-version").getInt() && !event
            .getPlayer().hasPermission("silverstone.bypassversion")) {
            incompatibleClient(event, false, "current-version");
            return;
        }

        // Anything less than minimum version and with perm
        if (version < i.fileManager.files.get(CONFIG).node("minimum-protocol-version").getInt())
            incompatibleClient(event, true, "minimum-version");
    }

    private void incompatibleClient(ServerPreConnectEvent event, boolean atLeast, String versionPath) {
        event.setResult(ServerPreConnectEvent.ServerResult.denied());

        String text = atLeast ? "at least " : "";
        event.getPlayer().disconnect(Component
            .text("Your client isn't compatible with the server!\n\n", NamedTextColor.RED)
            .append(Component.text(
                "Please update to " + text + "Minecraft " + i.fileManager.files.get(CONFIG).node(versionPath)
                    .getString() + " to join.", NamedTextColor.GRAY)));
    }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        String username = player.getUsername();
        UUID uuid = player.getUniqueId();

        i.server.getScheduler().buildTask(
            i, () -> {
                boolean isVanished = player.hasPermission("silverstone.vanished");

                if (event.getPreviousServer().isPresent()) {
                    Component displayName = new NicknameUtils(i).getDisplayName(uuid);
                    if (isVanished) {
                        for (Player players : i.server.getAllPlayers())
                            if (players.hasPermission("silverstone.moderator")) players.sendMessage(Component
                                .text().append(displayName).colorIfAbsent(NamedTextColor.AQUA).append(
                                    Component.text(" has switched to the ", NamedTextColor.DARK_GRAY))
                                .append(Component.text(
                                    event.getServer().getServerInfo().getName(),
                                    NamedTextColor.DARK_AQUA))
                                .append(Component.text(" server", NamedTextColor.DARK_GRAY)));

                    } else for (Player players : i.server.getAllPlayers())
                        players.sendMessage(Component.text().append(displayName)
                            .colorIfAbsent(NamedTextColor.AQUA).append(Component.text(
                                " has switched to the ",
                                NamedTextColor.GRAY))
                            .append(Component.text(
                                event.getServer().getServerInfo().getName(),
                                NamedTextColor.AQUA)).append(Component.text(" server", NamedTextColor.GRAY)));

                    i.server.getConsoleCommandSource().sendMessage(Component.text().append(displayName)
                        .colorIfAbsent(NamedTextColor.AQUA).append(Component.text(
                            " has switched to the ",
                            NamedTextColor.GRAY))
                        .append(Component.text(
                            event.getServer().getServerInfo().getName(),
                            NamedTextColor.AQUA)).append(Component.text(" server", NamedTextColor.GRAY)));

                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("switchsound");
                    out.writeUTF(event.getPlayer().getUniqueId().toString());
                    out.writeBoolean(isVanished);
                    for (RegisteredServer servers : i.server.getAllServers())
                        servers.sendPluginMessage(SilverstoneProxy.IDENTIFIER, out.toByteArray());

                    return;
                }

                // Send server links (1.21+)
                if (event.getPlayer().getProtocolVersion().getProtocol() >= 767) {
                    List<ServerLink> links = new ArrayList<>();
                    links.add(ServerLink.serverLink(
                        ServerLink.Type.ANNOUNCEMENTS,
                        "https://discord.gg/VVSUEPd"));
                    links.add(ServerLink.serverLink(ServerLink.Type.BUG_REPORT, "https://silverstonemc.net"));
                    links.add(ServerLink.serverLink(ServerLink.Type.COMMUNITY, "https://discord.gg/VVSUEPd"));
                    links.add(ServerLink.serverLink(ServerLink.Type.FEEDBACK, "https://silverstonemc.net"));
                    links.add(ServerLink.serverLink(ServerLink.Type.FORUMS, "https://silverstonemc.net"));
                    links.add(ServerLink.serverLink(ServerLink.Type.NEWS, "https://discord.gg/VVSUEPd"));
                    links.add(ServerLink.serverLink(
                        ServerLink.Type.STATUS,
                        "https://status.silverstonemc.net"));
                    player.setServerLinks(links);
                }

                boolean userExists = UserManager.playerMap.containsKey(uuid);

                // Non-same client/server version message
                if (event.getPlayer().getProtocolVersion().getProtocol() != i.fileManager.files.get(CONFIG)
                    .node("current-protocol-version").getInt()) i.server.getScheduler().buildTask(
                    i, () -> event.getPlayer().sendMessage(Component.text(
                        "The server is currently built using Minecraft " + i.fileManager.files.get(CONFIG)
                            .node("current-version")
                            .getString() + " and is recommended to use this version to avoid any issues.",
                        NamedTextColor.RED))).delay(2, TimeUnit.SECONDS).schedule();

                // Add the user if they don't exist and send a notification
                if (!userExists) {
                    int staff = 0;
                    for (Player players : i.server.getAllPlayers()) {
                        players.sendMessage(Component.text("Welcome to Silverstone, ", NamedTextColor.GREEN)
                            .append(Component.text(username, NamedTextColor.AQUA))
                            .append(Component.text("!", NamedTextColor.GREEN)));
                        if (players.hasPermission("silverstone.moderator")) staff++;
                    }
                    int finalStaff = staff;

                    new Thread(
                        () -> {
                            TextChannel discord = SilverstoneProxy.jda.getTextChannelById(1075643222832984075L);

                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setAuthor(
                                username + " is new",
                                null,
                                "https://mc-heads.net/avatar/" + uuid);
                            embed.setImage("https://mc-heads.net/body/" + uuid);
                            embed.setFooter(finalStaff + " staff members online");
                            embed.setColor(new Color(36, 197, 19));

                            //noinspection DataFlowIssue
                            Message message = discord.sendMessageEmbeds(embed.build()).setActionRow(Button
                                .danger("warnskin:" + username, "Warn for inappropriate skin")
                                .withEmoji(Emoji.fromUnicode("⚠"))).complete();
                            newPlayers.put(player, message);
                        }, "New Player Discord").start();

                    new UserManager(i).addUser(uuid, username);
                }

                Component displayName = new NicknameUtils(i).getDisplayName(uuid);
                new NicknameUtils(i).changeDisplayName(player, displayName);

                // Silent join message
                if (isVanished) {
                    int nonStaff = 0;
                    for (Player players : i.server.getAllPlayers())
                        if (!players.hasPermission("silverstone.moderator")) nonStaff++;
                    if (nonStaff == 0) return;

                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor(
                        username + " silently joined the server",
                        null,
                        "https://mc-heads.net/avatar/" + uuid);
                    embed.setColor(new Color(0x2b2d31));
                    //noinspection DataFlowIssue
                    SilverstoneProxy.jda.getTextChannelById(1075643381734195210L)
                        .sendMessageEmbeds(embed.build()).queue();

                } else {
                    for (Player players : i.server.getAllPlayers())
                        players.sendMessage(Component.text()
                            .append(Component.text("+ ", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                            .append(displayName.colorIfAbsent(NamedTextColor.AQUA)));

                    i.server.getConsoleCommandSource().sendMessage(Component.text()
                        .append(Component.text("+ ", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                        .append(displayName.colorIfAbsent(NamedTextColor.AQUA)));
                }

                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("joinsound");
                out.writeUTF(event.getPlayer().getUniqueId().toString());
                out.writeBoolean(isVanished);
                for (RegisteredServer servers : i.server.getAllServers())
                    servers.sendPluginMessage(SilverstoneProxy.IDENTIFIER, out.toByteArray());

                // Update the username if it has changed
                if (userExists && !UserManager.playerMap.get(uuid).equals(username)) {
                    String oldUsername = new UserManager(i).getUsername(uuid);

                    // Notify everyone online if not vanished
                    if (!isVanished) i.server.getScheduler().buildTask(
                        i, () -> {
                            for (Player players : i.server.getAllPlayers())
                                players.sendMessage(Component.text(username, NamedTextColor.AQUA)
                                    .append(Component.text(" was previously known as ", NamedTextColor.GRAY))
                                    .append(Component.text(oldUsername, NamedTextColor.AQUA)));
                        }).delay(1, TimeUnit.SECONDS).schedule();

                    // Notify the Discord
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor(
                        username + " was previously known as: " + oldUsername,
                        null,
                        "https://mc-heads.net/avatar/" + uuid);
                    embed.setColor(new Color(0x2b2d31));
                    //noinspection DataFlowIssue
                    SilverstoneProxy.jda.getTextChannelById(1075680288841138257L)
                        .sendMessageEmbeds(embed.build()).queue();

                    UserManager.playerMap.remove(uuid);
                    new UserManager(i).addUser(uuid, username);
                }

                if (i.fileManager.files.get(WARNQUEUE).node("queue", uuid.toString()).virtual()) return;

                i.server.getScheduler().buildTask(
                    i, () -> {
                        if (i.server.getPlayer(uuid).isPresent()) try {
                            new WarnPlayer(i).warn(
                                uuid,
                                i.fileManager.files.get(WARNQUEUE).node("queue", uuid.toString())
                                    .getString());
                            i.fileManager.files.get(WARNQUEUE).node("queue", uuid.toString()).set(null);
                            i.fileManager.save(WARNQUEUE);
                        } catch (SerializationException e) {
                            throw new RuntimeException(e);
                        }
                    }).delay(3, TimeUnit.SECONDS).schedule();
            }).delay(38, TimeUnit.MILLISECONDS).schedule();
    }
}
