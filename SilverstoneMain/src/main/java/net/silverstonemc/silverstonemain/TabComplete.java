package net.silverstonemc.silverstonemain;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter, Listener {

    final List<String> arguments1 = new ArrayList<>();
    final List<String> arguments2 = new ArrayList<>();

    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("silverstonemain")) {
            if (arguments1.isEmpty()) arguments1.add("reload");

            List<String> result = new ArrayList<>();
            if (args.length == 1) {
                for (String a : arguments1) if (a.toLowerCase().startsWith(args[0].toLowerCase())) result.add(a);
                return result;
            }
        } else if (cmd.getName().equalsIgnoreCase("rtplimit")) {
            if (arguments2.isEmpty()) arguments2.add("check");

            List<String> result = new ArrayList<>();
            if (args.length == 1) {
                for (String a : arguments2) if (a.toLowerCase().startsWith(args[0].toLowerCase())) result.add(a);
                return result;
            }
        }
        return null;
    }

    @EventHandler
    public void tabComplete(AsyncTabCompleteEvent event) {
        if (event.getBuffer().startsWith("/sell")) {
            List<String> completions = new ArrayList<>();
            completions.add("all");
            completions.add("hand");
            completions.add("handall");
            event.setCompletions(completions);
        }
    }
}
