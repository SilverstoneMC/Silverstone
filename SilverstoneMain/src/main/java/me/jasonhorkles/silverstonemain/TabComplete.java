package me.jasonhorkles.silverstonemain;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter, Listener {

    final List<String> arguments = new ArrayList<>();

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("silverstonemain")) {
            if (arguments.isEmpty()) arguments.add("reload");

            List<String> result = new ArrayList<>();
            if (args.length == 1) {
                for (String a : arguments) if (a.toLowerCase().startsWith(args[0].toLowerCase())) result.add(a);
                return result;
            }
        }
        return null;
    }

    @EventHandler
    public void tabComplete(AsyncTabCompleteEvent event) {
        if (event.getBuffer().startsWith("/afk")) {
            if (event.getSender().hasPermission("essentials.afk.message")) {
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
        } else if (event.getBuffer().startsWith("/sell")) {
            List<String> completions = new ArrayList<>();
            completions.add("all");
            completions.add("hand");
            completions.add("handall");
            event.setCompletions(completions);
        }
    }
}
