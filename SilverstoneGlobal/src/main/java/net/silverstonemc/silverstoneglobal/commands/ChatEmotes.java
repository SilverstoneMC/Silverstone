package net.silverstonemc.silverstoneglobal.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ChatEmotes implements CommandExecutor {

    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        switch (cmd.getName().toLowerCase()) {
            case "shrug" -> sender.sendMessage(Component.text("Click ")
                    .color(NamedTextColor.GREEN)
                    .append(Component.text("here")
                            .color(NamedTextColor.AQUA)
                            .clickEvent(ClickEvent.runCommand("¯\\_(ツ)_/¯")))
                    .append(Component.text(" to send ¯\\_(ツ)_/¯").color(NamedTextColor.GREEN)));
            case "tableflip" -> sender.sendMessage(Component.text("Click ")
                    .color(NamedTextColor.GREEN)
                    .append(Component.text("here")
                            .color(NamedTextColor.AQUA)
                            .clickEvent(ClickEvent.runCommand("╯°□°）╯︵ ┻━┻")))
                    .append(Component.text(" to send ╯°□°）╯︵ ┻━┻").color(NamedTextColor.GREEN)));
            case "facepalm" -> sender.sendMessage(Component.text("Click ")
                    .color(NamedTextColor.GREEN)
                    .append(Component.text("here")
                            .color(NamedTextColor.AQUA)
                            .clickEvent(ClickEvent.runCommand("./)_-)")))
                    .append(Component.text(" to send ./)_-)").color(NamedTextColor.GREEN)));
        }
        return true;
    }
}
