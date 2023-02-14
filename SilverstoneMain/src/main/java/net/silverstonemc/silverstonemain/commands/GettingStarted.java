package net.silverstonemc.silverstonemain.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class GettingStarted implements CommandExecutor {
    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        sender.sendMessage(Component.text("\nGETTING STARTED").color(TextColor.fromHexString("#18d958"))
            .decorate(TextDecoration.BOLD));

        sender.sendMessage(Component.text("-----------------").color(TextColor.fromHexString("#305abf")));

        sender.sendMessage(Component.text().append(
                Component.text("Type ").color(TextColor.fromHexString("#18c1c7"))
                    .decoration(TextDecoration.BOLD, false)).append(
                Component.text("/warps").color(TextColor.fromHexString("#305abf"))
                    .clickEvent(ClickEvent.suggestCommand("/warps")))
            .append(Component.text(" to see all available warps, ").color(TextColor.fromHexString("#18c1c7")))
            .append(Component.text("/server").color(TextColor.fromHexString("#305abf"))
                .clickEvent(ClickEvent.suggestCommand("/server"))).append(
                Component.text(" to see all available servers, and ")
                    .color(TextColor.fromHexString("#18c1c7"))).append(
                Component.text("/tips").color(TextColor.fromHexString("#305abf"))
                    .clickEvent(ClickEvent.suggestCommand("/tips"))).append(
                Component.text(" for tips about the network.").color(TextColor.fromHexString("#18c1c7"))));
        return true;
    }
}
