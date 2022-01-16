package net.silverstonemc.silverstonemain.events;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.ButtonClickEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.ActionRow;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DiscordEvents extends ListenerAdapter {

    private final JavaPlugin plugin;

    public DiscordEvents(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onButtonClick(ButtonClickEvent event) {
        if (event.getComponentId().startsWith("warnskin")) {
            Message message = event.getMessage();
            String player = message.getEmbeds().get(0).getAuthor().getName();
            player = player.substring(0, player.indexOf(' '));
            String finalPlayer = player;
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "warn " + finalPlayer + " skin");
                }
            };
            task.runTask(plugin);

            event.deferEdit().queue();
            Button button = event.getButton().asDisabled();
            message.editMessageComponents(ActionRow.of(button)).queue();
        }
    }
}
