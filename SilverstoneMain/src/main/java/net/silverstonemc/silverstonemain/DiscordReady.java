package net.silverstonemc.silverstonemain;

import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.util.DiscordUtil;
import net.silverstonemc.silverstonemain.events.DiscordEvents;
import org.bukkit.plugin.java.JavaPlugin;

public record DiscordReady(JavaPlugin plugin) {
    @Subscribe
    public void onReady(/*DiscordReadyEvent event*/) {
        DiscordUtil.getJda().addEventListener(new DiscordEvents(plugin));
    }
}
