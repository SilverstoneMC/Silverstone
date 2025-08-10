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
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.silverstonemc.silverstoneminigames.SilverstoneMinigames;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
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

import static net.silverstonemc.silverstoneminigames.SilverstoneMinigames.jda;

public class MinigameManager implements CommandExecutor, TabCompleter {
    private BukkitTask statusUpdateTask;
    private long lastStatusUpdate;
    private final Configuration dataFile;
    private final Configuration actionsFile;
    private final JavaPlugin plugin;

    public MinigameManager(JavaPlugin plugin) {
        dataFile = SilverstoneMinigames.data.getConfig();
        actionsFile = SilverstoneMinigames.actionsData.getConfig();
        this.plugin = plugin;
    }

    public enum MinigameStatus {
        OPEN("<green><b>Open", "Open"),
        READY("<green><b>Ready", "Ready"),
        IN_SESSION("<yellow><b>In Session...", "In Session..."),
        RESETTING("<gold><b>Resetting...", "Resetting..."),
        CLOSED("<red><b>Closed", "Closed");

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
        if (dataFile.get("minigame-data." + gameId) != null) {
            sender.sendMessage(Component.text(
                "Minigame with ID '" + gameId + "' already exists!",
                NamedTextColor.RED));
            return;
        }

        // friendly_name
        String friendlyName = args[2].replace("_", " ");

        // SINGLE | MULTI
        MinigamePlayers players;
        try {
            players = MinigamePlayers.valueOf(args[3].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("Invalid player count!", NamedTextColor.RED));
            return;
        }

        dataFile.set("minigame-data." + gameId + ".friendly-name", friendlyName);
        dataFile.set("minigame-data." + gameId + ".status", MinigameStatus.CLOSED.name());
        dataFile.set("minigame-data." + gameId + ".players", players.name());
        SilverstoneMinigames.data.saveConfig();
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

        if (dataFile.get("minigame-data." + gameId) == null) {
            sender.sendMessage(Component.text(
                "Minigame with ID '" + gameId + "' does not exist!",
                NamedTextColor.RED));
            return;
        }

        dataFile.set("minigame-data." + gameId, null);
        SilverstoneMinigames.data.saveConfig();
        updateDiscordMessage();

        sender.sendMessage(Component.text("Deleted minigame ", NamedTextColor.GRAY)
            .append(Component.text(gameId, NamedTextColor.RED)));
    }

    // mgm list
    private void listMinigames(CommandSender sender) {
        //noinspection DataFlowIssue
        List<String> minigames = new ArrayList<>(dataFile.getConfigurationSection("minigame-data")
            .getKeys(false));
        Collections.sort(minigames);

        TextComponent.Builder messageBuilder = Component.text(
            "\nAvailable minigames:\n",
            NamedTextColor.GREEN).toBuilder();

        for (String minigame : minigames) {
            MinigameStatus status = MinigameStatus.valueOf(dataFile.getString(
                "minigame-data." + minigame + ".status",
                "NULL"));
            String playerCount = MinigamePlayers
                .valueOf(dataFile.getString("minigame-data." + minigame + ".players", "NULL")).getText();

            TextColor gameColor = NamedTextColor.DARK_RED;
            switch (status) {
                case OPEN -> gameColor = NamedTextColor.AQUA;
                case READY -> gameColor = TextColor.fromHexString("#03fcad");
                case IN_SESSION -> gameColor = NamedTextColor.YELLOW;
                case RESETTING -> gameColor = NamedTextColor.GOLD;
                case CLOSED -> gameColor = NamedTextColor.RED;
            }
            String gameFriendlyName = dataFile.getString(
                "minigame-data." + minigame + ".friendly-name",
                "Unknown");

            boolean hasActions = SilverstoneMinigames.actionsData.getConfig().contains(minigame);
            boolean isOpen = status != MinigameStatus.CLOSED;

            String command = "/mgm set " + minigame + " status " + (isOpen ? "CLOSED" : "OPEN") + " -a";

            TextComponent hoverText;
            if (hasActions) hoverText = Component.text(
                "Click to " + (isOpen ? "close" : "open"),
                (isOpen ? NamedTextColor.RED : NamedTextColor.GREEN));

            else hoverText = Component.text("No toggle actions assigned!", NamedTextColor.RED);

            messageBuilder.appendNewline().append(
                Component.text("⏺", gameColor)
                    .hoverEvent(HoverEvent.showText(Component.text(status.name(), gameColor)
                        .append(Component.text(" | ", NamedTextColor.GRAY), hoverText)))
                    .clickEvent((hasActions ? ClickEvent.runCommand(command) : null)),
                Component.text(
                        " " + minigame.replaceFirst("^m", "") + " (" + playerCount + ")",
                        NamedTextColor.DARK_AQUA)
                    .hoverEvent(HoverEvent.showText(Component.text(gameFriendlyName, NamedTextColor.AQUA))));
        }

        sender.sendMessage(messageBuilder.build());
    }

