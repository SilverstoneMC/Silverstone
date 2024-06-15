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
    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        List<String> result = new ArrayList<>();

        switch (cmd.getName().toLowerCase()) {
            case "miniholo" -> {
                if (args.length == 1) {
                    Collection<Hologram> holograms = FancyHologramsPlugin.get().getHologramManager()
                        .getHolograms();
                    List<String> arguments = new ArrayList<>();
                    holograms.forEach(hologram -> arguments.add(hologram.getData().getName()));

                    for (String a : arguments)
                        if (a.toLowerCase().startsWith(args[0].toLowerCase())) result.add(a);
                    return result;
                }

                if (args.length == 2) {
                    List<String> arguments = new ArrayList<>(List.of("open",
                        "ready",
                        "in_session",
                        "resetting",
                        "closed"));

                    for (String a : arguments)
                        if (a.toLowerCase().startsWith(args[1].toLowerCase())) result.add(a);
                    return result;
                }
            }

            case "corruptedtag" -> {
                if (args.length == 1) {
                    List<String> arguments = new ArrayList<>(List.of("start",
                        "stop",
                        "corrupt",
                        "uncorrupt",
                        "kitselect",
                        "closeinv"));

                    for (String a : arguments)
                        if (a.toLowerCase().startsWith(args[0].toLowerCase())) result.add(a);
                    return result;
                }
            }
        }
        return null;
    }
}