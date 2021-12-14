package net.silverstonemc.silverstonewarnings;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.ButtonClickEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.ActionRow;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DiscordEvents extends ListenerAdapter {

    private final JavaPlugin plugin;

    public DiscordEvents(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        String type = event.getComponentId().replaceFirst(":.*", "");
        switch (type) {
            case "removewarning" -> {
                String warning = event.getComponentId().replaceFirst(".*: ", "").replaceAll(" :.*", "");
                String uuid = event.getComponentId().replaceAll(".*:", "");
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));

                BukkitRunnable task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ssw remove " + warning + " " + player.getName());
                    }
                };
                task.runTask(plugin);
                disableButtons(event, 1);
            }

            case "removewarningsilently" -> {
                String warning = event.getComponentId().replaceFirst(".*: ", "").replaceAll(" :.*", "");
                String uuid = event.getComponentId().replaceAll(".*:", "");
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));

                BukkitRunnable task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ssw remove " + warning + " " + player.getName() + " -s");
                    }
                };
                task.runTask(plugin);
                disableButtons(event, 1);
            }

            case "clearwarning" -> {
                String warning = event.getComponentId().replaceFirst(".*: ", "").replaceAll(" :.*", "");
                String uuid = event.getComponentId().replaceAll(".*:", "");
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));

                BukkitRunnable task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ssw clear " + warning + " " + player.getName());
                    }
                };
                task.runTask(plugin);
                disableButtons(event, 2);
            }

            case "clearallwarnings" -> {
                String uuid = event.getComponentId().replaceAll(".*:", "");
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));

                BukkitRunnable task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ssw clear all " + player.getName());
                    }
                };
                task.runTask(plugin);
                disableButtons(event, 3);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void disableButtons(ButtonClickEvent event, int level) {
        event.deferEdit().queue();
        List<Button> buttons = event.getMessage().getButtons();
        List<Button> newButtons = new ArrayList<>();
        Message message = event.getMessage();

        switch (level) {
            case 1 -> {
                newButtons.add(buttons.get(0).asDisabled());
                newButtons.add(buttons.get(1).asDisabled());
                newButtons.add(buttons.get(2));
                newButtons.add(buttons.get(3));
            }

            case 2 -> {
                newButtons.add(buttons.get(0).asDisabled());
                newButtons.add(buttons.get(1).asDisabled());
                newButtons.add(buttons.get(2).asDisabled());
                newButtons.add(buttons.get(3));
            }

            case 3 -> {
                newButtons.add(buttons.get(0).asDisabled());
                newButtons.add(buttons.get(1).asDisabled());
                newButtons.add(buttons.get(2).asDisabled());
                newButtons.add(buttons.get(3).asDisabled());
            }
        }

        message.editMessageComponents(ActionRow.of(newButtons)).queue();
    }
}