    // mgm set <game_id> friendlyname <name>
    // mgm set <game_id> players <SINGLE|MULTI>
    // mgm set <game_id> status <open|in_session|resetting|closed> [-a]
    private void setMinigame(CommandSender sender, String[] args) {
        TextComponent usage = Component.text(
            "/mgm set <game_id> <friendlyname | players | status> <value>",
            NamedTextColor.RED);

        if (args.length < 4) {
            sender.sendMessage(usage);
            return;
        }

        String gameId = args[1];
        if (dataFile.get("minigame-data." + gameId) == null) {
            sender.sendMessage(Component.text(
                "Minigame '" + gameId + "' does not exist!",
                NamedTextColor.RED));
            return;
        }

        String value = args[3];

        switch (args[2].toLowerCase()) {
            case "friendlyname" -> {
                String friendlyName = value.replace("_", " ");

                dataFile.set("minigame-data." + gameId + ".friendly-name", friendlyName);
                SilverstoneMinigames.data.saveConfig();
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

                dataFile.set("minigame-data." + gameId + ".players", players.name());
                SilverstoneMinigames.data.saveConfig();

                sender.sendMessage(Component.text("Set minigame ", NamedTextColor.GREEN)
                    .append(Component.text(gameId, NamedTextColor.AQUA))
                    .append(Component.text(" player count to ", NamedTextColor.GREEN))
                    .append(Component.text(players.getText(), NamedTextColor.AQUA)));
            }

            case "status" -> {
                // The status with READY combined into OPEN
                MinigameStatus status;

                try {
                    status = MinigameStatus.valueOf(value.toUpperCase());
                    if (status == MinigameStatus.READY) {
                        plugin.getLogger()
                            .severe("Minigame '" + gameId + "' cannot be set to READY directly! Use OPEN instead.");
                        return;
                    }
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(Component.text("Invalid game status!", NamedTextColor.RED));
                    return;
                }

                boolean announce = false;
                if (args.length > 4) announce = args[4].equalsIgnoreCase("-a");

                // Returns if the game failed to change states
                boolean stateFailed = false;
                switch (status) {
                    case OPEN, CLOSED, IN_SESSION -> stateFailed = !toggleGame(
                        sender,
                        gameId,
                        status,
                        announce);
                    case RESETTING -> stateFailed = !evacuateGame(gameId);
                }
                if (stateFailed) return;

                // Update the open status to be what the game should be from actions.yml (OPEN or READY)
                if (status == MinigameStatus.OPEN) status = MinigameStatus.valueOf(actionsFile
                    .getString(gameId + ".open-state", "NULL").toUpperCase());

                setHologram(sender, gameId, status);
                dataFile.set("minigame-data." + gameId + ".status", status.name());
                SilverstoneMinigames.data.saveConfig();
                queueStatusUpdate();

                if (!(sender instanceof Player))
                    sender.sendMessage("Set " + gameId + " status to: " + status.name());
            }

            default -> sender.sendMessage(usage);
        }
    }

    /// Returns true if the game was toggled successfully, otherwise false
    private boolean toggleGame(CommandSender sender, String gameId, MinigameStatus newStatus, boolean announce) {
        // Barrier coordinates
        int x1 = actionsFile.getInt(gameId + ".barriers.x1");
        int y1 = actionsFile.getInt(gameId + ".barriers.y1");
        int z1 = actionsFile.getInt(gameId + ".barriers.z1");
        int x2 = actionsFile.getInt(gameId + ".barriers.x2");
        int y2 = actionsFile.getInt(gameId + ".barriers.y2");
        int z2 = actionsFile.getInt(gameId + ".barriers.z2");

        if (badSettings(
            sender, gameId, new int[]{
                x1,
                y1,
                z1,
                x2,
                y2,
                z2
            })) return false;

        // Teleport players out if closed (RESETTING will take care of the OPEN state)
        if (newStatus == MinigameStatus.CLOSED) evacuateGame(gameId);

        // Fill entrances with barriers/structure voids
        Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(), String.format(
                "execute in minecraft:overworld run fill %d %d %d %d %d %d ",
                x1,
                y1,
                z1,
                x2,
                y2,
                z2) + (newStatus == MinigameStatus.OPEN ? "structure_void replace barrier" : "barrier replace structure_void"));

