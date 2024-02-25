package net.silverstonemc.silverstoneminigames.minigames;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record TNTRun(JavaPlugin plugin) implements CommandExecutor {
    public void deleteBlocks(Block block) {
        new BukkitRunnable() {
            @Override
            public void run() {
                block.setType(Material.AIR);
                block.getLocation().subtract(0, 1, 0).getBlock().setType(Material.AIR);
            }
        }.runTaskLater(plugin, 15);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length > 0) {
            List<Entity> selector = Bukkit.selectEntities(sender, args[0]);
            try {
                for (Entity entity : selector) {
                    Player player = Bukkit.getPlayer(entity.getName());
                    if (player == null) continue;
                    if (!player.getGameMode().equals(GameMode.ADVENTURE)) continue;

                    int[] xOffset = {-1, 1, -1, 1};
                    int[] zOffset = {-1, -1, 1, 1};
                    for (int i = 0; i < 4; i++) {
                        Block block = player.getLocation().subtract(player.getWidth() / 2 * xOffset[i], 1,
                            player.getWidth() / 2 * zOffset[i]).getBlock();
                        switch (block.getType()) {
                            case SAND, RED_SAND, GRAVEL, ORANGE_CONCRETE_POWDER -> deleteBlocks(block);
                        }
                    }

                }
            } catch (IndexOutOfBoundsException e) {
                sender.sendMessage(Component.text("Please provide a valid selector!", NamedTextColor.RED));
            }
            return true;
        }
        return false;
    }
}
