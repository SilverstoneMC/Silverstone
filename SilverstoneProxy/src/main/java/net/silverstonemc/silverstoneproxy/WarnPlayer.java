package net.silverstonemc.silverstoneproxy;

import com.google.common.reflect.TypeToken;
import com.velocitypowered.api.proxy.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

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
        TextChannel warningChannel = SilverstoneProxy.jda.getTextChannelById(1075643034634563695L);

        String username = new UserManager(i).getUsername(uuid);
        Player player = i.server.getPlayer(uuid).isPresent() ? i.server.getPlayer(uuid).get() : null;

        // If player is not online, queue the warning
        if (player == null) {
            i.fileManager.files.get(WARNQUEUE).getNode("queue", uuid.toString()).setValue(reason);
            i.fileManager.save(WARNQUEUE);

            Component offlinePlayerWarned = Component.text("Offline player ", NamedTextColor.RED)
                .append(Component.text(username, NamedTextColor.GRAY))
                .append(Component.text(" has been warned for reason: ", NamedTextColor.RED))
                .append(Component.text(reason, NamedTextColor.GRAY));

            i.server.getConsoleCommandSource().sendMessage(offlinePlayerWarned);
            // Message staff
            for (Player online : i.server.getAllPlayers())
                if (online.hasPermission("silverstone.moderator")) online.sendMessage(offlinePlayerWarned);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor(username + " has a warning queued: " + reason, null,
                "https://crafatar.com/avatars/" + uuid + "?overlay=true");
            embed.setColor(new Color(255, 217, 0));
            warningChannel.sendMessageEmbeds(embed.build()).queue();
            return;
        }

        ConfigurationNode warnData = i.fileManager.files.get(WARNDATA).getNode("data", uuid.toString(), reason);
        // If player doesn't have the reason in their data, add it
        // If they already had the reason, add 1 to it
        int warningCount = 1;
        if (!warnData.isVirtual()) warningCount = warnData.getInt() + 1;

        warnData.setValue(warningCount);
        i.fileManager.save(WARNDATA);

        // Grab the amount of punishments in the config
        ConfigurationNode punishments = i.fileManager.files.get(CONFIG).getNode("reasons", reason, "add");
        int punishmentCount = punishments.getChildrenMap().values().size();

        // Get the correct warning number
        int punishmentNumber = warningCount % punishmentCount;
        if (punishmentNumber == 0) punishmentNumber = punishmentCount;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(
            username + " was warned for reason: " + reason + " | Warning " + punishmentNumber + "/" + punishmentCount,
            null, "https://crafatar.com/avatars/" + uuid + "?overlay=true");
        embed.setColor(new Color(204, 27, 53));
        warningChannel.sendMessageEmbeds(embed.build()).setActionRow(
            Button.success("removewarning: " + reason + " :" + uuid, "Undo '" + reason + "' warning")
                .withEmoji(Emoji.fromUnicode("↩")),
            Button.primary("removewarningsilently: " + reason + " :" + uuid,
                "Undo '" + reason + "' warning silently").withEmoji(Emoji.fromUnicode("↩")),
            Button.secondary("clearwarning: " + reason + " :" + uuid, "Clear all '" + reason + "' warnings")
                .withEmoji(Emoji.fromUnicode("➖")),
            Button.danger("clearallwarnings:" + uuid, "Clear all of " + username + "'s warnings")
                .withEmoji(Emoji.fromUnicode("❌"))).queue();

        // Warn the player
        ArrayList<String> cmdList;
        try {
            cmdList = new ArrayList<>(
                punishments.getNode(punishmentNumber).getList(TypeToken.of(String.class)));
        } catch (ObjectMappingException e) {
            throw new RuntimeException(e);
        }
        for (String cmd : cmdList)
            i.server.getCommandManager()
                .executeAsync(i.server.getConsoleCommandSource(), cmd.replace("{player}", username));
    }
}
