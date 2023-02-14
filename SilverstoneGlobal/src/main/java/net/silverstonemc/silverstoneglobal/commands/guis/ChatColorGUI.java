package net.silverstonemc.silverstoneglobal.commands.guis;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silverstonemc.silverstoneglobal.SilverstoneGlobal;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.stream.IntStream;

public class ChatColorGUI implements CommandExecutor, Listener {
    private static Inventory inv;
    private static SilverstoneGlobal plugin;

    public ChatColorGUI(SilverstoneGlobal plugin) {
        ChatColorGUI.plugin = plugin;
    }

    public void closeInv(Player player) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
            }
        };
        task.runTask(plugin);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Sorry, but only players can do that.");
            return true;
        }
        // Open GUI
        player.openInventory(inv);
        return true;
    }

    // On item click
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!event.getInventory().equals(inv)) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;

        event.setCancelled(true);

        switch (event.getRawSlot()) {
            case 10 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &4");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Chat color changed."));
            }

            case 11 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &c");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cChat color changed."));
            }

            case 12 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &6");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Chat color changed."));
            }

            case 13 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &e");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eChat color changed."));
            }

            case 14 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &a");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aChat color changed."));
            }

            case 15 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &2");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2Chat color changed."));
            }

            case 16 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &3");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3Chat color changed."));
            }

            case 19 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &9");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9Chat color changed."));
            }

            case 20 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &1");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&1Chat color changed."));
            }

            case 21 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &5");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5Chat color changed."));
            }

            case 22 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &d");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dChat color changed."));
            }

            case 23 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &8");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8Chat color changed."));
            }

            case 24 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &7");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Chat color changed."));
            }

            case 25 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &f");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fChat color changed."));
            }

            case 31 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta removesuffix 500");
                player.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', "&fChat color reset to default."));
            }

            default -> {
                return;
            }
        }

        closeInv(player);
    }

    // Default inventory items
    public void createDefaultInv() {
        inv = Bukkit.createInventory(null, 45,
            Component.text("Chat Colors").color(TextColor.fromHexString("#a62828"))
                .decorate(TextDecoration.BOLD));

        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        // Fill items
        meta.displayName(Component.text(ChatColor.BOLD + ""));
        item.setItemMeta(meta);
        IntStream.rangeClosed(0, 44).boxed().toList().forEach(slot -> inv.setItem(slot, item));

        item.setType(Material.RED_CONCRETE);
        meta.displayName(Component.text(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Dark Red"));
        item.setItemMeta(meta);
        inv.setItem(10, item);

        item.setType(Material.RED_TERRACOTTA);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Red"));
        item.setItemMeta(meta);
        inv.setItem(11, item);

        item.setType(Material.ORANGE_CONCRETE);
        meta.displayName(Component.text(ChatColor.GOLD + "" + ChatColor.BOLD + "Gold"));
        item.setItemMeta(meta);
        inv.setItem(12, item);

        item.setType(Material.YELLOW_CONCRETE);
        meta.displayName(Component.text(ChatColor.YELLOW + "" + ChatColor.BOLD + "Yellow"));
        item.setItemMeta(meta);
        inv.setItem(13, item);

        item.setType(Material.LIME_CONCRETE);
        meta.displayName(Component.text(ChatColor.GREEN + "" + ChatColor.BOLD + "Green"));
        item.setItemMeta(meta);
        inv.setItem(14, item);

        item.setType(Material.GREEN_CONCRETE);
        meta.displayName(Component.text(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Dark Green"));
        item.setItemMeta(meta);
        inv.setItem(15, item);

        item.setType(Material.LIGHT_BLUE_TERRACOTTA);
        meta.displayName(Component.text(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Dark Aqua"));
        item.setItemMeta(meta);
        inv.setItem(16, item);

        item.setType(Material.BLUE_CONCRETE);
        meta.displayName(Component.text(ChatColor.BLUE + "" + ChatColor.BOLD + "Blue"));
        item.setItemMeta(meta);
        inv.setItem(19, item);

        item.setType(Material.BLUE_TERRACOTTA);
        meta.displayName(Component.text(ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "Dark Blue"));
        item.setItemMeta(meta);
        inv.setItem(20, item);

        item.setType(Material.PURPLE_CONCRETE);
        meta.displayName(Component.text(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Dark Purple"));
        item.setItemMeta(meta);
        inv.setItem(21, item);

        item.setType(Material.MAGENTA_CONCRETE);
        meta.displayName(Component.text(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Purple"));
        item.setItemMeta(meta);
        inv.setItem(22, item);

        item.setType(Material.GRAY_TERRACOTTA);
        meta.displayName(Component.text(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "Dark Gray"));
        item.setItemMeta(meta);
        inv.setItem(23, item);

        item.setType(Material.LIGHT_GRAY_CONCRETE);
        meta.displayName(Component.text(ChatColor.GRAY + "" + ChatColor.BOLD + "Gray"));
        item.setItemMeta(meta);
        inv.setItem(24, item);

        item.setType(Material.WHITE_CONCRETE);
        meta.displayName(Component.text(ChatColor.WHITE + "" + ChatColor.BOLD + "White"));
        item.setItemMeta(meta);
        inv.setItem(25, item);

        item.setType(Material.BARRIER);
        meta.displayName(Component.text(ChatColor.WHITE + "" + ChatColor.BOLD + "Reset to Default"));
        item.setItemMeta(meta);
        inv.setItem(31, item);
    }
}
