package net.silverstonemc.silverstonewarnings.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.silverstonemc.silverstonewarnings.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarningsCommand extends Command implements TabExecutor {
    public WarningsCommand() {
        super("warnings");
    }

    private final SilverstoneWarnings plugin = SilverstoneWarnings.getPlugin();

    public void execute(CommandSender sender, String[] args) {
        String arg0;
        // If no player specified
        if (args.length < 1) {
            // If console didn't specify a player
            if (!(sender instanceof ProxiedPlayer)) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Please specify a player!"));
                return;
            }
            arg0 = sender.getName();
            // If player specified but sender has no permission
        } else if (!sender.hasPermission("silverstone.moderator")) arg0 = sender.getName();
            // If player specified and sender does have permission
        else arg0 = args[0];

        UUID uuid = new UserManager().getUUID(arg0);
        String username = new UserManager().getUsername(uuid);

        if (uuid == null) {
            new Utils().nonexistentPlayerMessage(arg0, sender);
            return;
        }

        // If not in queue
        if (!ConfigurationManager.queue.contains("queue." + uuid))
            // And has no warnings
            if (!ConfigurationManager.data.contains("data." + uuid)) {
                sender.sendMessage(TextComponent.fromLegacyText(
                    ChatColor.translateAlternateColorCodes('&', "&b" + username + " &ahas no warnings!")));
                return;
            }

        sender.sendMessage(TextComponent.fromLegacyText(
            ChatColor.translateAlternateColorCodes('&', "&c&l" + username + "'s warnings:")));

        // If any warnings already exist
        if (ConfigurationManager.data.contains("data." + uuid))
            for (String reasonList : ConfigurationManager.data.getSection("data." + uuid).getKeys())
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                    "&7" + reasonList + " - " + ConfigurationManager.data.getInt(
                        "data." + uuid + "." + reasonList))));

        // If in queue
        if (ConfigurationManager.queue.contains("queue." + uuid)) sender.sendMessage(
            TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                "&7" + ConfigurationManager.queue.getString("queue." + uuid) + " (Queued)")));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (!sender.hasPermission("silverstone.moderator")) return new ArrayList<>();

        List<String> arguments = new ArrayList<>();
        for (ProxiedPlayer player : plugin.getProxy().getPlayers())
            arguments.add(player.getName());

        List<String> result = new ArrayList<>();
        for (String a : arguments)
            if (a.toLowerCase().startsWith(args[0].toLowerCase())) result.add(a);
        return result;
    }
}
