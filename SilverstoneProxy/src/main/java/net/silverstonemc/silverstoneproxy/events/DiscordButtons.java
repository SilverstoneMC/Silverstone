package net.silverstonemc.silverstoneproxy.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;
import net.silverstonemc.silverstoneproxy.UserManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DiscordButtons extends ListenerAdapter {
    public DiscordButtons(SilverstoneProxy instance) {
        i = instance;
    }

    private final SilverstoneProxy i;

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        // Warn new players with inappropriate skins
        if (event.getComponentId().startsWith("warnskin")) {
            Message message = event.getMessage();
            String player = message.getEmbeds().get(0).getAuthor().getName();
            player = player.substring(0, player.indexOf(' '));
            String finalPlayer = player;

            i.server.getCommandManager().executeAsync(i.server.getConsoleCommandSource(),
                "warn " + finalPlayer + " skin");

            event.deferEdit().queue();
            Button button = event.getButton().asDisabled();
            message.editMessageComponents(ActionRow.of(button)).queue();

            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                Message latestMessage = event.getChannel().retrieveMessageById(event.getMessageId())
                    .complete();
                EmbedBuilder embed = new EmbedBuilder(latestMessage.getEmbeds().get(0));
                embed.setDescription("Warned by " + event.getUser().getAsMention());
                latestMessage.editMessageEmbeds(embed.build()).queue();
            }, 3, TimeUnit.SECONDS);

            return;
        }

        UUID uuid = UUID.fromString(event.getComponentId().replaceAll(".*:", ""));
        String username = new UserManager(i).getUsername(uuid);
        String warning = event.getComponentId().replaceFirst(".*: ", "").replaceAll(" :.*", "");

        String type = event.getComponentId().replaceFirst(":.*", "");
        switch (type) {
            case "removewarning" -> {
                i.server.getCommandManager().executeAsync(i.server.getConsoleCommandSource(),
                    "ssw remove " + warning + " " + username);
                disableButtons(event, 1);
            }

            case "removewarningsilently" -> {
                i.server.getCommandManager().executeAsync(i.server.getConsoleCommandSource(),
                    "ssw remove " + warning + " " + username + " -s");
                disableButtons(event, 1);
            }

            case "clearwarning" -> {
                i.server.getCommandManager().executeAsync(i.server.getConsoleCommandSource(),
                    "ssw clear " + warning + " " + username);
                disableButtons(event, 2);
            }

            case "clearallwarnings" -> {
                i.server.getCommandManager().executeAsync(i.server.getConsoleCommandSource(),
                    "ssw clear all " + username);
                disableButtons(event, 3);
            }
        }
    }

    private void disableButtons(ButtonInteractionEvent event, int level) {
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

        EmbedBuilder embed = new EmbedBuilder(message.getEmbeds().get(0));
        embed.setDescription("Last action performed by " + event.getUser().getAsMention());
        message.editMessageEmbeds(embed.build()).queue();
    }
}
