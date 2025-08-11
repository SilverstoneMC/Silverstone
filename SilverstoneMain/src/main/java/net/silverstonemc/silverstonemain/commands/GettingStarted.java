package net.silverstonemc.silverstonemain.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class GettingStarted implements CommandExecutor {
    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        sender.sendMessage(Component.text(
            "\nGETTING STARTED",
            TextColor.fromHexString("#18d958"),
            TextDecoration.BOLD));

        sender.sendMessage(Component.text(
            "                         ",
            NamedTextColor.DARK_GRAY,
            TextDecoration.STRIKETHROUGH));

        sender.sendMessage(Component.empty().append(
            Component.text("/htp", TextColor.fromHexString("#305abf"))
                .clickEvent(ClickEvent.suggestCommand("/htp")),
            Component.text(" - Open the How to Play menu\n", TextColor.fromHexString("#18c1c7")),

            Component.text("/warp", TextColor.fromHexString("#305abf"))
                .clickEvent(ClickEvent.runCommand("/warp")),
            Component.text(" - List all available warps\n", TextColor.fromHexString("#18c1c7")),

            Component.text("/server", TextColor.fromHexString("#305abf"))
                .clickEvent(ClickEvent.runCommand("/server")),
            Component.text(" - List all available servers\n", TextColor.fromHexString("#18c1c7")),

            Component.text("/tips", TextColor.fromHexString("#305abf"))
                .clickEvent(ClickEvent.runCommand("/tips")),
            Component.text(" - View tips about the network\n", TextColor.fromHexString("#18c1c7")),

            Component.text("/game", TextColor.fromHexString("#305abf"))
                .clickEvent(ClickEvent.runCommand("/game")),
            Component.text(" - Get a random minigame to play\n", TextColor.fromHexString("#18c1c7")),

            Component.text("/prefixes", TextColor.fromHexString("#305abf"))
                .clickEvent(ClickEvent.runCommand("/prefixes")),
            Component.text(" - Display what the prefix emojis mean\n", TextColor.fromHexString("#18c1c7"))));
        return true;
    }
}
