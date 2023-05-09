package net.silverstonemc.silverstoneproxy;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;

@SuppressWarnings("DataFlowIssue")
public class WarnPlayer {
    private final SilverstoneProxy plugin = SilverstoneProxy.getPlugin();

    public void warn(UUID uuid, String reason) {
        TextChannel warningChannel = SilverstoneProxy.jda.getTextChannelById(1075643034634563695L);

        String username = new UserManager().getUsername(uuid);
        ProxiedPlayer player = plugin.getProxy().getPlayer(uuid);

        // If player is not online, queue the warning
        if (player == null) {
            ConfigurationManager.queue.set("queue." + uuid, reason);
            new ConfigurationManager().saveQueue();

            // Message staff
            for (ProxiedPlayer online : plugin.getProxy().getPlayers())
                if (online.hasPermission("silverstone.moderator")) online.sendMessage(
                    TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                        "&cOffline player &7" + username + " &chas been warned for reason: &7" + reason)));
            plugin.getLogger().info(ChatColor.translateAlternateColorCodes('&',
                "&cOffline player &7" + username + " &chas been warned for reason: &7" + reason));

            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor(username + " has a warning queued: " + reason, null,
                "https://crafatar.com/avatars/" + uuid + "?overlay=true");
            embed.setColor(new Color(255, 217, 0));
            warningChannel.sendMessageEmbeds(embed.build()).queue();
            return;
        }

        // If player doesn't have the reason in their data, add it
        if (!ConfigurationManager.data.contains("data." + uuid + "." + reason))
            ConfigurationManager.data.set("data." + uuid + "." + reason, 1);
        else { // If they already had the reason, add 1 to it
            int count = ConfigurationManager.data.getInt("data." + uuid + "." + reason);
            ConfigurationManager.data.set("data." + uuid + "." + reason, count + 1);
        }
        new ConfigurationManager().saveData();

        // Grab the number of times the player has been warned
        int warningCount = ConfigurationManager.data.getInt("data." + uuid + "." + reason);

        // Grab the amount of punishments in the config
        int punishmentCount = ConfigurationManager.config.getSection("reasons." + reason + ".add").getKeys()
            .toArray().length;

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
        ArrayList<String> cmdList = new ArrayList<>(
            ConfigurationManager.config.getStringList("reasons." + reason + ".add." + punishmentNumber));
        for (String s : cmdList)
            plugin.getProxy().getPluginManager()
                .dispatchCommand(plugin.getProxy().getConsole(), s.replace("{player}", username));
    }
}
