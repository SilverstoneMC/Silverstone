package net.silverstonemc.silverstonemain.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class GettingStarted implements CommandExecutor {

    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        sender.sendMessage(Component.text("\nGETTING STARTED")
                .color(TextColor.fromHexString("#18d958"))
                .decorate(TextDecoration.BOLD));

        sender.sendMessage(Component.text("-----------------")
                .color(TextColor.fromHexString("#305abf")));

        sender.sendMessage(Component.text()
                .append(Component.text("SURVIVAL: ")
                        .color(TextColor.fromHexString("#18d958"))
                        .decorate(TextDecoration.BOLD))
                .append(Component.text("If you're wanting to play survival, then simply type ")
                        .color(TextColor.fromHexString("#18c1c7")))
                .append(Component.text("/rtp")
                        .color(TextColor.fromHexString("#305abf"))
                        .clickEvent(ClickEvent.suggestCommand("/rtp")))
                .append(Component.text(" to get started. Keep Inventory is on by default, but you can type ")
                        .color(TextColor.fromHexString("#18c1c7")))
                .append(Component.text("/keepinv")
                        .color(TextColor.fromHexString("#305abf"))
                        .clickEvent(ClickEvent.suggestCommand("/keepinv")))
                .append(Component.text(" to toggle it (only applies to the overworld and the nether). Check your Keep Inventory status with ")
                        .color(TextColor.fromHexString("#18c1c7")))
                .append(Component.text("/keepinvstatus")
                        .color(TextColor.fromHexString("#305abf"))
                        .clickEvent(ClickEvent.suggestCommand("/keepinvstatus")))
                .append(Component.text(".")
                        .color(TextColor.fromHexString("#18c1c7"))));

        sender.sendMessage(Component.text()
                .append(Component.text("\nOTHER INFO: ")
                        .color(TextColor.fromHexString("#18d958"))
                        .decorate(TextDecoration.BOLD))
                .append(Component.text("Type ")
                        .color(TextColor.fromHexString("#18c1c7"))
                        .decoration(TextDecoration.BOLD, false))
                .append(Component.text("/warps")
                        .color(TextColor.fromHexString("#305abf"))
                        .clickEvent(ClickEvent.suggestCommand("/warps")))
                .append(Component.text(" to see a list of all available warps, and ")
                        .color(TextColor.fromHexString("#18c1c7")))
                .append(Component.text("/tips")
                        .color(TextColor.fromHexString("#305abf"))
                        .clickEvent(ClickEvent.suggestCommand("/tips")))
                .append(Component.text(" to get a list of all the tips for the server.")
                        .color(TextColor.fromHexString("#18c1c7"))));

        sender.sendMessage(Component.text(ChatColor.DARK_AQUA + "\nType ")
                .append(Component.text("/warp crates")
                        .color(TextColor.fromHexString("#305abf"))
                        .clickEvent(ClickEvent.suggestCommand("/warp crates")))
                .append(Component.text(ChatColor.DARK_AQUA + " to redeem your beginner crate keys!"))
        );
        return true;
    }
}
