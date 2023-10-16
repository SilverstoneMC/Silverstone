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
        sender.sendMessage(
            Component.text("\nGETTING STARTED", TextColor.fromHexString("#18d958"), TextDecoration.BOLD));

        sender.sendMessage(Component.text("-----------------", TextColor.fromHexString("#305abf")));

        sender.sendMessage(Component.text("/htp", TextColor.fromHexString("#305abf"))
            .clickEvent(ClickEvent.runCommand("/htp"))
            .append(Component.text(" - Open the How to Play menu\n", TextColor.fromHexString("#18c1c7")))
            .append(Component.text("/warps", TextColor.fromHexString("#305abf"))
                .clickEvent(ClickEvent.runCommand("/warps")))
            .append(Component.text(" - List all available warps\n", TextColor.fromHexString("#18c1c7")))
            .append(Component.text("/server", TextColor.fromHexString("#305abf"))
                .clickEvent(ClickEvent.runCommand("/server")))
            .append(Component.text(" - List all available servers\n", TextColor.fromHexString("#18c1c7")))
            .append(Component.text("/tips", TextColor.fromHexString("#305abf"))
                .clickEvent(ClickEvent.runCommand("/tips")))
            .append(Component.text(" - View tips about the network", TextColor.fromHexString("#18c1c7"))));
        return true;
    }
}
