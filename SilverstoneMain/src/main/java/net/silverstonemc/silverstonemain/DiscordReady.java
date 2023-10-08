package net.silverstonemc.silverstonemain;

import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import github.scarsz.discordsrv.util.DiscordUtil;
import net.silverstonemc.silverstonemain.events.DiscordEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public record DiscordReady(JavaPlugin plugin) {
    @Subscribe
    public void onReady(DiscordReadyEvent event) {
        DiscordUtil.getJda().addEventListener(new DiscordEvents(plugin));

        TextChannel channel = DiscordUtil.getJda().getTextChannelById(1075640285083734067L);
        try {
            //noinspection DataFlowIssue
            if (channel.getIterableHistory().takeAsync(1).thenApply(ArrayList::new).get(30, TimeUnit.SECONDS)
                .isEmpty()) channel.sendMessage("## Select a vanish state")
                .setActionRow(Button.success("vanish-on", "Vanish"), Button.danger("vanish-off", "Un-vanish"))
                .queue();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
