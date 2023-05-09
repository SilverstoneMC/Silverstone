package net.silverstonemc.silverstoneproxy.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.silverstonemc.silverstoneproxy.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Warn extends Command implements TabExecutor {
    public Warn() {
        super("warn", "silverstone.moderator", "swarn");
    }

    private final SilverstoneProxy plugin = SilverstoneProxy.getPlugin();

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

                UUID uuid = new UserManager().getUUID(args[0]);
                String username = new UserManager().getUsername(uuid);

                if (uuid == null) {
                    new Utils().nonexistentPlayerMessage(args[0], sender);
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
                UUID uuid = new UserManager().getUUID(args[0]);
                String username = new UserManager().getUsername(uuid);

                if (uuid == null) {
                    new Utils().nonexistentPlayerMessage(args[0], sender);
                    return;
                }

                new WarnReasons().sendReasonList(true, SilverstoneProxy.getAdventure().sender(sender), username);
            }

            case 2 -> {
                // Warn the targeted player
                if (checkIfValidReason(args[1])) {
                    plugin.getProxy().getPluginManager().dispatchCommand(sender, "warn " + args[0]);
                    return;
                }

                UUID uuid = new UserManager().getUUID(args[0]);
                String username = new UserManager().getUsername(uuid);

                if (uuid == null) {
                    new Utils().nonexistentPlayerMessage(args[0], sender);
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
            ConfigurationManager.config.getSection("reasons").getKeys());
        return !reasonList.contains(reason);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> arguments = new ArrayList<>();
        for (ProxiedPlayer player : plugin.getProxy().getPlayers())
            arguments.add(player.getName());

        List<String> arguments2 = new ArrayList<>(ConfigurationManager.config.getSection("reasons").getKeys());

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
