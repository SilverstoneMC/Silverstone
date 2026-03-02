package net.silverstonemc.silverstoneglobal;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;

import org.bukkit.GameRule;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
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
    final List<String> argumentsAfk = new ArrayList<>(Arrays.asList(
        "doing school work",
        "eating breakfast",
        "eating lunch",
        "eating dinner",
        "getting a drink",
        "getting a snack"));

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (cmd.getName().toLowerCase()) {
            case "ggamerule" -> {
                if (args.length == 1) return returnResult(
                    args,
                    0,
                    Registry.GAME_RULE.stream().map(gameRule -> gameRule.getKey().getKey())
                        .toArray(String[]::new));

                if (args.length == 2) {
                    List<String> gameRuleValues = new ArrayList<>();
                    GameRule<?> gameRule = Registry.GAME_RULE.get(NamespacedKey.minecraft(args[0]));
                    if (gameRule == null) return new ArrayList<>();
                    if (gameRule.getType() == Boolean.class) gameRuleValues.addAll(Arrays.asList(
                        "true",
                        "false"));
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

    @EventHandler
    public void tabComplete(AsyncTabCompleteEvent event) {
        if (event.getBuffer().startsWith("/afk") && event.getSender().hasPermission("essentials.afk.message"))
            event.setCompletions(argumentsAfk);
    }
}
