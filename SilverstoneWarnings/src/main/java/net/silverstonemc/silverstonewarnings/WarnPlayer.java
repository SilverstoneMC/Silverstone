package net.silverstonemc.silverstonewarnings;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.Button;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;

@SuppressWarnings("DataFlowIssue")
public class WarnPlayer {

    private final SilverstoneWarnings plugin = SilverstoneWarnings.getPlugin();

    public void warn(UUID uuid, String reason) {
        TextChannel warningChannel = SilverstoneWarnings.jda.getTextChannelById(826540656783523850L);

        String username = plugin.getPlayerName(uuid);
        ProxiedPlayer player = plugin.getOnlinePlayer(uuid);

        // If player is not online, queue the warning
        if (player == null) {
            SilverstoneWarnings.queue.set("queue." + uuid, reason);
            plugin.saveQueue();

            // Message staff
            for (ProxiedPlayer online : plugin.getProxy().getPlayers())
                if (online.hasPermission("sswarnings.warn"))
                    online.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&cOffline player &7" + username + " &chas been warned for reason: &7" + reason)));
            plugin.getLogger()
                    .info(ChatColor.translateAlternateColorCodes('&', "&cOffline player &7" + username + " &chas been warned for reason: &7" + reason));

            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor(username + " has a warning queued: " + reason, null, "https://crafatar.com/avatars/" + uuid + "?overlay=true");
            embed.setColor(new Color(255, 217, 0));
            warningChannel.sendMessageEmbeds(embed.build()).queue();
            return;
        }

        // If player doesn't have the reason in their data, add it
        if (!SilverstoneWarnings.data.contains("data." + uuid + "." + reason))
            SilverstoneWarnings.data.set("data." + uuid + "." + reason, 1);
        else { // If they already had the reason, add 1 to it
            int count = SilverstoneWarnings.data.getInt("data." + uuid + "." + reason);
            SilverstoneWarnings.data.set("data." + uuid + "." + reason, count + 1);
        }
        plugin.saveData();

        // Grab the number of times the player has been warned
        int warningCount = SilverstoneWarnings.data.getInt("data." + uuid + "." + reason);

        // Grab the amount of punishments in the config
        int punishmentCount = SilverstoneWarnings.config.getSection("reasons." + reason + ".add")
                .getKeys()
                .toArray().length;

        // Get the correct warning number
        int punishmentNumber = warningCount % punishmentCount;
        if (punishmentNumber == 0) punishmentNumber = punishmentCount;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(username + " was warned for reason: " + reason + " | Warning " + punishmentNumber + "/" + punishmentCount, null, "https://crafatar.com/avatars/" + uuid + "?overlay=true");
        embed.setColor(new Color(204, 27, 53));
        warningChannel.sendMessageEmbeds(embed.build())
                .setActionRow(
                        Button.success("removewarning: " + reason + " :" + uuid, "Undo '" + reason + "' warning")
                                .withEmoji(Emoji.fromUnicode("↩")),
                        Button.primary("removewarningsilently: " + reason + " :" + uuid, "Undo '" + reason + "' warning silently")
                                .withEmoji(Emoji.fromUnicode("↩")),
                        Button.secondary("clearwarning: " + reason + " :" + uuid, "Clear all '" + reason + "' warnings")
                                .withEmoji(Emoji.fromUnicode("➖")),
                        Button.danger("clearallwarnings:" + uuid, "Clear all of " + username + "'s warnings")
                                .withEmoji(Emoji.fromUnicode("❌")))
                .queue();

        // Warn the player
        ArrayList<String> cmdList = new ArrayList<>(SilverstoneWarnings.config.getStringList("reasons." + reason + ".add." + punishmentNumber));
        for (String s : cmdList)
            plugin.getProxy()
                    .getPluginManager()
                    .dispatchCommand(plugin.getProxy().getConsole(), s.replace("{player}", username));
    }
}
