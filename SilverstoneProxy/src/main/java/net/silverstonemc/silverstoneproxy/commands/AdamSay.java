package net.silverstonemc.silverstoneproxy.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class AdamSay extends Command {

    private final Plugin plugin = SilverstoneProxy.getPlugin();
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

    public AdamSay() {
        super("adamsay", "silverstone.console", "asay");
    }

    public void execute(CommandSender sender, String[] args) {
        while (scanner.hasNextLine()) try {
            list.add(scanner.next());
            list.add(scanner.next());
        } catch (NoSuchElementException ignored) {
        }

        String adam = list.get(list.indexOf("31f1cc2b-85aa-4e11-b4cd-0e0a0e2442a8:") + 1).replace("'", "");

        if (args.length > 0) {
            String message = "";
            for (String arg : args) {
                message = message.concat(arg);
                message = message.concat(" ");
            }

            message = message.trim();

            for (ProxiedPlayer player : plugin.getProxy().getPlayers())
                player.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&e[Mod] " + adam + " &7(Console) &3| &e" + message)));
            plugin.getProxy()
                    .getLogger()
                    .info(ChatColor.translateAlternateColorCodes('&', "&e[Mod] " + adam + " &7(Console) &3| &e" + message));
        } else sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: adamsay <message>"));
    }
}
