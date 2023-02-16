package net.silverstonemc.silverstonewarnings.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.silverstonemc.silverstonewarnings.SilverstoneWarnings;
import net.silverstonemc.silverstonewarnings.UndoWarning;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("DataFlowIssue")
public class BaseCommand extends Command implements TabExecutor {
    public BaseCommand() {
        super("ssw", "silverstone.moderator");
    }

    private final SilverstoneWarnings plugin = SilverstoneWarnings.getPlugin();
    
    public void execute(CommandSender sender, String[] args) {
        TextChannel warningChannel = SilverstoneWarnings.jda.getTextChannelById(826540656783523850L);

        // Reload
        if (args.length > 0)
            if (args[0].equalsIgnoreCase("reload")) if (sender.hasPermission("silverstone.admin")) {
                SilverstoneWarnings.config = plugin.loadFile("config.yml");
                SilverstoneWarnings.data = plugin.loadFile("data.yml");
                SilverstoneWarnings.queue = plugin.loadFile("queue.yml");
                SilverstoneWarnings.userCache = plugin.loadFile("usercache.yml");

                sender.sendMessage(
                    TextComponent.fromLegacyText(ChatColor.GREEN + "SilverstoneWarnings reloaded!"));
            } else sender.sendMessage(
                TextComponent.fromLegacyText(ChatColor.RED + "You don't have permission to do that!"));

                // remove <reason> <player>
            else if (args[0].equalsIgnoreCase("remove")) {
                if (args.length < 3) {
                    sender.sendMessage(
                        TextComponent.fromLegacyText(ChatColor.RED + "/ssw remove <reason> <player>"));
                    return;
                }

                UUID uuid = plugin.getPlayerUUID(args[2]);
                String username = plugin.getPlayerName(uuid);
                ProxiedPlayer player = plugin.getOnlinePlayer(uuid);

                if (uuid == null) {
                    plugin.nonexistentPlayerMessage(args[2], sender);
                    return;
                }

                int count = SilverstoneWarnings.data.getInt("data." + uuid + "." + args[1]);

                // Already has 0 warnings
                if ((count - 1) < 0) sender.sendMessage(TextComponent.fromLegacyText(
                    ChatColor.translateAlternateColorCodes('&',
                        "&7" + username + " &calready has 0 &7" + args[1] + " &cwarnings.")));
                else {
                    new UndoWarning().undoWarning(uuid, args[1], count);

                    if ((count - 1) == 0) SilverstoneWarnings.data.set("data." + uuid + "." + args[1], null);
                    else SilverstoneWarnings.data.set("data." + uuid + "." + args[1], count - 1);
                    plugin.saveData();

                    if (SilverstoneWarnings.data.getSection("data." + uuid).getKeys().isEmpty()) {
                        SilverstoneWarnings.data.set("data." + uuid, null);
                        plugin.saveData();
                    }

                    plugin.getLogger().info(ChatColor.translateAlternateColorCodes('&',
                        "&7" + sender.getName() + " &cremoved a &7" + args[1] + " &cwarning from &7" + username));

                    // Message staff
                    for (ProxiedPlayer online : plugin.getProxy().getPlayers())
                        if (online.hasPermission("sswarnings.warn")) online.sendMessage(
                            TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                                "&7" + sender.getName() + " &cremoved a &7" + args[1] + " &cwarning from &7" + username)));

                    // Message player if online and command not silent
                    try {
                        if (!args[3].equalsIgnoreCase("-s")) if (player != null) player.sendMessage(
                            TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                                "&7" + sender.getName() + " &cremoved a &7" + args[1] + " &cwarning from you.")));
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                        if (player != null) player.sendMessage(TextComponent.fromLegacyText(
                            ChatColor.translateAlternateColorCodes('&',
                                "&7" + sender.getName() + " &cremoved a &7" + args[1] + " &cwarning from you.")));
                    }

                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor(
                        sender.getName() + " removed a '" + args[1] + "' warning from " + username, null,
                        "https://crafatar.com/avatars/" + uuid + "?overlay=true");
                    embed.setColor(new Color(42, 212, 85));
                    warningChannel.sendMessageEmbeds(embed.build()).queue();
                }


                // clear <[reason]|all> <player>
            } else if (args[0].equalsIgnoreCase("clear")) {
                if (args.length < 3) {
                    sender.sendMessage(
                        TextComponent.fromLegacyText(ChatColor.RED + "/ssw clear <[reason]|all> <player>"));
                    return;
                }

                UUID uuid = plugin.getPlayerUUID(args[2]);
                String username = plugin.getPlayerName(uuid);
                ProxiedPlayer player = plugin.getOnlinePlayer(uuid);

                if (uuid == null) {
                    plugin.nonexistentPlayerMessage(args[2], sender);
                    return;
                }

                // If all
                if (args[1].equalsIgnoreCase("all")) {
                    new UndoWarning().undoWarning(uuid, args[1], null);

                    SilverstoneWarnings.data.set("data." + uuid, null);
                    plugin.saveData();

                    plugin.getLogger().info(ChatColor.translateAlternateColorCodes('&',
                        "&7" + sender.getName() + " &ccleared all of &7" + username + "'s &cwarnings"));

                    // Message staff
                    for (ProxiedPlayer online : plugin.getProxy().getPlayers())
                        if (online.hasPermission("sswarnings.warn")) online.sendMessage(
                            TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                                "&7" + sender.getName() + " &ccleared all of &7" + username + "'s &cwarnings")));

                    // Message player if online and command not silent
                    try {
                        if (!args[3].equalsIgnoreCase("-s")) if (player != null) player.sendMessage(
                            TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                                "&7" + sender.getName() + " &ccleared all your warnings.")));
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                        if (player != null) player.sendMessage(TextComponent.fromLegacyText(
                            ChatColor.translateAlternateColorCodes('&',
                                "&7" + sender.getName() + " &ccleared all your warnings.")));
                    }

                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor(sender.getName() + " cleared all of " + username + "'s warnings", null,
                        "https://crafatar.com/avatars/" + uuid + "?overlay=true");
                    embed.setColor(new Color(42, 212, 85));
                    warningChannel.sendMessageEmbeds(embed.build()).queue();

                    // If a reason is defined
                } else {
                    new UndoWarning().undoWarning(uuid, args[1], null);

                    SilverstoneWarnings.data.set("data." + uuid + "." + args[1], null);
                    plugin.saveData();

                    plugin.getLogger().info(ChatColor.translateAlternateColorCodes('&',
                        "&7" + sender.getName() + " &ccleared all &7" + args[1] + " &cwarnings from &7" + username));

                    // Message staff
                    for (ProxiedPlayer online : plugin.getProxy().getPlayers())
                        if (online.hasPermission("sswarnings.warn")) online.sendMessage(
                            TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                                "&7" + sender.getName() + " &ccleared all &7" + args[1] + " &cwarnings from &7" + username)));

                    // Message player if online and command not silent
                    try {
                        if (!args[3].equalsIgnoreCase("-s")) if (player != null) player.sendMessage(
                            TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                                "&7" + sender.getName() + " &ccleared all your &7" + args[1] + " &cwarnings.")));
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                        if (player != null) player.sendMessage(TextComponent.fromLegacyText(
                            ChatColor.translateAlternateColorCodes('&',
                                "&7" + sender.getName() + " &ccleared all your &7" + args[1] + " &cwarnings.")));
                    }

                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor(
                        sender.getName() + " cleared all '" + args[1] + "' warnings from " + username, null,
                        "https://crafatar.com/avatars/" + uuid + "?overlay=true");
                    embed.setColor(new Color(42, 212, 85));
                    warningChannel.sendMessageEmbeds(embed.build()).queue();
                }
            }
    }

    final List<String> arguments = new ArrayList<>();

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender.hasPermission("silverstone.admin")) {
            if (!arguments.contains("reload")) arguments.add("reload");
        } else arguments.remove("reload");

        if (!arguments.contains("remove")) arguments.add("remove");
        if (!arguments.contains("clear")) arguments.add("clear");

        List<String> arguments2 = new ArrayList<>();
        if (args[0].equalsIgnoreCase("remove"))
            arguments2.addAll(SilverstoneWarnings.config.getSection("reasons").getKeys());
        else if (args[0].equalsIgnoreCase("clear")) {
            arguments2.add("all");
            arguments2.addAll(SilverstoneWarnings.config.getSection("reasons").getKeys());
        }

        List<String> arguments3 = new ArrayList<>();
        for (ProxiedPlayer player : plugin.getProxy().getPlayers())
            arguments3.add(player.getName());

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
        return result;
    }
}
