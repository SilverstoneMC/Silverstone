package net.silverstonemc.silverstoneminigames.minigames;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silverstonemc.silverstoneminigames.BossBarManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CorruptedTag implements CommandExecutor {
    public CorruptedTag(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;
    private static BukkitRunnable bossBarUpdater = null;

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length > 0) {
            List<Entity> selector = Bukkit.selectEntities(sender, args[0]);

            switch (cmd.getName().toLowerCase()) {
                case "corruptedtagstart" -> {
                    try {
                        for (Entity entity : selector) {
                            Player player = Bukkit.getPlayer(entity.getName());
                            if (player == null) continue;

                            // Create the boss bar
                            new BossBarManager().createBossBar(player,
                                Component.text("Corruption", NamedTextColor.LIGHT_PURPLE,
                                    TextDecoration.BOLD), 0f, BossBar.Color.PURPLE,
                                BossBar.Overlay.NOTCHED_10);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        sender.sendMessage(
                            Component.text("Please provide a valid selector!", NamedTextColor.RED));
                    }

                    // Start the boss bar updater
                    bossBarUpdater = new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (Player players : BossBarManager.bossBars.keySet()) {
                                if (!BossBarManager.bossBars.get(players).name().toString()
                                    .contains("Corruption")) continue;

                                //noinspection DataFlowIssue
                                float value = Bukkit.getScoreboardManager().getMainScoreboard()
                                    .getObjective("mctagcorruption").getScore(players).getScore() / 1000f;
                                new BossBarManager().setBossBarProgress(players, value);
                            }
                        }
                    };
                    bossBarUpdater.runTaskTimer(plugin, 0, 5);
                }

                case "corruptedtagstop" -> {
                    try {
                        for (Entity entity : selector) {
                            Player player = Bukkit.getPlayer(entity.getName());
                            if (player == null) continue;

                            // Remove the boss bar
                            new BossBarManager().removeBossBar(player);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        sender.sendMessage(
                            Component.text("Please provide a valid selector!", NamedTextColor.RED));
                    }

                    // Stop the boss bar updater
                    if (bossBarUpdater != null) {
                        bossBarUpdater.cancel();
                        bossBarUpdater = null;
                    }
                }
            }

            return true;

        }
        return false;
    }
}
