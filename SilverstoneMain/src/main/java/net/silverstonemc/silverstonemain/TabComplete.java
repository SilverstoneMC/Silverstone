package net.silverstonemc.silverstonemain;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter, Listener {
    private final List<String> arguments1 = new ArrayList<>();

    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ssm")) {
            if (arguments1.isEmpty()) arguments1.add("reload");

            List<String> result = new ArrayList<>();
            if (args.length == 1) {
                for (String a : arguments1)
                    if (a.toLowerCase().startsWith(args[0].toLowerCase())) result.add(a);
                return result;
            }
        }
        return null;
    }
}
