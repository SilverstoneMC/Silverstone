package net.silverstonemc.silverstoneminigames;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter {
    private final List<String> arguments1 = new ArrayList<>();

    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("miniholo")) {
            if (arguments1.isEmpty()) {
                arguments1.add("open");
                arguments1.add("ready");
                arguments1.add("in_session");
                arguments1.add("resetting");
                arguments1.add("closed");
            }

            if (args.length == 1) return new ArrayList<>();
            
            List<String> result = new ArrayList<>();
            if (args.length == 2) {
                for (String a : arguments1)
                    if (a.toLowerCase().startsWith(args[1].toLowerCase())) result.add(a);
                return result;
            }
        }
        return null;
    }
}