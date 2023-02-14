package net.silverstonemc.silverstoneglobal.commands.guis;

import net.silverstonemc.silverstoneglobal.SilverstoneGlobal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class BuyGUI implements CommandExecutor, Listener {

    private static SilverstoneGlobal plugin;
    public static Inventory inv;

    public BuyGUI(SilverstoneGlobal plugin) {
        BuyGUI.plugin = plugin;
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
        Inventory inventory = event.getInventory();

        if (!inventory.equals(inv)) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;

        event.setCancelled(true);

        switch (event.getRawSlot()) {
            case 11 -> {
                // Member
                player.sendMessage("");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aPurchase the &bMember &arank at: &bsilverstone.craftingstore.net/category/261250"));
                closeInv(player);
            }
            case 12 -> {
                // VIP
                player.sendMessage("");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aPurchase the &bVIP &arank at: &bsilverstone.craftingstore.net/category/261250"));
                closeInv(player);
            }
            case 13 -> {
                // VIP+
                player.sendMessage("");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aPurchase the &bVIP+ &arank at: &bsilverstone.craftingstore.net/category/261250"));
                closeInv(player);
            }
            case 14 -> {
                // MVP
                player.sendMessage("");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aPurchase the &bMVP &arank at: &bsilverstone.craftingstore.net/category/261250"));
                closeInv(player);
            }
            case 15 -> {
                // Donate
                player.sendMessage("");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDonate to the server at: &bsilverstone.craftingstore.net/category/261655"));
                closeInv(player);
            }
        }
    }

    public static Inventory createInv() {
        Inventory inventory = Bukkit.createInventory(null, 27, Component.text("Available Ranks")
                .color(TextColor.fromHexString("#048a3e"))
                .decorate(TextDecoration.BOLD));

        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = new ArrayList<>();

        // Fill items
        meta.displayName(Component.text(ChatColor.BOLD + ""));
        item.setItemMeta(meta);
        IntStream.rangeClosed(0, 26).boxed().toList().forEach(slot -> inventory.setItem(slot, item));

        // Member
        item.setType(Material.IRON_INGOT);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Member"));
        for (String customLore : plugin.getConfig().getStringList("buy-gui.member"))
            lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', customLore)));
        meta.lore(lore);
        item.setItemMeta(meta);
        inventory.setItem(11, item);

        // VIP
        item.setType(Material.EMERALD);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "VIP"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buy-gui.vip"))
            lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', customLore)));
        meta.lore(lore);
        item.setItemMeta(meta);
        inventory.setItem(12, item);

        // VIP+
        item.setType(Material.DIAMOND);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "VIP+"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buy-gui.vip+"))
            lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', customLore)));
        meta.lore(lore);
        item.setItemMeta(meta);
        inventory.setItem(13, item);

        // MVP
        item.setType(Material.NETHERITE_INGOT);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "MVP"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buy-gui.mvp"))
            lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', customLore)));
        meta.lore(lore);
        item.setItemMeta(meta);
        inventory.setItem(14, item);

        // Donate
        item.setType(Material.WRITABLE_BOOK);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Donate"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buy-gui.donate"))
            lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', customLore)));
        meta.lore(lore);
        item.setItemMeta(meta);
        inventory.setItem(15, item);

        return inventory;
    }
}