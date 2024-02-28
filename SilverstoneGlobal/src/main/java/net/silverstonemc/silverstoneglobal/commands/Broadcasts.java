package net.silverstonemc.silverstoneglobal.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Broadcasts implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        switch (cmd.getName().toLowerCase()) {
            case "bclag" -> {
                sendBroadcast("WARNING", "The server may experience lag for a moment!");

                if (!(sender instanceof Player)) sender.sendMessage(Component.text("Lag broadcast sent!",
                    NamedTextColor.GREEN));
            }

            case "bcnolag" -> {
                sendBroadcast("NOTICE", "The server should no longer lag.");

                if (!(sender instanceof Player)) sender.sendMessage(Component.text("No more lag broadcast sent!",
                    NamedTextColor.GREEN));
            }

            case "bcrestart" -> {
                sendBroadcast("WARNING", "The server will restart soon!");
                for (Player player : Bukkit.getOnlinePlayers())
                    player.playSound(player.getLocation(),
                        Sound.BLOCK_NOTE_BLOCK_HARP,
                        SoundCategory.MASTER,
                        100,
                        1.6f);

                if (!(sender instanceof Player)) sender.sendMessage(Component.text("Restart broadcast sent!",
                    NamedTextColor.GREEN));
            }

            case "bcshutdown" -> {
                sendBroadcast("WARNING", "The server will shut down soon!");
                for (Player player : Bukkit.getOnlinePlayers())
                    player.playSound(player.getLocation(),
                        Sound.BLOCK_NOTE_BLOCK_HARP,
                        SoundCategory.MASTER,
                        100,
                        1.6f);

                if (!(sender instanceof Player)) sender.sendMessage(Component.text("Shutdown broadcast sent!",
                    NamedTextColor.GREEN));
            }
        }
        return true;
    }

    private void sendBroadcast(String pretext, String message) {
        for (Player player : Bukkit.getOnlinePlayers())
            player.sendMessage(Component.text().append(Component.text(pretext,
                    NamedTextColor.RED,
                    TextDecoration.BOLD)).append(Component.text(" > ", NamedTextColor.AQUA, TextDecoration.BOLD))
                .append(Component.text(message, NamedTextColor.GREEN)));
    }
}
