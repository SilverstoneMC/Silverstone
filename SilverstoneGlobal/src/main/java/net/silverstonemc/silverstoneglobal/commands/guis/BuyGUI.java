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

    public BuyGUI(SilverstoneGlobal plugin) {
        BuyGUI.plugin = plugin;
    }

    public static Inventory defaultInv;
    public static Inventory inv1;
    public static Inventory inv2;
    public static Inventory inv3;
    public static Inventory inv4;

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

    public void openInv(Player player, Inventory inv) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                player.openInventory(inv);
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
        player.openInventory(defaultInv);
        return true;
    }

    // On item click
    @EventHandler
    public void onClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();

        if (!inventory.equals(defaultInv) && !inventory.equals(inv1) && !inventory.equals(inv2) && !inventory.equals(inv3) && !inventory.equals(inv4))
            return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;

        event.setCancelled(true);

        // If left clicked
        if (event.isLeftClick()) switch (event.getRawSlot()) {
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
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aPurchase the &bVIP+ &arank at: &bsilverstone.craftingstore.net/category/261247"));
                closeInv(player);
            }
            case 14 -> {
                // MVP
                player.sendMessage("");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aPurchase the &bMVP &arank at: &bsilverstone.craftingstore.net/category/261247"));
                closeInv(player);
            }
        }
        if ((event.isLeftClick() || event.isRightClick()) && (event.getRawSlot() == 15)) {
            // Donate
            player.sendMessage("");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDonate to the server at: &bsilverstone.craftingstore.net/category/261250"));
            closeInv(player);
        }

        // If right clicked
        if (event.isRightClick()) switch (event.getRawSlot()) {
            case 11 -> {
                // Member
                // If already on 1
                if (event.getInventory().equals(inv1)) openInv(player, defaultInv);
                else openInv(player, inv1);
            }
            case 12 -> {
                // VIP
                // If already on 3
                if (event.getInventory().equals(inv2)) openInv(player, defaultInv);
                else openInv(player, inv2);
            }
            case 13 -> {
                // VIP+
                // If already on 4
                if (event.getInventory().equals(inv3)) openInv(player, defaultInv);
                else openInv(player, inv3);
            }
            case 14 -> {
                // MVP
                // If already on 5
                if (event.getInventory().equals(inv4)) openInv(player, defaultInv);
                else openInv(player, inv4);
            }
        }
    }

    public static Inventory createInv(int page) {
        int member = 1;
        int vip = 1;
        int vipPlus = 1;
        int mvp = 1;
        switch (page) {
            case 1 -> member = 2;
            case 2 -> vip = 2;
            case 3 -> vipPlus = 2;
            case 4 -> mvp = 2;
        }

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
        for (String customLore : plugin.getConfig().getStringList("buyGUI.Member." + member))
            lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', customLore)));
        meta.lore(lore);
        item.setItemMeta(meta);
        inventory.setItem(11, item);

        // VIP
        item.setType(Material.EMERALD);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "VIP"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buyGUI.VIP." + vip))
            lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', customLore)));
        meta.lore(lore);
        item.setItemMeta(meta);
        inventory.setItem(12, item);

        // VIP+
        item.setType(Material.DIAMOND);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "VIP+"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buyGUI.VIP+." + vipPlus))
            lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', customLore)));
        meta.lore(lore);
        item.setItemMeta(meta);
        inventory.setItem(13, item);

        // MVP
        item.setType(Material.NETHERITE_INGOT);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "MVP"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buyGUI.MVP." + mvp))
            lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', customLore)));
        meta.lore(lore);
        item.setItemMeta(meta);
        inventory.setItem(14, item);

        // Donate
        item.setType(Material.WRITABLE_BOOK);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Donate"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buyGUI.Donate.1"))
            lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', customLore)));
        meta.lore(lore);
        item.setItemMeta(meta);
        inventory.setItem(15, item);

        return inventory;
    }
}