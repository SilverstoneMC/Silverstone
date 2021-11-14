package me.jasonhorkles.silverstonemain.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Music implements CommandExecutor {

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Sorry, but only players can do that.");
            return true;
        }

        switch (cmd.getName().toLowerCase()) {
            case "stopmusic" -> {
                player.performCommand("music stop");
                sender.sendActionBar(Component.text(ChatColor.translateAlternateColorCodes('&', "&cMusic stopped! Type &7/togglemusic &cto permanently toggle it")));
            }
            case "togglemusic" -> {
                boolean disabled = false;

                for (String s : player.getScoreboardTags())
                    if (s.equals("MusicDisabled")) {
                        disabled = true;
                        break;
                    }

                if (disabled) {
                    player.getScoreboardTags().remove("MusicDisabled");
                    player.sendMessage(ChatColor.GREEN + "Music will now automatically play again.");
                } else {
                    player.getScoreboardTags().add("MusicDisabled");
                    player.sendMessage(ChatColor.RED + "Music will no longer automatically play.");
                }
            }
        }
        return true;
    }
}
