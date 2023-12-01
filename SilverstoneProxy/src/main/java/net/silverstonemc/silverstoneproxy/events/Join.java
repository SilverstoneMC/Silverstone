package net.silverstonemc.silverstoneproxy.events;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
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

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static net.silverstonemc.silverstoneproxy.ConfigurationManager.FileType.CONFIG;
import static net.silverstonemc.silverstoneproxy.ConfigurationManager.FileType.WARNQUEUE;

public class Join {
    public Join(SilverstoneProxy instance) {
        i = instance;
    }

    public static final Map<Player, Message> newPlayers = new HashMap<>();

    private final SilverstoneProxy i;

    @Subscribe
    public void onServerConnect(ServerPreConnectEvent event) {
        if (event.getPreviousServer() != null) return;

        int version = event.getPlayer().getProtocolVersion().getProtocol();
        i.logger.info(event.getPlayer().getUsername() + " is joining with protocol version " + version);

        // Anything less than current version and not staff
        if (version < i.fileManager.files.get(CONFIG).getNode("current-protocol-version")
            .getInt() && !event.getPlayer().hasPermission("silverstone.moderator")) {
            incompatibleClient(event, false, "current-version");
            return;
        }

        // Anything less than minimum version and staff
        if (version < i.fileManager.files.get(CONFIG).getNode("minimum-protocol-version").getInt())
            incompatibleClient(event, true, "minimum-version");
    }

    private void incompatibleClient(ServerPreConnectEvent event, boolean atLeast, String versionPath) {
        event.setResult(ServerPreConnectEvent.ServerResult.denied());

        String text = atLeast ? "at least " : "";
        event.getPlayer().disconnect(
            Component.text("Your client isn't compatible with the server!\n\n", NamedTextColor.RED).append(
                Component.text("Please update to " + text + "Minecraft " + i.fileManager.files.get(CONFIG)
                    .getNode(versionPath).getString() + " to join.", NamedTextColor.GRAY)));
    }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        String username = player.getUsername();
        boolean isVanished = player.hasPermission("silverstone.vanished");
        UUID uuid = player.getUniqueId();

        i.server.getScheduler().buildTask(i, () -> {
            Component displayName = new NicknameUtils(i).getDisplayName(uuid);
            new NicknameUtils(i).changeDisplayName(player, displayName);

            if (event.getPreviousServer().isPresent()) {
                if (isVanished) {
                    for (Player players : i.server.getAllPlayers())
                        if (players.hasPermission("silverstone.moderator")) players.sendMessage(
                            Component.text().append(displayName).colorIfAbsent(NamedTextColor.AQUA)
                                .append(Component.text(" has switched to the ", NamedTextColor.DARK_GRAY))
                                .append(Component.text(event.getServer().getServerInfo().getName(),
                                    NamedTextColor.DARK_AQUA))
                                .append(Component.text(" server", NamedTextColor.DARK_GRAY)));

                } else for (Player players : i.server.getAllPlayers())
                    players.sendMessage(
                        Component.text().append(displayName).colorIfAbsent(NamedTextColor.AQUA)
                            .append(Component.text(" has switched to the ", NamedTextColor.GRAY)).append(
                                Component.text(event.getServer().getServerInfo().getName(), NamedTextColor.AQUA))
                            .append(Component.text(" server", NamedTextColor.GRAY)));

                return;
            }

            boolean userExists = UserManager.playerMap.containsKey(uuid);

            // Older version message (should only be seen by staff)
            if (event.getPlayer().getProtocolVersion().getProtocol() < i.fileManager.files.get(CONFIG)
                .getNode("current-protocol-version").getInt()) event.getPlayer().sendMessage(Component.text(
                "The server is currently built using Minecraft " + i.fileManager.files.get(CONFIG)
                    .getNode("current-version")
                    .getString() + " - please update your client to use all the features.",
                NamedTextColor.RED));

            // Silent join message
            if (isVanished) {
                int nonStaff = 0;
                for (Player players : i.server.getAllPlayers())
                    if (!players.hasPermission("silverstone.moderator")) nonStaff++;
                if (nonStaff == 0) return;

                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor(username + " silently joined the server", null,
                    "https://crafatar.com/avatars/" + uuid + "?overlay=true");
                embed.setColor(new Color(0x2b2d31));
                //noinspection DataFlowIssue
                SilverstoneProxy.jda.getTextChannelById(1075643381734195210L).sendMessageEmbeds(embed.build())
                    .queue();

            } else for (Player players : i.server.getAllPlayers())
                players.sendMessage(Component.text()
                    .append(Component.text("+ ", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                    .append(displayName.colorIfAbsent(NamedTextColor.AQUA)));

            // Vanish status handled on backend
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
                if (!isVanished) i.server.getScheduler().buildTask(i, () -> {
                    for (Player players : i.server.getAllPlayers())
                        players.sendMessage(Component.text(username, NamedTextColor.AQUA)
                            .append(Component.text(" was previously known as ", NamedTextColor.GRAY))
                            .append(Component.text(oldUsername, NamedTextColor.AQUA)));
                }).delay(1, TimeUnit.SECONDS).schedule();

                // Notify the Discord
                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor(username + " was previously known as: " + oldUsername, null,
                    "https://crafatar.com/avatars/" + uuid + "?overlay=true");
                embed.setColor(new Color(0x2b2d31));
                //noinspection DataFlowIssue
                SilverstoneProxy.jda.getTextChannelById(1075680288841138257L).sendMessageEmbeds(embed.build())
                    .queue();

                UserManager.playerMap.remove(uuid);
                new UserManager(i).addUser(uuid, username);
            }

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

                new Thread(() -> {
                    TextChannel discord = SilverstoneProxy.jda.getTextChannelById(1075643222832984075L);

                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor(username + " is new", null,
                        "https://crafatar.com/avatars/" + uuid + "?overlay=true");
                    embed.setImage("https://crafatar.com/renders/body/" + uuid + "?overlay=true");
                    embed.setFooter(finalStaff + " staff members online");
                    embed.setColor(new Color(36, 197, 19));

                    //noinspection DataFlowIssue
                    Message message = discord.sendMessageEmbeds(embed.build()).setActionRow(
                        Button.danger("warnskin:" + username, "Warn for inappropriate skin")
                            .withEmoji(Emoji.fromUnicode("⚠"))).complete();
                    newPlayers.put(player, message);
                }, "New Player Discord").start();

                new UserManager(i).addUser(uuid, username);
            }
        }).delay(50, TimeUnit.MILLISECONDS).schedule();

        if (i.fileManager.files.get(WARNQUEUE).getNode("queue", uuid.toString()).isVirtual()) return;

        i.server.getScheduler().buildTask(i, () -> {
            if (i.server.getPlayer(uuid).isPresent()) {
                new WarnPlayer(i).warn(uuid,
                    i.fileManager.files.get(WARNQUEUE).getNode("queue", uuid.toString()).getString());
                i.fileManager.files.get(WARNQUEUE).getNode("queue", uuid.toString()).setValue(null);
                i.fileManager.save(WARNQUEUE);
            }
        }).delay(3, TimeUnit.SECONDS).schedule();
    }
}
