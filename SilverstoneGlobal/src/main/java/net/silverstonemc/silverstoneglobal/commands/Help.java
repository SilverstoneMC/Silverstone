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

public class Help implements CommandExecutor {
    public Help(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        // #serverSpecific
        //noinspection DataFlowIssue
        switch (plugin.getConfig().getString("server").toLowerCase()) {
            case "minigames" -> plugin.getServer().dispatchCommand(sender, "htp");

            case "creative" -> sender.sendMessage(Component.empty().append(
                Component.text("\nCommands:", NamedTextColor.GREEN, TextDecoration.BOLD),

                Component.text("\n/p claim ", NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.runCommand("/p claim")),
                Component.text("on an unclaimed plot to get started", NamedTextColor.GREEN),

                Component.text("\n/p home [#] ", NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.suggestCommand("/p home ")),
                Component.text("to teleport to your plot(s)", NamedTextColor.GREEN),

                Component.text("\n/p set ", NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.suggestCommand("/p set ")),
                Component.text("to see multiple plot options", NamedTextColor.GREEN),

                Component.text("\n/p merge all ", NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.suggestCommand("/p merge all")),
                Component.text("to merge all your plots together", NamedTextColor.GREEN),

                Component.text("\n/p clear ", NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.suggestCommand("/p clear")),
                Component.text("to clear your plot", NamedTextColor.GREEN),

                Component.text("\n/p delete ", NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.suggestCommand("/p delete")),
                Component.text("to delete your plot", NamedTextColor.GREEN),

                Component.text("\n/stuck ", NamedTextColor.AQUA).clickEvent(ClickEvent.runCommand("/stuck")),
                Component.text("if trapped in a plot", NamedTextColor.GREEN),

                Component.text("\n/wesui toggle ", NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.runCommand("/wesui toggle")),

                Component.text("to toggle the selection particles", NamedTextColor.GREEN),
                Component.text(
                    "\nLow-effort plots are deleted after 60 days of inactivity.",
                    NamedTextColor.RED,
                    TextDecoration.BOLD)));

            case "survival", "events" -> plugin.getServer().dispatchCommand(sender, "tips");
        }
        return true;
    }
}