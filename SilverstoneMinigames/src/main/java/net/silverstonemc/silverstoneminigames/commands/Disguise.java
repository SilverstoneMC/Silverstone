package net.silverstonemc.silverstoneminigames.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.List;

public class Disguise implements Listener {
    @EventHandler
    public void onDisguiseCommand(ServerCommandEvent event) {
        if (event.getCommand().toLowerCase().startsWith("disguiseplayer") || event.getCommand().toLowerCase()
            .startsWith("disguisemodifyplayer") || event.getCommand().toLowerCase()
            .startsWith("undisguiseplayer")) {
            event.setCancelled(true);
            runForPlayers(event.getCommand(), event.getSender());
        }
    }

    private void runForPlayers(String command, CommandSender sender) {
        String[] args = command.split(" ");
        List<Entity> selector = Bukkit.selectEntities(sender, args[1]);

        if (selector.isEmpty()) {
            sender.sendMessage(Component.text("No players found.", NamedTextColor.RED));
            return;
        }

        for (Entity entity : selector) {
            Player player = Bukkit.getPlayer(entity.getName());
            if (player == null) continue;

            Bukkit.dispatchCommand(sender, command.replace(args[1], player.getName()));
        }
    }
}
