package net.silverstonemc.silverstoneminigames;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class BossBarManager implements CommandExecutor {
    public static final HashMap<Player, BossBar> bossBars = new HashMap<>();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length > 0) {
            List<Entity> selector = Bukkit.selectEntities(sender, args[0]);
            try {
                for (Entity entity : selector) {
                    Player player = Bukkit.getPlayer(entity.getName());
                    if (player == null) continue;

                    removeBossBar(player);
                }
            } catch (IndexOutOfBoundsException e) {
                sender.sendMessage(Component.text("Please provide a valid selector!", NamedTextColor.RED));
            }

            return true;
        }
        return false;
    }

    public void createBossBar(Player player, Component name, float progress, BossBar.Color color, BossBar.Overlay overlay) {
        if (bossBars.containsKey(player)) removeBossBar(player);

        BossBar bossBar = BossBar.bossBar(name, progress, color, overlay);
        player.showBossBar(bossBar);
        bossBars.put(player, bossBar);
    }

    public void setBossBarProgress(Player player, float progress) {
        if (bossBars.containsKey(player)) {
            if (progress > 1.0f) progress = 1.0f;
            else if (progress < 0.0f) progress = 0.0f;

            bossBars.get(player).progress(progress);
        }
    }

    public void setBossBarColor(Player player, BossBar.Color color, TextColor textColor) {
        if (bossBars.containsKey(player)) {
            BossBar bossBar = bossBars.get(player);
            bossBar.color(color);
            bossBar.name(bossBar.name().color(textColor));
        }
    }

    public void removeBossBar(Player player) {
        if (bossBars.containsKey(player)) {
            player.hideBossBar(bossBars.get(player));
            bossBars.remove(player);
        }
    }
}
