package net.silverstonemc.silverstoneglobal;

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

    final List<String> arguments = new ArrayList<>();

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (arguments.isEmpty()) arguments.add("reload");

        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (String a : arguments)
                if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                    result.add(a);
            return result;
        }
        return null;
    }

    @EventHandler
    public void tabComplete(AsyncTabCompleteEvent event) {
        if (event.getBuffer().startsWith("/afk")) if (event.getSender().hasPermission("essentials.afk.message")) {
            List<String> completions = new ArrayList<>();
            completions.add("doing school work");
            completions.add("eating breakfast");
            completions.add("eating lunch");
            completions.add("eating dinner");
            completions.add("getting a drink");
            completions.add("getting a snack");
            completions.add("getting ready for the day");
            completions.add("getting ready for bed");
            completions.add("walking the dog");
            completions.add("with a nose bleed");
            event.setCompletions(completions);
        }
    }
}
