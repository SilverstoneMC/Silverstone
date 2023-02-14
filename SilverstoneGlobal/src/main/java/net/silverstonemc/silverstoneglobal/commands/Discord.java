package net.silverstonemc.silverstoneglobal.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Discord implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        sender.sendMessage(Component.text("Join the Discord server at ").color(NamedTextColor.DARK_AQUA)
            .append(Component.text("discord.gg/VVSUEPd").color(NamedTextColor.GREEN)
                .clickEvent(ClickEvent.openUrl("https://discord.gg/VVSUEPd"))));
        return true;
    }
}
