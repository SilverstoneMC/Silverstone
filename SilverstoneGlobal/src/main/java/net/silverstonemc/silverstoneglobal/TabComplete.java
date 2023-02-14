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
                if (a.toLowerCase().startsWith(args[0].toLowerCase())) result.add(a);
            return result;
        }
        return null;
    }

    final List<String> argumentsAfk = new ArrayList<>();

    @EventHandler
    public void tabComplete(AsyncTabCompleteEvent event) {
        if (event.getBuffer().startsWith("/afk")) if (event.getSender().hasPermission("essentials.afk.message")) {
            if (!argumentsAfk.contains("doing school work")) argumentsAfk.add("doing school work");
            if (!argumentsAfk.contains("eating breakfast")) argumentsAfk.add("eating breakfast");
            if (!argumentsAfk.contains("eating lunch")) argumentsAfk.add("eating lunch");
            if (!argumentsAfk.contains("eating dinner")) argumentsAfk.add("eating dinner");
            if (!argumentsAfk.contains("getting a drink")) argumentsAfk.add("getting a drink");
            if (!argumentsAfk.contains("getting a snack")) argumentsAfk.add("getting a snack");
            if (!argumentsAfk.contains("getting ready for the day")) argumentsAfk.add("getting ready for the day");
            if (!argumentsAfk.contains("getting ready for bed")) argumentsAfk.add("getting ready for bed");
            if (!argumentsAfk.contains("walking the dog")) argumentsAfk.add("walking the dog");
            if (!argumentsAfk.contains("with a nose bleed")) argumentsAfk.add("with a nose bleed");
            event.setCompletions(argumentsAfk);
        }
    }
}
