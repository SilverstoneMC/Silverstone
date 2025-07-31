package net.silverstonemc.silverstoneminigames.managers;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.api.hologram.HologramType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silverstonemc.silverstoneminigames.SilverstoneMinigames;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import static net.silverstonemc.silverstoneminigames.SilverstoneMinigames.data;
import static net.silverstonemc.silverstoneminigames.SilverstoneMinigames.jda;

public class MinigameManager implements CommandExecutor, TabCompleter {
    private BukkitTask statusUpdateTask;
    private long lastStatusUpdate;

    public enum MinigameStatus {
        OPEN("<green><bold>Open", "Open"),
        READY("<green><bold>Ready", "Ready"),
        IN_SESSION("<yellow><bold>In Session...", "In Session..."),
        RESETTING("<gold><bold>Resetting...", "Resetting..."),
        CLOSED("<red><bold>Closed", "Closed");

        private final String text;
        private final String strippedText;

        MinigameStatus(String text, String strippedText) {
            this.text = text;
            this.strippedText = strippedText;
        }

        public String getText() {
            return text;
        }

        public String getStrippedText() {
            return strippedText;
        }
    }

    public enum MinigamePlayers {
        SINGLE("1"),
        COMBO("1+"),
        MULTI("2+");

        private final String text;

        MinigamePlayers(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) return false;
        switch (args[0].toLowerCase()) {
            case "create" -> createMinigame(sender, args);
            case "delete" -> deleteMinigame(sender, args);
            case "list" -> listMinigames(sender);
            case "set" -> setMinigame(sender, args);

            default -> {
                return false;
            }
        }

        return true;
    }

    private void createMinigame(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(Component.text(
                "Usage: /mgm create <game_id> <friendly_name> <SINGLE | MULTI>",
                NamedTextColor.RED));
            return;
        }

        // game_id
        String gameId = args[1];

        // Validate gameId against existing holograms
        if (FancyHologramsPlugin.get().getHologramManager().getHologram(gameId).isEmpty()) {
            sender.sendMessage(Component.text(
                "Hologram with name '" + gameId + "' does not exist!",
                NamedTextColor.RED));
            return;
        }

        // Validate gameId against existing minigames
        if (data.getConfig().get("minigame-data." + gameId) != null) {
            sender.sendMessage(Component.text(
                "Minigame with ID '" + gameId + "' already exists!",
                NamedTextColor.RED));
            return;
        }


        // friendly_name
        String friendlyName = args[2].replace("_", " ");
        data.getConfig().set("minigame-data." + gameId + ".friendly-name", friendlyName);


        // SINGLE | MULTI
        MinigamePlayers players;
        try {
            players = MinigamePlayers.valueOf(args[3].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("Invalid player count!", NamedTextColor.RED));
            return;
        }

        data.getConfig().set("minigame-data." + gameId + ".status", MinigameStatus.CLOSED.name());
        data.getConfig().set("minigame-data." + gameId + ".players", players.name());
        data.saveConfig();
        updateDiscordMessage();

