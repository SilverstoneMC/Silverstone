package me.jasonhorkles.silverstonemain.minigames;

import me.jasonhorkles.silverstonemain.SilverstoneMain;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public record FlyingCourse(JavaPlugin plugin) implements CommandExecutor {
    private static final Scoreboard fctop = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("FCTop").getScoreboard();

    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("zfcfinish")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "Sorry, but only players can do that.");
                return true;
            }

            Location location = new Location(Bukkit.getWorld(plugin.getConfig()
                    .getString("amplified-minigame-world")), 23.5, 131, 88.5, 0, 10);

            if (player.getGameMode() != GameMode.ADVENTURE) return false;

            if (player.hasPermission("silverstone.minigames.flyingcourse.easy")) {
                for (Player players : Bukkit.getOnlinePlayers())
                    if (players.getWorld()
                            .getName()
                            .equalsIgnoreCase(plugin.getConfig().getString("amplified-minigame-world")))
                        players.sendMessage(Component.text(ChatColor.translateAlternateColorCodes('&', "&c&lNOTICE &b&l> &a" + player
                                .getName() + " just finished the &aEasy &acourse!")));
                addToTopScores(player, "easy");
                player.teleportAsync(location);

            } else if (player.hasPermission("silverstone.minigames.flyingcourse.medium")) {
                for (Player players : Bukkit.getOnlinePlayers())
                    if (players.getWorld()
                            .getName()
                            .equalsIgnoreCase(plugin.getConfig().getString("amplified-minigame-world")))
                        players.sendMessage(Component.text(ChatColor.translateAlternateColorCodes('&', "&c&lNOTICE &b&l> &a" + player
                                .getName() + " just finished the &6Medium &acourse!")));
                addToTopScores(player, "medium");
                player.teleportAsync(location);

            } else if (player.hasPermission("silverstone.minigames.flyingcourse.hard")) {
                for (Player players : Bukkit.getOnlinePlayers())
                    if (players.getWorld()
                            .getName()
                            .equalsIgnoreCase(plugin.getConfig().getString("amplified-minigame-world")))
                        players.sendMessage(Component.text(ChatColor.translateAlternateColorCodes('&', "&c&lNOTICE &b&l> &a" + player
                                .getName() + " just finished the &cHard &acourse!")));
                addToTopScores(player, "hard");
                player.teleportAsync(location);

            } else if (player.hasPermission("silverstone.minigames.flyingcourse.multiplayer")) {
                for (Player players : Bukkit.getOnlinePlayers())
                    if (players.getWorld()
                            .getName()
                            .equalsIgnoreCase(plugin.getConfig().getString("amplified-minigame-world")))
                        players.sendMessage(Component.text(ChatColor.translateAlternateColorCodes('&', "&c&lNOTICE &b&l> &a" + player
                                .getName() + " just finished the &7Multiplayer &acourse!")));
                player.teleportAsync(location);

            } else
                player.sendMessage(Component.text(ChatColor.translateAlternateColorCodes('&', "&cYou're not in a finish zone!")));

        } else if (cmd.getName().equalsIgnoreCase("flyingcourse")) {
            if (args.length > 0) {
                // There should only be players in the selector with certain tags
                List<Entity> selector = Bukkit.selectEntities(sender, args[0]);
                try {
                    for (Entity entity : selector) {
                        Player player = Bukkit.getPlayer(entity.getName());
                        if (player == null) break;
                        if (!player.getGameMode().equals(GameMode.ADVENTURE)) break;

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
                            // If touching a block that's not air, send them back
                            if (block.getType() != Material.AIR && block.getType() != Material.CAVE_AIR && block.getType() != Material.MAGENTA_GLAZED_TERRACOTTA) {
                                player.teleportAsync(new Location(player.getWorld(), 23.5, 131, 88.5, 0, 10));
                                break;
                            }
                        }

                        // Check if the boost block is anywhere near the player
                        boolean boostBlockNear = false;
                        for (int y = -3; y < 0; y++)
                            if (player.getLocation()
                                    .add(0, y, 0)
                                    .getBlock()
                                    .getType() == Material.MAGENTA_GLAZED_TERRACOTTA) boostBlockNear = true;

                        // If near speed boost blocks, give boost
                        if (boostBlockNear) {
                            Vector speed = player.getVelocity();
                            // If speed is greater than 0.05 or -0.05
                            if (((speed.getX() > 0.05) || (speed.getX() < -0.05)) || ((speed.getZ() > 0.05) || (speed.getZ() < -0.05))) {
                                player.setVelocity(speed.multiply(plugin.getConfig()
                                        .getDouble("fc-speed-boost-multiplier")));
                                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 10, 2);
                            }
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    sender.sendMessage(ChatColor.RED + "Please provide a valid selector!");
                }
                return true;
            }
            return false;
        }
        return true;
    }

    private void addToTopScores(Player player, String difficulty) {
        int score = 0;
        if (SilverstoneMain.data.getConfig().contains("data." + player.getUniqueId() + ".flyingcourse." + difficulty))
            score = SilverstoneMain.data.getConfig()
                    .getInt("data." + player.getUniqueId() + ".flyingcourse." + difficulty);
        SilverstoneMain.data.getConfig().set("data." + player.getUniqueId() + ".flyingcourse." + difficulty, ++score);
        SilverstoneMain.data.saveConfig();

        updateFCScoreboard();
    }

    public void updateFCScoreboard() {
        FileConfiguration config = SilverstoneMain.data.getConfig();
        Map<OfflinePlayer, Integer> easyScores = new HashMap<>();
        Map<OfflinePlayer, Integer> mediumScores = new HashMap<>();
        Map<OfflinePlayer, Integer> hardScores = new HashMap<>();

        // Stop if there's no data
        try {
            config.getConfigurationSection("data").getKeys(false);
        } catch (NullPointerException ignored) {
            return;
        }
        for (String uuid : config.getConfigurationSection("data").getKeys(false)) {
            if (config.contains("data." + uuid + ".flyingcourse.easy"))
                easyScores.put(Bukkit.getOfflinePlayer(UUID.fromString(uuid)), config.getInt("data." + uuid + ".flyingcourse.easy"));

            if (config.contains("data." + uuid + ".flyingcourse.medium"))
                mediumScores.put(Bukkit.getOfflinePlayer(UUID.fromString(uuid)), config.getInt("data." + uuid + ".flyingcourse.medium"));

            if (config.contains("data." + uuid + ".flyingcourse.hard"))
                hardScores.put(Bukkit.getOfflinePlayer(UUID.fromString(uuid)), config.getInt("data." + uuid + ".flyingcourse.hard"));
        }

        easyScores = sortScores(easyScores);
        mediumScores = sortScores(mediumScores);
        hardScores = sortScores(hardScores);

        for (String score : fctop.getEntries()) fctop.resetScores(score);

        int score = 11;

        int x = 0;
        fctop.getObjective("FCTop").getScore(ChatColor.translateAlternateColorCodes('&', "&a&lEasy:")).setScore(score);
        for (OfflinePlayer player : easyScores.keySet()) {
            if (x >= 3) break;
            fctop.getObjective("FCTop").getScore(ChatColor.translateAlternateColorCodes('&', "&2" + player.getName() + ": &b" + easyScores.get(player)))
                    .setScore(--score);
            x++;
        }

        x = 0;
        fctop.getObjective("FCTop").getScore(ChatColor.translateAlternateColorCodes('&', "&e&lMedium:")).setScore(--score);
        for (OfflinePlayer player : mediumScores.keySet()) {
            if (x >= 3) break;
            fctop.getObjective("FCTop").getScore(ChatColor.translateAlternateColorCodes('&', "&6" + player.getName() + ": &b" + mediumScores.get(player)))
                    .setScore(--score);
            x++;
        }

        x = 0;
        fctop.getObjective("FCTop").getScore(ChatColor.translateAlternateColorCodes('&', "&4&lHard:")).setScore(--score);
        for (OfflinePlayer player : hardScores.keySet()) {
            if (x >= 3) break;
            fctop.getObjective("FCTop").getScore(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName() + ": &b" + hardScores.get(player)))
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
}
