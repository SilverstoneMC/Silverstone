package net.silverstonemc.silverstoneglobal;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

public class CommandVerbose implements Listener {
    @EventHandler
    public void command(ServerCommandEvent event) {
        event.getSender().getServer().getLogger().info(event.getCommand());
    }
}
