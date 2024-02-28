package net.silverstonemc.silverstoneproxy.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;

public class Glist {
    public Glist(SilverstoneProxy instance) {
        i = instance;
    }

    private final SilverstoneProxy i;

    @Subscribe
    public void onCommand(CommandExecuteEvent event) {
        if (event.getCommand().equalsIgnoreCase("glist")) {
            event.setResult(CommandExecuteEvent.CommandResult.denied());
            i.server.getCommandManager().executeAsync(event.getCommandSource(), "glist all");
        }
    }
}
