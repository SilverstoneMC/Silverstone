package net.silverstonemc.silverstoneminigames.minigames;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silverstonemc.silverstoneminigames.SilverstoneMinigames;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("DataFlowIssue")
public record FlyingCourse(JavaPlugin plugin) implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String @NotNull [] args) {
        if (cmd.getName().equalsIgnoreCase("fcfinish")) {
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage(Component.text("Please provide an online player!", NamedTextColor.RED));
                return true;
            }

            if (player.getGameMode() != GameMode.ADVENTURE) return true;

            new BukkitRunnable() {
                @Override
                public void run() {
                    Location location = new Location(
                        Bukkit.getWorld(plugin.getConfig().getString("flying-course-world")),
                        23.5,
                        132,
                        88.5,
                        0,
                        10);

                    switch (args[1].toLowerCase()) {
                        case "easy" -> {
                            sendFinishMessage(Difficulty.EASY);
                            addToTopScores("easy");
                            player.teleportAsync(location);
                        }
                        case "medium" -> {
                            sendFinishMessage(Difficulty.MEDIUM);
                            addToTopScores("medium");
                            player.teleportAsync(location);
                        }
                        case "hard" -> {
                            sendFinishMessage(Difficulty.HARD);
                            addToTopScores("hard");
                            player.teleportAsync(location);
                        }
                        case "extended" -> {
                            sendFinishMessage(Difficulty.EXTENDED);
                            addToTopScores("extended");
                            player.teleportAsync(location);
                        }
                        default -> sender.sendMessage(Component.text(
                            "Please provide a valid difficulty!",
                            NamedTextColor.RED));
                    }
                }

                private void addToTopScores(String difficulty) {
                    int score = 0;
                    if (SilverstoneMinigames.data.getConfig()
                        .contains("data." + player.getUniqueId() + ".flyingcourse." + difficulty))
                        score = SilverstoneMinigames.data.getConfig()
                            .getInt("data." + player.getUniqueId() + ".flyingcourse." + difficulty);
                    SilverstoneMinigames.data.getConfig()
                        .set("data." + player.getUniqueId() + ".flyingcourse." + difficulty, ++score);
                    SilverstoneMinigames.data.saveConfig();

                    updateFCScoreboard();
                }

                private void sendFinishMessage(Difficulty difficulty) {
                    String difficultyType = null;
                    NamedTextColor namedTextColor = null;

                    switch (difficulty) {
                        case EASY -> {
                            difficultyType = "Easy";
                            namedTextColor = NamedTextColor.DARK_GREEN;
                        }
                        case MEDIUM -> {
                            difficultyType = "Medium";
                            namedTextColor = NamedTextColor.GOLD;
                        }
                        case HARD -> {
                            difficultyType = "Hard";
                            namedTextColor = NamedTextColor.RED;
                        }
                        case EXTENDED -> {
                            difficultyType = "Extended";
                            namedTextColor = NamedTextColor.GRAY;
                        }
                    }

                    for (Player players : Bukkit.getOnlinePlayers())
                        if (players.getWorld().getName().equalsIgnoreCase(plugin.getConfig()
                            .getString("flying-course-world"))) players.sendMessage(Component.text().append(
                                Component.text("NOTICE", NamedTextColor.RED, TextDecoration.BOLD)).append(
                                Component.text(" > ", NamedTextColor.AQUA, TextDecoration.BOLD))
                            .append(Component.text(
                                player.getName() + " just finished the ",
                                NamedTextColor.GREEN)).append(Component.text(difficultyType, namedTextColor))
                            .append(Component.text(" course!", NamedTextColor.GREEN)).build());
                }
            }.runTaskLater(plugin, 5);
            return true;

        } else if (cmd.getName().equalsIgnoreCase("flyingcourse")) {
            if (args.length > 0) {
                // There should only be players in the selector with certain tags
                List<Entity> selector = Bukkit.selectEntities(sender, args[0]);
                try {
                    for (Entity entity : selector) {
                        Player player = Bukkit.getPlayer(entity.getName());
                        if (player == null) break;
                        if (player.getGameMode() != GameMode.ADVENTURE) break;

                        // Check all the blocks around the player
                        for (int x = 0; x < 6; x++) {
                            Block block = switch (x) {
                                case 0 -> player.getLocation().subtract(0, 0.01, 0).getBlock();
                                case 1 -> player.getLocation().add(0, 0.5, 0).getBlock();
                                case 2 -> player.getLocation().add(0.5, 0, 0).getBlock();
                                case 3 -> player.getLocation().subtract(0.5, 0, 0).getBlock();
                                case 4 -> player.getLocation().add(0, 0, 0.5).getBlock();
                                case 5 -> player.getLocation().subtract(0, 0, 0.5).getBlock();
                                default -> null;
                            };
                            // If touching a block that's not allowed, send them back
                            if (block.getType() != Material.AIR && block.getType() != Material.CAVE_AIR && block.getType() != Material.MAGENTA_GLAZED_TERRACOTTA && block.getType() != Material.BLACK_STAINED_GLASS) {
                                player.teleportAsync(new Location(player.getWorld(), 23.5, 132, 88.5, 0, 10));
                                break;
                            }
                        }

                        // Check if the boost block is anywhere near the player
                        boolean boostBlockNear = false;
                        for (int y = -3; y < 0; y++)
                            if (player.getLocation().add(0, y, 0).getBlock()
                                .getType() == Material.MAGENTA_GLAZED_TERRACOTTA) boostBlockNear = true;

                        // If near speed boost blocks, give boost
                        if (boostBlockNear) {
                            Vector speed = player.getVelocity();
                            // If speed is greater than 0.05 or -0.05
                            if (((speed.getX() > 0.05) || (speed.getX() < -0.05)) || ((speed.getZ() > 0.05) || (speed.getZ() < -0.05))) {
                                player.setVelocity(speed.multiply(1.075));
                                player.playSound(
                                    player.getLocation(),
                                    Sound.BLOCK_BEACON_ACTIVATE,
                                    SoundCategory.BLOCKS,
                                    10,
                                    2);
                            }
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    sender.sendMessage(Component.text(
                        "Please provide a valid selector!",
                        NamedTextColor.RED));
                }
                return true;
            }
            return false;
        }
        return true;
    }

    public void updateFCScoreboard() {
        if (Bukkit.getScoreboardManager().getMainScoreboard().getObjective("FCTop") == null) {
            plugin.getLogger().severe("Couldn't find the scoreboard objective 'FCTop'!");
            return;
        }

        Scoreboard fctop = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("FCTop")
            .getScoreboard();
        FileConfiguration config = SilverstoneMinigames.data.getConfig();
        Map<OfflinePlayer, Integer> easyScores = new HashMap<>();
        Map<OfflinePlayer, Integer> mediumScores = new HashMap<>();
        Map<OfflinePlayer, Integer> hardScores = new HashMap<>();
        Map<OfflinePlayer, Integer> extendedScores = new HashMap<>();

        // Stop if there's no data
        try {
            config.getConfigurationSection("data").getKeys(false);
        } catch (NullPointerException ignored) {
            return;
        }
        for (String uuid : config.getConfigurationSection("data").getKeys(false)) {
            if (config.contains("data." + uuid + ".flyingcourse.easy"))
                easyScores.put(
                    Bukkit.getOfflinePlayer(UUID.fromString(uuid)),
                    config.getInt("data." + uuid + ".flyingcourse.easy"));

            if (config.contains("data." + uuid + ".flyingcourse.medium"))
                mediumScores.put(
                    Bukkit.getOfflinePlayer(UUID.fromString(uuid)),
                    config.getInt("data." + uuid + ".flyingcourse.medium"));

            if (config.contains("data." + uuid + ".flyingcourse.hard"))
                hardScores.put(
                    Bukkit.getOfflinePlayer(UUID.fromString(uuid)),
                    config.getInt("data." + uuid + ".flyingcourse.hard"));

            if (config.contains("data." + uuid + ".flyingcourse.extended"))
                extendedScores.put(
                    Bukkit.getOfflinePlayer(UUID.fromString(uuid)),
                    config.getInt("data." + uuid + ".flyingcourse.extended"));
        }

        easyScores = sortScores(easyScores);
        mediumScores = sortScores(mediumScores);
        hardScores = sortScores(hardScores);
        extendedScores = sortScores(extendedScores);

        for (String score : fctop.getEntries()) fctop.resetScores(score);

        int score = 15;

        int x = 0;
        fctop.getObjective("FCTop").getScore("§a§lEasy:").setScore(score);
        for (Map.Entry<OfflinePlayer, Integer> entry : easyScores.entrySet()) {
            if (x >= 2) break;
            fctop.getObjective("FCTop").getScore("§2" + entry.getKey().getName() + ": §b" + entry.getValue())
                .setScore(--score);
            x++;
        }

        x = 0;
        fctop.getObjective("FCTop").getScore("§e§lMedium:").setScore(--score);
        for (Map.Entry<OfflinePlayer, Integer> entry : mediumScores.entrySet()) {
            if (x >= 3) break;
            fctop.getObjective("FCTop").getScore("§6" + entry.getKey().getName() + ": §b" + entry.getValue())
                .setScore(--score);
            x++;
        }

        x = 0;
        fctop.getObjective("FCTop").getScore("§4§lHard:").setScore(--score);
        for (Map.Entry<OfflinePlayer, Integer> entry : hardScores.entrySet()) {
            if (x >= 3) break;
            fctop.getObjective("FCTop").getScore("§c" + entry.getKey().getName() + ": §b" + entry.getValue())
                .setScore(--score);
            x++;
        }

        x = 0;
        fctop.getObjective("FCTop").getScore("§8§lExtended:").setScore(--score);
        for (Map.Entry<OfflinePlayer, Integer> entry : extendedScores.entrySet()) {
            if (x >= 3) break;
            fctop.getObjective("FCTop").getScore("§7" + entry.getKey().getName() + ": §b" + entry.getValue())
                .setScore(--score);
            x++;
        }
    }

    private Map<OfflinePlayer, Integer> sortScores(Map<OfflinePlayer, Integer> scores) {
        LinkedHashMap<OfflinePlayer, Integer> reverseSortedMap = new LinkedHashMap<>();
        scores.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
        return reverseSortedMap;
    }

    private enum Difficulty {
        EASY, MEDIUM, HARD, EXTENDED
    }
}