        if (announce) {
            String friendlyName = dataFile.getString("minigame-data." + gameId + ".friendly-name");

            if (newStatus == MinigameStatus.OPEN) Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "ebc &3Minigame &a" + friendlyName + " &3has been reopened.");
            else if (newStatus == MinigameStatus.CLOSED) Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "ebc &cMinigame &7" + friendlyName + " &chas been temporarily closed.");
        }

        if (sender instanceof Player) new BukkitRunnable() {
            @Override
            public void run() {
                listMinigames(sender);
            }
        }.runTask(plugin);

        return true;
    }

    private boolean badSettings(CommandSender sender, String gameId, int[] barrierCoords) {
        if (actionsFile.get(gameId) == null) {
            plugin.getLogger().severe("Minigame '" + gameId + "' is not in actions.yml!");
            return true;
        }

        World world = Bukkit.getWorld(actionsFile.getString(gameId + ".world", "null"));
        if (world == null) {
            if (sender instanceof Player) sender.sendMessage(Component.text(
                "World '" + actionsFile.getString(
                    gameId + ".world") + "' does not exist!",
                NamedTextColor.RED));

            else plugin.getLogger()
                .severe("World '" + actionsFile.getString(gameId + ".world") + "' does not exist under game ID '" + gameId + "'!");

            return true;
        }

        int x1 = barrierCoords[0];
        int y1 = barrierCoords[1];
        int z1 = barrierCoords[2];
        int x2 = barrierCoords[3];
        int y2 = barrierCoords[4];
        int z2 = barrierCoords[5];

        if (x1 == 0 && y1 == 0 && z1 == 0 && x2 == 0 && y2 == 0 && z2 == 0) {
            plugin.getLogger().severe("Minigame '" + gameId + "' does not have any barriers assigned!");
            return true;
        }

        // Protect against too large coordinates
        if (Math.abs(x1 - x2) > 10 || Math.abs(y1 - y2) > 5 || Math.abs(z1 - z2) > 10) {
            plugin.getLogger().severe("Minigame '" + gameId + "' has too large barrier coordinates!");
            return true;
        }

        return false;
    }

    /// Returns true if the game was evacuated successfully, otherwise false
    private boolean evacuateGame(String gameId) {
        World world = Bukkit.getWorld(actionsFile.getString(gameId + ".world", "null"));
        if (world == null) {
            plugin.getLogger()
                .severe("World '" + actionsFile.getString(gameId + ".world") + "' does not exist!");
            return false;
        }

        boolean fullWorld = actionsFile.getConfigurationSection(gameId + ".game-cuboids") == null;

        // Teleport players out
        Location teleportLoc = new Location(Bukkit.getWorld("utility"), -88, 41, -27, 90, 90);

        for (Player player : world.getPlayers()) {
            if (player.getGameMode() != GameMode.ADVENTURE) continue;

            if (fullWorld) player.teleportAsync(teleportLoc);
            else //noinspection DataFlowIssue
                for (String cuboidId : actionsFile.getConfigurationSection(gameId + ".game-cuboids").getKeys(
                    false)) {

                    // Cuboid(s) of the minigame for teleporting players out
                    int x1 = actionsFile.getInt(gameId + ".game-cuboids." + cuboidId + ".x1");
                    int y1 = actionsFile.getInt(gameId + ".game-cuboids." + cuboidId + ".y1");
                    int z1 = actionsFile.getInt(gameId + ".game-cuboids." + cuboidId + ".z1");
                    int x2 = actionsFile.getInt(gameId + ".game-cuboids." + cuboidId + ".x2");
                    int y2 = actionsFile.getInt(gameId + ".game-cuboids." + cuboidId + ".y2");
                    int z2 = actionsFile.getInt(gameId + ".game-cuboids." + cuboidId + ".z2");

                    Location playerLoc = player.getLocation();
                    boolean inX = playerLoc.getX() >= Math.min(x1, x2) && playerLoc.getX() <= Math.max(
                        x1,
                        x2);
                    boolean inY = playerLoc.getY() >= Math.min(y1, y2) && playerLoc.getY() <= Math.max(
                        y1,
                        y2);
                    boolean inZ = playerLoc.getZ() >= Math.min(z1, z2) && playerLoc.getZ() <= Math.max(
                        z1,
                        z2);

                    if (inX && inY && inZ) {
                        player.teleportAsync(teleportLoc);
                        break; // No need to check other cuboids
                    }
                }
        }
        return true;
    }

    private void setHologram(CommandSender sender, String gameId, MinigameStatus status) {
        Optional<Hologram> optionalHologram = FancyHologramsPlugin.get().getHologramManager().getHologram(
            gameId);

        if (optionalHologram.isEmpty()) {
            sender.sendMessage(Component.text("Hologram not found!", NamedTextColor.RED));
            return;
        }

        HologramData hologramData = optionalHologram.get().getData();
        if (hologramData.getType() != HologramType.TEXT) {
            sender.sendMessage(Component.text("Hologram is not of type TEXT!", NamedTextColor.RED));
            return;
        }

        Hologram hologram = optionalHologram.get();
        TextHologramData textData = (TextHologramData) hologramData;
        List<String> holoText = new ArrayList<>(textData.getText());
        holoText.set(holoText.size() - 1, status.getText());
        textData.setText(holoText);
        hologram.forceUpdate();
    }

    private void queueStatusUpdate() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastUpdate = currentTime - lastStatusUpdate;
        final int cooldown = 2000;

        // Update immediately if enough time has passed since the last update
        if (timeSinceLastUpdate >= cooldown && statusUpdateTask == null) {
            updateDiscordMessage();
            lastStatusUpdate = currentTime;

        } else if (statusUpdateTask == null) statusUpdateTask = new BukkitRunnable() {
            @Override
            public void run() {
                updateDiscordMessage();

                lastStatusUpdate = System.currentTimeMillis();
                statusUpdateTask = null;
            }
        }.runTaskLaterAsynchronously(plugin, cooldown / 50);
    }


    // game_id will coincide with the hologram name
    // mgm list
    // mgm delete <game_id>
    // mgm create <game_id> <friendly_name> <SINGLE|MULTI>
    // mgm set    <game_id> friendlyname    <name>
    // mgm set    <game_id> players         <SINGLE|MULTI>
    // mgm set    <game_id> status          <open|in_session|resetting|closed> [-a]
    //      0         1       2               3                                  4
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
            List<String> minigameIds = new ArrayList<>(dataFile.getConfigurationSection("minigame-data")
                .getKeys(false));

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
            List<String> friendlyName = new ArrayList<>(List.of(dataFile
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
                // Don't include READY as it's combined with OPEN when using set (refers to actions.yml)
                if (args[2].equalsIgnoreCase("status")) arguments.addAll(Arrays
                    .stream(MinigameStatus.values()).filter(status -> status != MinigameStatus.READY)
                    .map(Enum::name).toList());

                for (String a : arguments)
                    if (a.toLowerCase().startsWith(args[3].toLowerCase())) result.add(a);
                return result;
            }

            if (args.length == 5) {
                List<String> arguments = new ArrayList<>();

                // Announce flag for "set <game> status"
                if (args[2].equalsIgnoreCase("status")) arguments = new ArrayList<>(List.of("-a"));

                for (String a : arguments)
                    if (a.toLowerCase().startsWith(args[4].toLowerCase())) result.add(a);
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
                    plugin.getLogger().severe("Minigames Discord channel not found!");
                    return;
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Minigame Status");
                embed.setColor(Color.decode("#37E67D"));

                // Get all minigames (sorted alphabetically by friendly name)
                // In order to do so, the friendly names are the keys instead of the game IDs
                Map<String, MinigameStatus> minigames = new HashMap<>();

                //noinspection DataFlowIssue
                for (String gameId : dataFile.getConfigurationSection("minigame-data").getKeys(false)) {
                    String friendlyName = dataFile.getString(
                        "minigame-data." + gameId + ".friendly-name",
                        gameId);
                    MinigameStatus status = MinigameStatus.valueOf(dataFile.getString(
                        "minigame-data." + gameId + ".status",
                        "NULL"));

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
        }.runTaskAsynchronously(plugin);
    }
}
