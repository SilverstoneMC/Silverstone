package me.jasonhorkles.silverstonewarnings;

import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.plugin.java.JavaPlugin;

public record DiscordReady(JavaPlugin plugin) {

    @Subscribe
    public void onReady() {
        DiscordUtil.getJda().addEventListener(new DiscordEvents(plugin));
    }
}
