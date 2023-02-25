package net.silverstonemc.silverstonemain.events;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.ButtonClickEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.ActionRow;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class DiscordEvents extends ListenerAdapter {
    public DiscordEvents(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;

    @SuppressWarnings("DataFlowIssue")
    public void onButtonClick(ButtonClickEvent event) {
        String componentId = event.getComponentId();

        //todo migrate to proxy
        if (componentId.startsWith("warnskin")) {
            Message message = event.getMessage();
            String player = message.getEmbeds().get(0).getAuthor().getName();
            player = player.substring(0, player.indexOf(' '));
            String finalPlayer = player;
            
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hopecommander warn " + finalPlayer + " skin");
                }
            }.runTask(plugin);

            event.deferEdit().queue();
            Button button = event.getButton().asDisabled();
            message.editMessageComponents(ActionRow.of(button)).queue();
        }

        if (componentId.equals("vanish")) if (event.getMember().getIdLong() == 277291758503723010L)
            event.reply("**Select a status:**")
                .addActionRow(Button.success("vanish:on:jasonmain", "Vanish main"),
                    Button.danger("vanish:off:jasonmain", "Un-vanish main"),
                    Button.success("vanish:on:jasonalt", "Vanish alt"),
                    Button.danger("vanish:off:jasonalt", "Un-vanish alt")).setEphemeral(true).queue();
        else event.reply("**Select a status:**")
                .addActionRow(Button.success("vanish:on", "Vanish"), Button.danger("vanish:off", "Un-vanish"))
                .setEphemeral(true).queue();

        if (componentId.startsWith("vanish:")) {
            UUID uuid = null;

            if (componentId.endsWith("jasonmain"))
                uuid = UUID.fromString("a28173af-f0a9-47fe-8549-19c6bccf68da");
            else if (componentId.endsWith("jasonalt"))
                uuid = UUID.fromString("bc9848dd-5dd9-4141-9790-f023134cbb7d");
            else switch (event.getMember().getId()) {
                    // Ace
                    case "287993228026707971" ->
                        uuid = UUID.fromString("5c3d3b7c-aa02-4751-ae4b-60b277da9c35");
                    // Panda
                    case "361767232805535746" ->
                        uuid = UUID.fromString("75fb05a2-9d9e-49cb-be34-6bd5215548ba");
                    // Dragon
                    case "340508770788573186" ->
                        uuid = UUID.fromString("e70a4622-85b6-417d-9201-7322e5094465");
                }

            if (uuid == null) {
                event.reply("Couldn't find you in the database! Please contact Jason for help.")
                    .setEphemeral(true).queue();
                return;
            } else event.deferReply(true).queue();

            String username = Bukkit.getOfflinePlayer(uuid).getName();

            if (componentId.replace("vanish:", "").startsWith("on")) {
                BukkitRunnable sync = new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "v on " + username);
                    }
                };
                sync.runTask(plugin);
                event.getHook().editOriginal("Successfully vanished " + username).queue();

            } else if (componentId.replace("vanish:", "").startsWith("off")) {
                BukkitRunnable sync = new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "v off " + username);
                    }
                };
                sync.runTask(plugin);
                event.getHook().editOriginal("Successfully un-vanished " + username).queue();

            } else event.getHook().editOriginal(
                    "Error: No status in component ID `" + componentId + "` - Please report this to Jason")
                .queue();
        }
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        if (event.getChannel().getIdLong() == 869674733925449758L)
            event.getChannel().sendMessage("**Click the button below to change your vanish status:**")
                .setActionRow(Button.primary("vanish", "Vanish")).queue();
    }
}
