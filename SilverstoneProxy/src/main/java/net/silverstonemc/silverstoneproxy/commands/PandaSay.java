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

public class PandaSay extends Command {

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

    public PandaSay() {
        super("pandasay", "silverstone.console");
    }

    public void execute(CommandSender sender, String[] args) {
        while (scanner.hasNextLine()) try {
            list.add(scanner.next());
            list.add(scanner.next());
        } catch (NoSuchElementException ignored) {
        }

        String panda = list.get(list.indexOf("75fb05a2-9d9e-49cb-be34-6bd5215548ba:") + 1).replace("'", "");

        if (args.length > 0) {
            String message = "";
            for (String arg : args) {
                message = message.concat(arg);
                message = message.concat(" ");
            }

            message = message.trim();

            for (ProxiedPlayer player : plugin.getProxy().getPlayers())
                player.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&d[Admin] " + panda + " &7(Console) &3| &e" + message)));
            plugin.getProxy()
                    .getLogger()
                    .info(ChatColor.translateAlternateColorCodes('&', "&d[Admin] " + panda + " &7(Console) &3| &e" + message));
        } else sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: pandasay <message>"));
    }
}
