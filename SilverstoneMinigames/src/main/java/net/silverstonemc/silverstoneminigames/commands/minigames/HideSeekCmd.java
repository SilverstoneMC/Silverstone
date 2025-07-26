package net.silverstonemc.silverstoneminigames.commands.minigames;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.silverstonemc.silverstoneminigames.minigames.HideSeek;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class HideSeekCmd implements CommandExecutor {
    public HideSeekCmd(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        if (args.length == 0) return false;

        if (args[0].equalsIgnoreCase("reset_taunt_points")) {
            HideSeek.points.clear();
            sender.sendMessage(Component.text("Points reset!", NamedTextColor.GREEN));
            return true;
        }

        // All other subcommands require a selector
        if (args.length == 1) {
            sender.sendMessage(Component.text("Please provide a valid selector!", NamedTextColor.RED));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "assign_block" -> {
                for (Entity players : Bukkit.selectEntities(sender, args[1])) {
                    if (!(players instanceof Player player)) continue;

                    // Assign random tag to each player
                    Random r = new Random();
                    int randomTag = r.nextInt(7);
                    String[] tags = {
                        "HSCornflower",
                        "HSPoppy",
                        "HSOak",
                        "HSSpruce",
                        "HSPrismarine",
                        "HSStone",
                        "HSGrass"
                    };
                    if (player.addScoreboardTag(tags[randomTag])) sender.sendMessage(Component.text("Assigned " + tags[randomTag] + " to " + player.getName(),
                        NamedTextColor.GREEN));
                    else
                        sender.sendMessage(Component.text(
                            "Failed to assign " + tags[randomTag] + " to " + player.getName(),
                            NamedTextColor.RED));
                }
                return true;
            }

            case "heartbeat" -> {
                for (Entity players : Bukkit.selectEntities(sender, args[1])) {
                    if (!(players instanceof Player player)) continue;
                    // 0-5 sec
                    playHeartbeat(player, 24, 8, 5);

                    // 6-10 sec
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            playHeartbeat(player, 20, 6, 3);
                        }
                    }.runTaskLater(plugin, 6 * 20L);

                    // 11-15 sec
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            playHeartbeat(player, 16, 5, 3);
                        }
                    }.runTaskLater(plugin, 11 * 20L);
                }
                return true;
            }

            case "random_taunt" -> {
                List<Entity> player = Bukkit.selectEntities(sender, args[1]);
                if (player.isEmpty()) {
                    sender.sendMessage(Component.text(
                        "No players found with that selector!",
                        NamedTextColor.RED));
                    return true;
                }
                // Assumes @r in the selector
                Player randomPlayer = (Player) player.getFirst();

                Random r = new Random();
                int randomTaunt = r.nextInt(7) + 1;

                HideSeek hideSeek = new HideSeek(plugin);
                switch (randomTaunt) {
                    case 1 -> hideSeek.bark(randomPlayer);
                    case 2 -> hideSeek.ding(randomPlayer);
                    case 3 -> hideSeek.scream(randomPlayer);
                    case 4 -> hideSeek.roar(randomPlayer);
                    case 5 -> hideSeek.explosion(randomPlayer);
                    case 6 -> hideSeek.fireworks(randomPlayer);
                    case 7 -> hideSeek.boom(randomPlayer);
                }
                return true;
            }
        }
        return false;
    }

    private void playHeartbeat(Player player, int speed1, int speed2, int stopAfter) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                Team hidersTeam = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam(
                    "Hiders");
                if (hidersTeam == null) {
                    cancel();
                    plugin.getLogger()
                        .severe("Error playing Hide & Seek heartbeat: 'Hiders' team not found!");
                    return;
                }

                // Check if the player is in the Hiders team
                if (!hidersTeam.getEntries().contains(player.getName())) {
                    cancel();
                    return;
                }

                player.playSound(
                    player.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_BASEDRUM,
                    SoundCategory.PLAYERS,
                    2,
                    0);
                player.addPotionEffect(new PotionEffect(
                    PotionEffectType.SLOWNESS,
                    3,
                    0,
                    false,
                    false,
                    false));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.removePotionEffect(PotionEffectType.SLOWNESS);
                    }
                }.runTaskLater(plugin, 2);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.playSound(
                            player.getLocation(),
                            Sound.BLOCK_NOTE_BLOCK_BASEDRUM,
                            SoundCategory.PLAYERS,
                            2,
                            0);
                        player.addPotionEffect(new PotionEffect(
                            PotionEffectType.SLOWNESS,
                            3,
                            1,
                            false,
                            false,
                            false));

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.removePotionEffect(PotionEffectType.SLOWNESS);
                            }
                        }.runTaskLater(plugin, 2);
                    }
                }.runTaskLater(plugin, speed2);
            }
        };
        task.runTaskTimer(plugin, 0, speed1);

        new BukkitRunnable() {
            @Override
            public void run() {
                task.cancel();
            }
        }.runTaskLater(plugin, (stopAfter * 20L) + 20L);
    }
}