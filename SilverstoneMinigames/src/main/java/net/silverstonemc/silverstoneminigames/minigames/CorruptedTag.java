package net.silverstonemc.silverstoneminigames.minigames;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class CorruptedTag implements CommandExecutor {
    private static final HashMap<Player, BossBar> bossBars = new HashMap<>();
    
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("corruptedtagtest")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(
                    Component.text("Sorry, but only players can do that.").color(NamedTextColor.RED));
                return true;
            }

            float value = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("mctagcorruption")
                .getScore(player).getScore() / 100f;
            
            if (!bossBars.containsKey(player)) {
                BossBar bossBar = BossBar.bossBar(
                    Component.text("Corruption", NamedTextColor.GREEN, TextDecoration.BOLD), value,
                    BossBar.Color.GREEN, BossBar.Overlay.NOTCHED_10);
                player.showBossBar(bossBar);
                bossBars.put(player, bossBar);
            } else {
                bossBars.get(player).progress(value);
            }
            return true;

            // Clear boss bars
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (bossBars.containsKey(player)) {
                    player.hideBossBar(bossBars.get(player));
                    bossBars.remove(player);
                }
            }
            return true;
        }
    }
}
