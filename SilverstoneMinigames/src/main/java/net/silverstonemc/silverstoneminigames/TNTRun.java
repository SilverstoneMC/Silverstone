package net.silverstonemc.silverstoneminigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
                    if (player == null) break;
                    if (!player.getGameMode().equals(GameMode.ADVENTURE)) break;

                    Block block1 = player.getLocation()
                        .subtract(player.getWidth() / 2, 1, player.getWidth() / 2).getBlock();
                    Block block2 = player.getLocation()
                        .subtract(-player.getWidth() / 2, 1, player.getWidth() / 2).getBlock();
                    Block block3 = player.getLocation()
                        .subtract(player.getWidth() / 2, 1, -player.getWidth() / 2).getBlock();
                    Block block4 = player.getLocation()
                        .subtract(-player.getWidth() / 2, 1, -player.getWidth() / 2).getBlock();

                    switch (block1.getType()) {
                        case SAND, RED_SAND, GRAVEL, BLACK_CONCRETE_POWDER, WHITE_CONCRETE_POWDER, RED_CONCRETE_POWDER, ORANGE_CONCRETE_POWDER, YELLOW_CONCRETE_POWDER, GREEN_CONCRETE_POWDER, LIME_CONCRETE_POWDER, CYAN_CONCRETE_POWDER, BLUE_CONCRETE_POWDER, LIGHT_BLUE_CONCRETE_POWDER, PURPLE_CONCRETE_POWDER, MAGENTA_CONCRETE_POWDER, PINK_CONCRETE_POWDER, GRAY_CONCRETE_POWDER, LIGHT_GRAY_CONCRETE_POWDER, BROWN_CONCRETE_POWDER ->
                            deleteBlocks(block1);
                    }

                    switch (block2.getType()) {
                        case SAND, RED_SAND, GRAVEL, BLACK_CONCRETE_POWDER, WHITE_CONCRETE_POWDER, RED_CONCRETE_POWDER, ORANGE_CONCRETE_POWDER, YELLOW_CONCRETE_POWDER, GREEN_CONCRETE_POWDER, LIME_CONCRETE_POWDER, CYAN_CONCRETE_POWDER, BLUE_CONCRETE_POWDER, LIGHT_BLUE_CONCRETE_POWDER, PURPLE_CONCRETE_POWDER, MAGENTA_CONCRETE_POWDER, PINK_CONCRETE_POWDER, GRAY_CONCRETE_POWDER, LIGHT_GRAY_CONCRETE_POWDER, BROWN_CONCRETE_POWDER ->
                            deleteBlocks(block2);
                    }

                    switch (block3.getType()) {
                        case SAND, RED_SAND, GRAVEL, BLACK_CONCRETE_POWDER, WHITE_CONCRETE_POWDER, RED_CONCRETE_POWDER, ORANGE_CONCRETE_POWDER, YELLOW_CONCRETE_POWDER, GREEN_CONCRETE_POWDER, LIME_CONCRETE_POWDER, CYAN_CONCRETE_POWDER, BLUE_CONCRETE_POWDER, LIGHT_BLUE_CONCRETE_POWDER, PURPLE_CONCRETE_POWDER, MAGENTA_CONCRETE_POWDER, PINK_CONCRETE_POWDER, GRAY_CONCRETE_POWDER, LIGHT_GRAY_CONCRETE_POWDER, BROWN_CONCRETE_POWDER ->
                            deleteBlocks(block3);
                    }

                    switch (block4.getType()) {
                        case SAND, RED_SAND, GRAVEL, BLACK_CONCRETE_POWDER, WHITE_CONCRETE_POWDER, RED_CONCRETE_POWDER, ORANGE_CONCRETE_POWDER, YELLOW_CONCRETE_POWDER, GREEN_CONCRETE_POWDER, LIME_CONCRETE_POWDER, CYAN_CONCRETE_POWDER, BLUE_CONCRETE_POWDER, LIGHT_BLUE_CONCRETE_POWDER, PURPLE_CONCRETE_POWDER, MAGENTA_CONCRETE_POWDER, PINK_CONCRETE_POWDER, GRAY_CONCRETE_POWDER, LIGHT_GRAY_CONCRETE_POWDER, BROWN_CONCRETE_POWDER ->
                            deleteBlocks(block4);
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                sender.sendMessage(ChatColor.RED + "Please provide a valid selector!");
            }
            return true;
        }
        return false;
    }
}