        sender.sendMessage(Component.text("Created minigame ", NamedTextColor.GREEN).append(Component.text(gameId,
                NamedTextColor.AQUA)).append(Component.text(" with ", NamedTextColor.GREEN))
            .append(Component.text(players.getText(), NamedTextColor.AQUA))
            .append(Component.text(" player(s)!", NamedTextColor.GREEN)));
    }

    private void deleteMinigame(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /mgm delete <game_id>", NamedTextColor.RED));
            return;
        }

        String gameId = args[1];

        if (data.getConfig().get("minigame-data." + gameId) == null) {
            sender.sendMessage(Component.text(
                "Minigame with ID '" + gameId + "' does not exist!",
                NamedTextColor.RED));
            return;
        }

        data.getConfig().set("minigame-data." + gameId, null);
        data.saveConfig();
        updateDiscordMessage();

        sender.sendMessage(Component.text("Deleted minigame ", NamedTextColor.GRAY)
            .append(Component.text(gameId, NamedTextColor.RED)));
    }

    // mgm list
    private void listMinigames(CommandSender sender) {
        //noinspection DataFlowIssue
        Set<String> minigameSet = data.getConfig().getConfigurationSection("minigame-data").getKeys(false);
        List<String> minigames = new ArrayList<>(minigameSet);
        Collections.sort(minigames);

        sender.sendMessage(Component.text("Available minigames:", NamedTextColor.GREEN));
        for (String minigame : minigames) {
            MinigameStatus status = MinigameStatus.valueOf(data.getConfig()
                .getString("minigame-data." + minigame + ".status", "NULL"));
            String playerCount = MinigamePlayers.valueOf(data.getConfig()
                .getString("minigame-data." + minigame + ".players", "NULL")).getText();

            NamedTextColor gameColor = NamedTextColor.DARK_RED;
            switch (status) {
                case OPEN -> gameColor = NamedTextColor.AQUA;
                case READY -> gameColor = NamedTextColor.GREEN;
                case IN_SESSION -> gameColor = NamedTextColor.YELLOW;
                case RESETTING -> gameColor = NamedTextColor.GOLD;
                case CLOSED -> gameColor = NamedTextColor.RED;
            }
            HoverEvent<Component> gameHover = HoverEvent.showText(Component.text(status.name(), gameColor));
            String gameFriendlyName = data.getConfig()
                .getString("minigame-data." + minigame + ".friendly-name", "Unknown");

            sender.sendMessage(Component.empty().append(Component.text("⏺", gameColor).hoverEvent(gameHover)
                .append(Component.text(
                    " " + minigame.replaceFirst("^m", "") + " (" + playerCount + ")",
                    NamedTextColor.DARK_AQUA).hoverEvent(HoverEvent.showText(Component.text(
                    gameFriendlyName,
                    NamedTextColor.AQUA))))));
        }
    }

    // mgm set <game_id> friendlyname <name>
    // mgm set <game_id> players <SINGLE|MULTI>
    // mgm set <game_id> status <open|ready|in_session|resetting|closed>
    private void setMinigame(CommandSender sender, String[] args) {
        TextComponent usage = Component.text(
            "/mgm set <game_id> <friendly_name | players | status> <value>",
            NamedTextColor.RED);

        if (args.length < 4) {
            sender.sendMessage(usage);
            return;
        }

        String gameId = args[1];
        if (data.getConfig().get("minigame-data." + gameId) == null) {
            sender.sendMessage(Component.text(
                "Minigame '" + gameId + "' does not exist!",
                NamedTextColor.RED));
            return;
        }

        String value = args[3];

        switch (args[2].toLowerCase()) {
            case "friendlyname" -> {
                String friendlyName = value.replace("_", " ");

                data.getConfig().set("minigame-data." + gameId + ".friendly-name", friendlyName);
                data.saveConfig();
                updateDiscordMessage();

                sender.sendMessage(Component.text("Set minigame ", NamedTextColor.GREEN)
                    .append(Component.text(gameId, NamedTextColor.AQUA))
                    .append(Component.text(" friendly name to ", NamedTextColor.GREEN))
                    .append(Component.text(friendlyName, NamedTextColor.AQUA)));
            }

            case "players" -> {
                MinigamePlayers players;
                try {
                    players = MinigamePlayers.valueOf(value.toUpperCase());
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(Component.text("Invalid player count!", NamedTextColor.RED));
                    return;
                }

                data.getConfig().set("minigame-data." + gameId + ".players", players.name());
                data.saveConfig();

                sender.sendMessage(Component.text("Set minigame ", NamedTextColor.GREEN)
                    .append(Component.text(gameId, NamedTextColor.AQUA))
                    .append(Component.text(" player count to ", NamedTextColor.GREEN))
                    .append(Component.text(players.getText(), NamedTextColor.AQUA)));
            }

            case "status" -> {
                MinigameStatus status;
                try {
                    status = MinigameStatus.valueOf(value.toUpperCase());
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(Component.text("Invalid game status!", NamedTextColor.RED));
                    return;
                }

                setHologram(sender, gameId, status);
                data.getConfig().set("minigame-data." + gameId + ".status", status.name());
                queueStatusUpdate();

                if (sender instanceof Player) sender.sendMessage(Component.text(
                    "Reminder: This command is only intended to be run via command blocks!",
                    NamedTextColor.GRAY,
                    TextDecoration.ITALIC));
                sender.sendMessage("Set " + gameId + " status to: " + status.name());
            }

            default -> sender.sendMessage(usage);
        }
    }

    private void setHologram(CommandSender sender, String gameId, MinigameStatus status) {
        Optional<Hologram> optionalHologram = FancyHologramsPlugin.get().getHologramManager().getHologram(
            gameId);

        if (optionalHologram.isEmpty()) {
            sender.sendMessage(Component.text("Hologram not found!", NamedTextColor.RED));
            return;
        }

        HologramData data = optionalHologram.get().getData();
        if (data.getType() != HologramType.TEXT) {
            sender.sendMessage(Component.text("Hologram is not of type TEXT!", NamedTextColor.RED));
            return;
        }

        Hologram hologram = optionalHologram.get();
        TextHologramData textData = (TextHologramData) data;
        List<String> holoText = new ArrayList<>(textData.getText());
        holoText.set(holoText.size() - 1, status.getText());
        textData.setText(holoText);
        hologram.queueUpdate();
        hologram.refreshHologram(Bukkit.getOnlinePlayers());
    }

    private void queueStatusUpdate() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastUpdate = currentTime - lastStatusUpdate;
        final int cooldown = 1050;

        // Update immediately if enough time has passed since the last update
        if (timeSinceLastUpdate >= cooldown && statusUpdateTask == null) {
            data.saveConfig();
            updateDiscordMessage();
            lastStatusUpdate = currentTime;

        } else if (statusUpdateTask == null) statusUpdateTask = new BukkitRunnable() {
            @Override
            public void run() {
                data.saveConfig();
                updateDiscordMessage();

                lastStatusUpdate = System.currentTimeMillis();
                statusUpdateTask = null;
            }
        }.runTaskLaterAsynchronously(SilverstoneMinigames.getInstance(), cooldown / 50);
    }


    // game_id will coincide with the hologram name
    // mgm list
    // mgm delete <game_id>
    // mgm create <game_id> <friendly_name> <SINGLE|MULTI>
    // mgm set    <game_id> friendlyname    <name>
    // mgm set    <game_id> players         <SINGLE|MULTI>
    // mgm set    <game_id> status          <open|ready|in_session|resetting|closed>
    //      0          1        2                   3
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        List<String> result = new ArrayList<>();

        if (args.length == 1) {
            List<String> commands = List.of("create", "delete", "list", "set");

            for (String a : commands)
                if (a.toLowerCase().startsWith(args[0].toLowerCase())) result.add(a);
            return result;
        }

        if (args[0].equalsIgnoreCase("list")) return null;

        // Game IDs for "create", "delete", and "set"
        if (args.length == 2) {
            List<String> arguments = new ArrayList<>();

            //noinspection DataFlowIssue
            List<String> minigameIds = new ArrayList<>(data.getConfig()
                .getConfigurationSection("minigame-data").getKeys(false));

            switch (args[0].toLowerCase()) {
                case "create" -> {
                    // Get the hologram names minus the ones that are already in use
                    Collection<Hologram> holograms = FancyHologramsPlugin.get().getHologramManager()
                        .getHolograms();
                    List<String> hologramNames = new ArrayList<>();
                    holograms.forEach(hologram -> hologramNames.add(hologram.getData().getName()));

                    for (String hologramName : hologramNames)
                        if (!minigameIds.contains(hologramName)) arguments.add(hologramName);
                }

                case "delete", "set" -> arguments.addAll(minigameIds);
            }

            for (String gameId : arguments)
                if (gameId.toLowerCase().startsWith(args[1].toLowerCase())) result.add(gameId);
            return result;
        }

        // "<friendly_name>" for "create"
        if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
            List<String> friendlyName = new ArrayList<>(List.of("<friendly_name>"));

            for (String a : friendlyName)
                if (a.toLowerCase().startsWith(args[2].toLowerCase())) result.add(a);
            return result;
        }

        // Game's friendly name for "set <game> friendlyname"
        if (args.length == 4 && args[0].equalsIgnoreCase("set") && args[2].equalsIgnoreCase("friendlyname")) {
            List<String> friendlyName = new ArrayList<>(List.of(data.getConfig()
                .getString("minigame-data." + args[1] + ".friendly-name", "Unknown").replace(" ", "_")));

            for (String a : friendlyName)
                if (a.toLowerCase().startsWith(args[3].toLowerCase())) result.add(a);
            return result;
        }

        List<String> minigamePlayerTypes = Stream.of(MinigamePlayers.values()).map(Enum::name).toList();

        // SINGLE/MULTI for "create" and "set <game> players"
        if (args.length == 4 && (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("set") && args[2].equalsIgnoreCase(
            "players"))) {

            List<String> arguments = new ArrayList<>(minigamePlayerTypes);
            for (String gameId : arguments)
                if (gameId.toLowerCase().startsWith(args[3].toLowerCase())) result.add(gameId);
            return result;
        }

        if (args[0].equalsIgnoreCase("set")) {
            // friendlyname/players/status for "set"
            if (args.length == 3) {
                List<String> arguments = new ArrayList<>(List.of("friendlyname", "players", "status"));

                for (String a : arguments)
                    if (a.toLowerCase().startsWith(args[2].toLowerCase())) result.add(a);
                return result;
            }

            if (args.length == 4) {
                List<String> arguments = new ArrayList<>();

                // Game statuses for "set <game> status"
                if (args[2].equalsIgnoreCase("status")) arguments.addAll(Arrays
                    .stream(MinigameStatus.values()).map(Enum::name).toList());

                for (String a : arguments)
                    if (a.toLowerCase().startsWith(args[3].toLowerCase())) result.add(a);
                return result;
            }
        }

        return null;
    }

    public void updateDiscordMessage() {
        if (jda == null) throw new IllegalStateException(
            "Tried to update message while JDA is not initialized!");

        new BukkitRunnable() {
            @Override
            public void run() {
                NewsChannel channel = jda.getNewsChannelById(1395537310694641704L);
                if (channel == null) {
                    SilverstoneMinigames.getInstance().getLogger().severe(
                        "Minigames Discord channel not found!");
                    return;
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Minigame Status");
                embed.setColor(Color.decode("#37E67D"));

                // Get all minigames (sorted alphabetically by friendly name)
                // In order to do so, the friendly names are the keys instead of the game IDs
                Map<String, MinigameStatus> minigames = new HashMap<>();

                //noinspection DataFlowIssue
                for (String gameId : data.getConfig().getConfigurationSection("minigame-data")
                    .getKeys(false)) {
                    String friendlyName = data.getConfig()
                        .getString("minigame-data." + gameId + ".friendly-name", gameId);
                    MinigameStatus status = MinigameStatus.valueOf(data.getConfig()
                        .getString("minigame-data." + gameId + ".status", "NULL"));

                    // Add to the map
                    minigames.put(friendlyName, status);
                }

                // Sort the minigames by friendly name and build the description
                StringBuilder description = new StringBuilder();
                minigames.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
                    String friendlyName = entry.getKey();
                    MinigameStatus status = entry.getValue();
                    String statusText = status.getStrippedText();

                    String statusIcon = "Unknown";
                    switch (status) {
                        case OPEN -> statusIcon = "☑️";
                        case READY -> statusIcon = "✅";
                        case IN_SESSION -> statusIcon = "⏳";
                        case RESETTING -> statusIcon = "🔁";
                        case CLOSED -> statusIcon = "🚫";
                    }

                    description.append(String.format(
                        "%s %-17s%s%s",
                        statusIcon,
                        "`" + friendlyName,
                        "`→ ",
                        statusText)).append("\n");
                });

                embed.setDescription(description.toString().strip());

                List<Message> latestMessage;
                try {
                    latestMessage = channel.getIterableHistory().takeAsync(1).thenApply(ArrayList::new)
                        .get(30, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    throw new RuntimeException(e);
                }

                if (latestMessage.isEmpty()) channel.sendMessageEmbeds(embed.build()).queue();
                else latestMessage.getFirst().editMessageEmbeds(embed.build()).queue();
            }
        }.runTaskAsynchronously(SilverstoneMinigames.getInstance());
    }
}
