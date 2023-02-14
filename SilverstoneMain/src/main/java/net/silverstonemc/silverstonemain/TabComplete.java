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
    final List<String> arguments3 = new ArrayList<>();

    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ssm")) {
            if (arguments1.isEmpty()) arguments1.add("reload");

            List<String> result = new ArrayList<>();
            if (args.length == 1) {
                for (String a : arguments1)
                    if (a.toLowerCase().startsWith(args[0].toLowerCase())) result.add(a);
                return result;
            }
        } else if (cmd.getName().equalsIgnoreCase("rtplimit")) {
            if (arguments2.isEmpty()) arguments2.add("check");

            List<String> result = new ArrayList<>();
            if (args.length == 1) {
                for (String a : arguments2)
                    if (a.toLowerCase().startsWith(args[0].toLowerCase())) result.add(a);
                return result;
            }
        } else if (cmd.getName().equalsIgnoreCase("claimpoints")) {
            if (!arguments3.contains("view")) arguments3.add("view");

            if (sender.hasPermission("silverstone.moderator")) {
                if (!arguments3.contains("give")) {
                    arguments3.add("give");
                    arguments3.add("take");
                }
            } else {
                arguments3.remove("give");
                arguments3.remove("take");
            }

            List<String> result = new ArrayList<>();
            if (args.length == 1) {
                for (String a : arguments3)
                    if (a.toLowerCase().startsWith(args[0].toLowerCase())) result.add(a);
                return result;
            }
        }
        return null;
    }

    final List<String> argumentsSell = new ArrayList<>();
    final List<String> argumentsSetHome = new ArrayList<>();

    @EventHandler
    public void tabComplete(AsyncTabCompleteEvent event) {
        if (event.getBuffer().startsWith("/sell")) {
            if (!argumentsSell.contains("all")) argumentsSell.add("all");
            if (!argumentsSell.contains("hand")) argumentsSell.add("hand");
            if (!argumentsSell.contains("handall")) argumentsSell.add("handall");
            event.setCompletions(argumentsSell);

        } else if (event.getBuffer().startsWith("/sethome")) {
            if (!argumentsSetHome.contains("home")) argumentsSetHome.add("home");
            if (!argumentsSetHome.contains("base")) argumentsSetHome.add("base");
            if (!argumentsSetHome.contains("cave")) argumentsSetHome.add("cave");
            if (!argumentsSetHome.contains("village")) argumentsSetHome.add("village");
            if (!argumentsSetHome.contains("temp")) argumentsSetHome.add("temp");
            if (!argumentsSetHome.contains("spawner")) argumentsSetHome.add("spawner");
            event.setCompletions(argumentsSetHome);
        }
    }
}
