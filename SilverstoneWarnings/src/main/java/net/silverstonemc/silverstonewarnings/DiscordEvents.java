package net.silverstonemc.silverstonewarnings;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DiscordEvents extends ListenerAdapter {

    private final SilverstoneWarnings plugin = SilverstoneWarnings.getPlugin();

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        UUID uuid = UUID.fromString(event.getComponentId().replaceAll(".*:", ""));
        String username = plugin.getPlayerName(uuid);
        String warning = event.getComponentId().replaceFirst(".*: ", "").replaceAll(" :.*", "");

        String type = event.getComponentId().replaceFirst(":.*", "");
        switch (type) {
            case "removewarning" -> {
                plugin.getProxy()
                        .getPluginManager()
                        .dispatchCommand(plugin.getProxy().getConsole(), "ssw remove " + warning + " " + username);
                disableButtons(event, 1);
            }

            case "removewarningsilently" -> {
                plugin.getProxy()
                        .getPluginManager()
                        .dispatchCommand(plugin.getProxy()
                                .getConsole(), "ssw remove " + warning + " " + username + " -s");
                disableButtons(event, 1);
            }

            case "clearwarning" -> {
                plugin.getProxy()
                        .getPluginManager()
                        .dispatchCommand(plugin.getProxy().getConsole(), "ssw clear " + warning + " " + username);
                disableButtons(event, 2);
            }

            case "clearallwarnings" -> {
                plugin.getProxy()
                        .getPluginManager()
                        .dispatchCommand(plugin.getProxy().getConsole(), "ssw clear all " + username);
                disableButtons(event, 3);
            }
        }
    }

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
