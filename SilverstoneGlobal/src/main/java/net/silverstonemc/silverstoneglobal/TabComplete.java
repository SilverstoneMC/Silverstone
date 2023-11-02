package net.silverstonemc.silverstoneglobal;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import org.bukkit.GameRule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabComplete implements TabCompleter, Listener {
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (cmd.getName().toLowerCase()) {
            case "ggamerule" -> {
                if (args.length == 1) return returnResult(args, 0,
                    Arrays.stream(GameRule.values()).map(GameRule::getName).toArray(String[]::new));

                if (args.length == 2) {
                    List<String> gameRuleValues = new ArrayList<>();
                    GameRule<?> gameRule = GameRule.getByName(args[0]);
                    if (gameRule == null) return new ArrayList<>();
                    if (gameRule.getType() == Boolean.class)
                        gameRuleValues.addAll(Arrays.asList("true", "false"));
                    else if (gameRule.getType() == Integer.class) gameRuleValues.add("<integer>");

                    return returnResult(args, 1, gameRuleValues.toArray(String[]::new));
                }
            }
        }

        return null;
    }

    private List<String> returnResult(String[] args, int index, String... arguments) {
        List<String> result = new ArrayList<>();
        for (String a : arguments)
            if (a.toLowerCase().startsWith(args[index].toLowerCase())) result.add(a);
        return result;
    }

    final List<String> argumentsAfk = new ArrayList<>();

    @EventHandler
    public void tabComplete(AsyncTabCompleteEvent event) {
        if (event.getBuffer().startsWith("/afk"))
            if (event.getSender().hasPermission("essentials.afk.message")) {
                if (!argumentsAfk.contains("doing school work")) argumentsAfk.add("doing school work");
                if (!argumentsAfk.contains("eating breakfast")) argumentsAfk.add("eating breakfast");
                if (!argumentsAfk.contains("eating lunch")) argumentsAfk.add("eating lunch");
                if (!argumentsAfk.contains("eating dinner")) argumentsAfk.add("eating dinner");
                if (!argumentsAfk.contains("getting a drink")) argumentsAfk.add("getting a drink");
                if (!argumentsAfk.contains("getting a snack")) argumentsAfk.add("getting a snack");
                event.setCompletions(argumentsAfk);
            }
    }
}
