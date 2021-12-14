package net.silverstonemc.silverstoneproxy.commands;

import net.silverstonemc.silverstoneproxy.SilverstoneProxy;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Say extends Command {

    private final Plugin plugin = SilverstoneProxy.getInstance();
    private final File displaynames = new File("/home/container/plugins/BungeeDisplayName/players.yml");
    private final ArrayList<String> list = new ArrayList<>();
    private Scanner scanner;

    {
        try {
            scanner = new Scanner(displaynames);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Say() {
        super("say", "silverstone.console");
    }

    public void execute(CommandSender sender, String[] args) {
        while (scanner.hasNextLine()) try {
            list.add(scanner.next());
            list.add(scanner.next());
        } catch (NoSuchElementException ignored) {
        }

        String jason = list.get(list.indexOf("a28173af-f0a9-47fe-8549-19c6bccf68da:") + 1).replace("'", "");

        if (args.length > 0) {
            String message = "";
            for (String arg : args) {
                message = message.concat(arg);
                message = message.concat(" ");
            }

            message = message.trim();

            for (ProxiedPlayer player : plugin.getProxy().getPlayers())
                player.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&9&l[Owner] " + jason + " &7(Console) &3| &b" + message)));
            plugin.getProxy()
                    .getLogger()
                    .info(ChatColor.translateAlternateColorCodes('&', "&9&l[Owner] " + jason + " &7(Console) &3| &b" + message));
        } else sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: say <message>"));
    }
}
