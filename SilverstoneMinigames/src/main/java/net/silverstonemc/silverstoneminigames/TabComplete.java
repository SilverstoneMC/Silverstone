package net.silverstonemc.silverstoneminigames;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.Hologram;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TabComplete implements TabCompleter {
    private final List<String> arguments2 = new ArrayList<>();

    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("miniholo")) {
            List<String> result = new ArrayList<>();

            if (args.length == 1) {
                Collection<Hologram> holograms = FancyHologramsPlugin.get().getHologramManager()
                    .getHolograms();
                List<String> arguments1 = new ArrayList<>();
                holograms.forEach(hologram -> arguments1.add(hologram.getData().getName()));

                for (String a : arguments1)
                    if (a.toLowerCase().startsWith(args[0].toLowerCase())) result.add(a);
                return result;
            }

            if (args.length == 2) {
                if (arguments2.isEmpty()) {
                    arguments2.add("open");
                    arguments2.add("ready");
                    arguments2.add("in_session");
                    arguments2.add("resetting");
                    arguments2.add("closed");
                }

                for (String a : arguments2)
                    if (a.toLowerCase().startsWith(args[1].toLowerCase())) result.add(a);
                return result;
            }
        }
        return null;
    }
}