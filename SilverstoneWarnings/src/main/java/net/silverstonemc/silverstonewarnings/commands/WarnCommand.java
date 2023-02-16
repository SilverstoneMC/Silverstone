package net.silverstonemc.silverstonewarnings.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.silverstonemc.silverstonewarnings.SilverstoneWarnings;
import net.silverstonemc.silverstonewarnings.WarnPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarnCommand extends Command implements TabExecutor {
    public WarnCommand() {
        super("warn", "silverstone.moderator", "swarn");
    }

    private final SilverstoneWarnings plugin = SilverstoneWarnings.getPlugin();

    public void execute(CommandSender sender, String[] args) {
        // If console does the command
        if (!(sender instanceof ProxiedPlayer)) switch (args.length) {
            case 0, 1 ->
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "/warn <player> <reason>"));

            case 2 -> {
                // Warn the targeted player
                if (checkIfValidReason(args[1])) {
                    plugin.getProxy().getPluginManager()
                        .dispatchCommand(plugin.getProxy().getConsole(), "reasons");
                    return;
                }

                UUID uuid = plugin.getPlayerUUID(args[0]);
                String username = plugin.getPlayerName(uuid);

                if (uuid == null) {
                    plugin.nonexistentPlayerMessage(args[0], sender);
                    return;
                }

                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                    "&cWarning &7" + username + " &cfor reason: &7" + args[1])));
                new WarnPlayer().warn(uuid, args[1]);
            }
        }
        else switch (args.length) {
            case 0 ->
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "/warn <player> <reason>"));

            case 1 -> {
                UUID uuid = plugin.getPlayerUUID(args[0]);
                String username = plugin.getPlayerName(uuid);

                if (uuid == null) {
                    plugin.nonexistentPlayerMessage(args[0], sender);
                    return;
                }

                new ReasonsCommand().sendReasonList(true, sender, username);
            }

            case 2 -> {
                // Warn the targeted player
                if (checkIfValidReason(args[1])) {
                    plugin.getProxy().getPluginManager().dispatchCommand(sender, "warn " + args[0]);
                    return;
                }

                UUID uuid = plugin.getPlayerUUID(args[0]);
                String username = plugin.getPlayerName(uuid);

                if (uuid == null) {
                    plugin.nonexistentPlayerMessage(args[0], sender);
                    return;
                }

                TextComponent blank = new TextComponent();
                for (int x = 0; x < 50; x++) sender.sendMessage(blank);
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                    "&cWarning &7" + username + " &cfor reason: &7" + args[1])));
                new WarnPlayer().warn(uuid, args[1]);
            }
        }
    }

    private boolean checkIfValidReason(String reason) {
        ArrayList<String> reasonList = new ArrayList<>(
            SilverstoneWarnings.config.getSection("reasons").getKeys());
        return !reasonList.contains(reason);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> arguments = new ArrayList<>();
        for (ProxiedPlayer player : plugin.getProxy().getPlayers())
            arguments.add(player.getName());

        List<String> arguments2 = new ArrayList<>(SilverstoneWarnings.config.getSection("reasons").getKeys());

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
        return result;
    }
}
