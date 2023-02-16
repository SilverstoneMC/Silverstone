package net.silverstonemc.silverstoneproxy.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;

import java.util.ArrayList;
import java.util.List;

public class BaseCommand extends Command implements TabExecutor {
    public BaseCommand() {
        super("ssp", "silverstone.admin");
    }
    
    private final List<String> arguments = new ArrayList<>();
    private final SilverstoneProxy plugin = SilverstoneProxy.getPlugin();

    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) if (args[0].equalsIgnoreCase("reload")) {
            SilverstoneProxy.config = plugin.loadConfig();

            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "SilverstoneProxy reloaded!"));
        } else sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: /ssp reload"));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (!arguments.contains("reload")) arguments.add("reload");

        List<String> result = new ArrayList<>();
        if (args.length == 1) for (String a : arguments)
            if (a.toLowerCase().startsWith(args[0].toLowerCase())) result.add(a);
        return result;
    }
}
