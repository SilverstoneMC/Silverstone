package net.silverstonemc.silverstoneproxy;

import com.velocitypowered.api.proxy.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;

import static net.silverstonemc.silverstoneproxy.ConfigurationManager.FileType.*;

@SuppressWarnings("DataFlowIssue")
public class WarnPlayer {
    public WarnPlayer(SilverstoneProxy instance) {
        i = instance;
    }

    private final SilverstoneProxy i;

    public void warn(UUID uuid, String reason) {
        // Me, dragon, panda, ace
        if (uuid.toString().equals("a28173af-f0a9-47fe-8549-19c6bccf68da") || uuid.toString().equals(
            "e70a4622-85b6-417d-9201-7322e5094465") || uuid.toString().equals(
            "75fb05a2-9d9e-49cb-be34-6bd5215548ba") || uuid.toString().equals(
            "5c3d3b7c-aa02-4751-ae4b-60b277da9c35")) {
            i.logger.error(
                "Cancelled warning {} for reason: {}",
                new UserManager(i).getUsername(uuid),
                reason);
            return;
        }

        TextChannel warningChannel = SilverstoneProxy.jda.getTextChannelById(1075643034634563695L);

        String username = new UserManager(i).getUsername(uuid);
        Player player = i.server.getPlayer(uuid).isPresent() ? i.server.getPlayer(uuid).get() : null;

        // If player is not online, queue the warning
        try {
            if (player == null) {
                i.fileManager.files.get(WARNQUEUE).node("queue", uuid.toString()).set(reason);
                i.fileManager.save(WARNQUEUE);

                Component offlinePlayerWarned = Component.text("Offline player ", NamedTextColor.RED).append(
                    Component.text(username, NamedTextColor.GRAY),
                    Component.text(" has been warned for reason: ", NamedTextColor.RED),
                    Component.text(reason, NamedTextColor.GRAY));

                i.server.getConsoleCommandSource().sendMessage(offlinePlayerWarned);
                // Message staff
                for (Player online : i.server.getAllPlayers())
                    if (online.hasPermission("silverstone.moderator"))
                        online.sendMessage(offlinePlayerWarned);

                EmbedBuilder embed = new EmbedBuilder();
                embed.setAuthor(
                    username + " has a warning queued: " + reason,
                    null,
                    "https://mc-heads.net/avatar/" + uuid);
                embed.setColor(new Color(255, 217, 0));
                warningChannel.sendMessageEmbeds(embed.build()).queue();
                return;
            }

            ConfigurationNode warnData = i.fileManager.files.get(WARNDATA).node(
                "data",
                uuid.toString(),
                reason);
            // If player doesn't have the reason in their data, add it
            // If they already had the reason, add 1 to it
            int warningCount = 1;
            if (!warnData.virtual()) warningCount = warnData.getInt() + 1;

            warnData.set(warningCount);
            i.fileManager.save(WARNDATA);

            // Grab the amount of punishments in the config
            ConfigurationNode punishments = i.fileManager.files.get(CONFIG).node("reasons", reason, "add");
            int punishmentCount = punishments.childrenMap().size();

            // Get the correct warning number
            int punishmentNumber = warningCount % punishmentCount;
            if (punishmentNumber == 0) punishmentNumber = punishmentCount;

            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor(
                username + " was warned for reason: " + reason + " | Warning " + punishmentNumber + "/" + punishmentCount,
                null,
                "https://mc-heads.net/avatar/" + uuid);
            embed.setColor(new Color(204, 27, 53));
            warningChannel.sendMessageEmbeds(embed.build()).addComponents(ActionRow.of(
                Button.success("removewarning: " + reason + " :" + uuid, "Undo '" + reason + "' warning")
                    .withEmoji(Emoji.fromUnicode("↩")),
                Button.primary(
                    "removewarningsilently: " + reason + " :" + uuid,
                    "Undo '" + reason + "' warning silently").withEmoji(Emoji.fromUnicode("↩")),
                Button
                    .secondary("clearwarning: " + reason + " :" + uuid, "Clear all '" + reason + "' warnings")
                    .withEmoji(Emoji.fromUnicode("➖")),
                Button.danger("clearallwarnings:" + uuid, "Clear all of " + username + "'s warnings")
                    .withEmoji(Emoji.fromUnicode("❌")))).queue();

            // Warn the player
            Iterable<String> cmdList;
            try {
                cmdList = new ArrayList<>(punishments.node(punishmentNumber).getList(String.class));
            } catch (SerializationException e) {
                throw new RuntimeException(e);
            }
            for (String cmd : cmdList)
                i.server.getCommandManager().executeAsync(
                    i.server.getConsoleCommandSource(),
                    cmd.replace("{player}", username));

        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }
}
