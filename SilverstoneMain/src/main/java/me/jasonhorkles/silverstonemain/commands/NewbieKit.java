package me.jasonhorkles.silverstonemain.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public record NewbieKit(JavaPlugin plugin) implements CommandExecutor {

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length < 1) return false;
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) return false;
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                player.setGameMode(GameMode.SURVIVAL);
            }
        };
        task.runTaskLater(plugin, 40);

        BukkitRunnable task2 = new BukkitRunnable() {
            @Override
            public void run() {
                player.getInventory().addItem(new ItemStack(Material.STONE_SWORD));
                player.getInventory().addItem(new ItemStack(Material.STONE_AXE));
                player.getInventory().addItem(new ItemStack(Material.CYAN_BED));
                player.getInventory().addItem(new ItemStack(Material.COOKIE));
            }
        };
        task2.runTaskLater(plugin, 50);

        BukkitRunnable task3 = new BukkitRunnable() {
            @Override
            public void run() {
                player.setGameMode(GameMode.ADVENTURE);
            }
        };
        task3.runTaskLater(plugin, 80);
        return true;
    }
}
