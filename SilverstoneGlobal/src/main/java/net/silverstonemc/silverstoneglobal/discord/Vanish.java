package net.silverstonemc.silverstoneglobal.discord;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Vanish extends ListenerAdapter {
    public Vanish(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getComponentId().startsWith("vanish-")) return;

        String componentId = event.getComponentId();
        UUID uuid = null;
        switch (event.getMember().getId()) {
            // Ace
            case "287993228026707971" -> uuid = UUID.fromString("5c3d3b7c-aa02-4751-ae4b-60b277da9c35");
            // Panda
            case "361767232805535746" -> uuid = UUID.fromString("75fb05a2-9d9e-49cb-be34-6bd5215548ba");
            // Dragon
            case "340508770788573186" -> uuid = UUID.fromString("e70a4622-85b6-417d-9201-7322e5094465");
            // Jason
            case "277291758503723010" -> {
                if (componentId.endsWith("main")) {
                    uuid = UUID.fromString("a28173af-f0a9-47fe-8549-19c6bccf68da");
                    break;
                } else if (componentId.endsWith("alt")) {
                    uuid = UUID.fromString("bc9848dd-5dd9-4141-9790-f023134cbb7d");
                    break;
                }

                switch (componentId) {
                    case "vanish-on" -> event.reply("**Which account?**").addActionRow(
                        Button.primary("vanish-on-main", "Vanish main"),
                        Button.secondary("vanish-on-alt", "Vanish alt")).setEphemeral(true).queue();
                    case "vanish-off" -> event.reply("**Which account?**").addActionRow(
                        Button.primary("vanish-off-main", "Un-vanish main"),
                        Button.secondary("vanish-off-alt", "Un-vanish alt")).setEphemeral(true).queue();
                }

                return;
            }
        }

        if (uuid == null) {
            event.reply("Couldn't find your account! Please contact Jason for help.").setEphemeral(true)
                .queue();
            return;
        } else event.deferReply(true).queue();

        String username = Bukkit.getOfflinePlayer(uuid).getName();

        switch (componentId.replace("-alt", "").replace("-main", "")) {
            case "vanish-on" -> {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "v on " + username);
                    }
                }.runTask(plugin);
                event.getHook().editOriginal("Successfully vanished your account: " + username).queue();
            }

            case "vanish-off" -> {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "v off " + username);
                    }
                }.runTask(plugin);
                event.getHook().editOriginal("Successfully un-vanished your account: " + username).queue();
            }
        }
    }
}
