package me.jasonhorkles.silverstonewarnings;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class TabComplete implements TabCompleter {

    private final JavaPlugin plugin;

    public TabComplete(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    final List<String> arguments = new ArrayList<>();

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("silverstonewarnings")) {
            if (sender.hasPermission("sswarnings.admin")) {
                if (!arguments.contains("reload")) arguments.add("reload");
            } else arguments.remove("reload");
            arguments.add("remove");
            arguments.add("clear");

            List<String> arguments2 = new ArrayList<>();
            if (args[0].equalsIgnoreCase("remove"))
                arguments2.addAll(plugin.getConfig().getConfigurationSection("reasons").getKeys(false));
            else if (args[0].equalsIgnoreCase("clear")) {
                arguments2.add("all");
                arguments2.addAll(plugin.getConfig().getConfigurationSection("reasons").getKeys(false));
            }

            List<String> result = new ArrayList<>();
            switch (args.length) {
                case 1:
                    for (String a : arguments)
                        if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                            result.add(a);
                    break;
                case 2:
                    for (String a : arguments2)
                        if (a.toLowerCase().startsWith(args[1].toLowerCase()))
                            result.add(a);
                    break;
                case 3:
                    return null;
                case 4:
                    result.add("-s");
                    break;
            }

            return result;
        }
        return null;
    }
}