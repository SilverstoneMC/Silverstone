package net.silverstonemc.silverstoneminigames.commands;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

@Deprecated(forRemoval = true)
public class Disguise implements Listener {
    @EventHandler
    public void onDisguiseCommand(ServerCommandEvent event) {
        if (event.getCommand().toLowerCase().startsWith("disguiseplayer") || event.getCommand().toLowerCase()
            .startsWith("disguisemodifyplayer") || event.getCommand().toLowerCase().startsWith(
            "undisguiseplayer")) Bukkit.getLogger().severe("Disguise command used: " + event.getCommand());
    }
}
