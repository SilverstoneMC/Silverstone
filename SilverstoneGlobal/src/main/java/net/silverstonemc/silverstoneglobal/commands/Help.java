package net.silverstonemc.silverstoneglobal.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public record Help(JavaPlugin plugin) implements CommandExecutor {
    @SuppressWarnings("DataFlowIssue")
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        // #serverSpecific
        switch (plugin.getConfig().getString("server").toLowerCase()) {
            case "minigames" -> plugin.getServer().dispatchCommand(sender, "htp");
            case "creative" -> sender.sendMessage(Component.text("")
                .append(Component.text("\nCommands:", NamedTextColor.GREEN, TextDecoration.BOLD))

                .append(Component.text("\n/p2 claim ", NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/p2 claim")))
                .append(Component.text("on an unclaimed plot to get started", NamedTextColor.GREEN))

                .append(Component.text("\n/p2 home [#] ", NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/p2 home ")))
                .append(Component.text("to teleport to your plot(s)", NamedTextColor.GREEN))

                .append(Component.text("\n/p2 set ", NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/p2 set ")))
                .append(Component.text("to see multiple plot options", NamedTextColor.GREEN))

                .append(Component.text("\n/p2 merge all ", NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/p2 merge all")))
                .append(Component.text("to merge all your plots together", NamedTextColor.GREEN))

                .append(Component.text("\n/p2 clear ", NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/p2 clear")))
                .append(Component.text("to clear your plot", NamedTextColor.GREEN))

                .append(Component.text("\n/p2 delete ", NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/p2 delete")))
                .append(Component.text("to delete your plot", NamedTextColor.GREEN))

                .append(Component.text("\n/stuck ", NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/stuck")))
                .append(Component.text("if trapped in a plot", NamedTextColor.GREEN))

                .append(Component.text("\n/wesui toggle ", NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/wesui toggle")))
                .append(Component.text("to toggle the selection particles", NamedTextColor.GREEN))

                .append(Component.text("\nLow-effort plots are deleted after 60 days of inactivity.",
                    NamedTextColor.RED, TextDecoration.BOLD)));

            case "survival" -> plugin.getServer().dispatchCommand(sender, "tips");
        }
        return true;
    }
}