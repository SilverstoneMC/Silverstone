package net.silverstonemc.silverstoneglobal.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class GlobalGameRule implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length < 2) return false;

        GameRule<?> gameRule = GameRule.getByName(args[0]);
        if (gameRule == null) return false;

        try {
            setGameRule(gameRule, args[1], sender);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Invalid integer!", NamedTextColor.RED));
            return true;
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text(e.getMessage(), NamedTextColor.RED));
            return true;
        }

        return true;
    }

    private <T> void setGameRule(GameRule<T> rule, String value, CommandSender sender) {
        T parsedValue = parseValue(rule.getType(), value);
        for (World world : Bukkit.getWorlds())
            sendMessage(
                sender,
                world.setGameRule(rule, parsedValue),
                rule,
                String.valueOf(parsedValue),
                world);
    }

    @SuppressWarnings("unchecked")
    private <T> T parseValue(Class<T> type, String value) {
        if (type == Boolean.class) return (T) Boolean.valueOf(value);
        else if (type == Integer.class) return (T) Integer.valueOf(value);
        else throw new IllegalArgumentException("Invalid game rule type! (" + type + ")");
    }

    private void sendMessage(CommandSender sender, boolean success, GameRule<?> gameRule, String value, World world) {
        String resultMessage = success ? "Set " + gameRule.getName() + " to " + value + " in world " + world.getName() + "!" : "Failed to set " + gameRule.getName() + " to " + value + " in world " + world.getName() + "!";

        NamedTextColor color = success ? NamedTextColor.GREEN : NamedTextColor.RED;
        sender.sendMessage(Component.text(resultMessage, color));
    }
}
