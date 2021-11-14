package me.jasonhorkles.silverstoneglobal.commands.guis;

import me.jasonhorkles.silverstoneglobal.SilverstoneGlobal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BuyGUI implements CommandExecutor, Listener {

    private static SilverstoneGlobal plugin;

    public BuyGUI(SilverstoneGlobal plugin) {
        BuyGUI.plugin = plugin;
    }

    private static Inventory defaultInv;
    private static Inventory inv1;
    private static Inventory inv2;
    private static Inventory inv3;
    private static Inventory inv4;

    public void closeInv(Player player) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            }
        };
        task.runTask(plugin);
    }

    public void openInv(Player player, Inventory inv) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                player.openInventory(inv);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            }
        };
        task.runTask(plugin);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
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

        if (!event.getInventory().equals(defaultInv) && !event.getInventory().equals(inv1) && !event.getInventory()
                .equals(inv2) && !event.getInventory()
                .equals(inv3) && !event.getInventory().equals(inv4))
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
            case 11:
                // Member
                // If already on 1
                if (event.getInventory().equals(inv1)) openInv(player, defaultInv);
                else openInv(player, inv1);
                break;

            case 12:
                // VIP
                // If already on 3
                if (event.getInventory().equals(inv2)) openInv(player, defaultInv);
                else openInv(player, inv2);
                break;

            case 13:
                // VIP+
                // If already on 4
                if (event.getInventory().equals(inv3)) openInv(player, defaultInv);
                else openInv(player, inv3);
                break;

            case 14:
                // MVP
                // If already on 5
                if (event.getInventory().equals(inv4)) openInv(player, defaultInv);
                else openInv(player, inv4);
        }
    }

    // Default inventory items
    public static void createDefaultInv() {

        defaultInv = Bukkit.createInventory(null, 27, Component.text("Available Ranks")
                .color(TextColor.fromHexString("#048a3e"))
                .decorate(TextDecoration.BOLD));

        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        // Fill items
        meta.displayName(Component.text(ChatColor.BOLD + ""));
        List<String> lore = new ArrayList<>();
        meta.setLore(lore);
        item.setItemMeta(meta);
        IntStream.rangeClosed(0, 26)
                .boxed()
                .collect(Collectors.toList())
                .forEach(slot -> defaultInv.setItem(slot, item));

        // Member
        item.setType(Material.IRON_INGOT);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Member"));
        for (String customLore : plugin.getConfig().getStringList("buyGUI.Member.1"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        defaultInv.setItem(11, item);

        // VIP
        item.setType(Material.EMERALD);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "VIP"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buyGUI.VIP.1"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        defaultInv.setItem(12, item);

        // VIP+
        item.setType(Material.DIAMOND);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "VIP+"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buyGUI.VIP+.1"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        defaultInv.setItem(13, item);

        // MVP
        item.setType(Material.NETHERITE_INGOT);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "MVP"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buyGUI.MVP.1"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        defaultInv.setItem(14, item);

        // Donate
        item.setType(Material.WRITABLE_BOOK);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Donate"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buyGUI.Donate.1"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        defaultInv.setItem(15, item);
    }

    // Member item
    public static void createInv1() {

        inv1 = Bukkit.createInventory(null, 27, Component.text("Available Ranks")
                .color(TextColor.fromHexString("#048a3e"))
                .decorate(TextDecoration.BOLD));

        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        // Fill items
        meta.displayName(Component.text(ChatColor.BOLD + ""));
        List<String> lore = new ArrayList<>();
        meta.setLore(lore);
        item.setItemMeta(meta);
        IntStream.rangeClosed(0, 26).boxed().collect(Collectors.toList()).forEach(slot -> inv1.setItem(slot, item));

        // Member
        item.setType(Material.IRON_INGOT);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Member"));
        for (String customLore : plugin.getConfig().getStringList("buyGUI.Member.2"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv1.setItem(11, item);

        // VIP
        item.setType(Material.EMERALD);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "VIP"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buyGUI.VIP.1"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv1.setItem(12, item);

        // VIP+
        item.setType(Material.DIAMOND);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "VIP+"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buyGUI.VIP+.1"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv1.setItem(13, item);

        // MVP
        item.setType(Material.NETHERITE_INGOT);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "MVP"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buyGUI.MVP.1"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv1.setItem(14, item);

        // Donate
        item.setType(Material.WRITABLE_BOOK);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Donate"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buyGUI.Donate.1"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv1.setItem(15, item);
    }

    // VIP item
    public static void createInv2() {

        inv2 = Bukkit.createInventory(null, 27, Component.text("Available Ranks")
                .color(TextColor.fromHexString("#048a3e"))
                .decorate(TextDecoration.BOLD));

        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        // Fill items
        meta.displayName(Component.text(ChatColor.BOLD + ""));
        List<String> lore = new ArrayList<>();
        meta.setLore(lore);
        item.setItemMeta(meta);
        IntStream.rangeClosed(0, 26).boxed().collect(Collectors.toList()).forEach(slot -> inv2.setItem(slot, item));

        // Member
        item.setType(Material.IRON_INGOT);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Member"));
        for (String customLore : plugin.getConfig().getStringList("buyGUI.Member.1"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv2.setItem(11, item);

        // VIP
        item.setType(Material.EMERALD);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "VIP"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buyGUI.VIP.2"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv2.setItem(12, item);

        // VIP+
        item.setType(Material.DIAMOND);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "VIP+"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buyGUI.VIP+.1"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv2.setItem(13, item);

        // MVP
        item.setType(Material.NETHERITE_INGOT);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "MVP"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buyGUI.MVP.1"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv2.setItem(14, item);

        // Donate
        item.setType(Material.WRITABLE_BOOK);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Donate"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buyGUI.Donate.1"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv2.setItem(15, item);
    }

    // VIP+ item
    public static void createInv3() {

        inv3 = Bukkit.createInventory(null, 27, Component.text("Available Ranks")
                .color(TextColor.fromHexString("#048a3e"))
                .decorate(TextDecoration.BOLD));

        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        // Fill items
        meta.displayName(Component.text(ChatColor.BOLD + ""));
        List<String> lore = new ArrayList<>();
        meta.setLore(lore);
        item.setItemMeta(meta);
        IntStream.rangeClosed(0, 26).boxed().collect(Collectors.toList()).forEach(slot -> inv3.setItem(slot, item));

        // Member
        item.setType(Material.IRON_INGOT);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Member"));
        for (String customLore : plugin.getConfig().getStringList("buyGUI.Member.1"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv3.setItem(11, item);

        // VIP
        item.setType(Material.EMERALD);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "VIP"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buyGUI.VIP.1"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv3.setItem(12, item);

        // VIP+
        item.setType(Material.DIAMOND);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "VIP+"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buyGUI.VIP+.2"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv3.setItem(13, item);

        // MVP
        item.setType(Material.NETHERITE_INGOT);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "MVP"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buyGUI.MVP.1"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv3.setItem(14, item);

        // Donate
        item.setType(Material.WRITABLE_BOOK);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Donate"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buyGUI.Donate.1"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv3.setItem(15, item);
    }

    // MVP item
    public static void createInv4() {

        inv4 = Bukkit.createInventory(null, 27, Component.text("Available Ranks")
                .color(TextColor.fromHexString("#048a3e"))
                .decorate(TextDecoration.BOLD));

        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        // Fill items
        meta.displayName(Component.text(ChatColor.BOLD + ""));
        List<String> lore = new ArrayList<>();
        meta.setLore(lore);
        item.setItemMeta(meta);
        IntStream.rangeClosed(0, 26).boxed().collect(Collectors.toList()).forEach(slot -> inv4.setItem(slot, item));

        // Member
        item.setType(Material.IRON_INGOT);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Member"));
        for (String customLore : plugin.getConfig().getStringList("buyGUI.Member.1"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv4.setItem(11, item);

        // VIP
        item.setType(Material.EMERALD);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "VIP"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buyGUI.VIP.1"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv4.setItem(12, item);

        // VIP+
        item.setType(Material.DIAMOND);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "VIP+"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buyGUI.VIP+.1"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv4.setItem(13, item);

        // MVP
        item.setType(Material.NETHERITE_INGOT);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "MVP"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buyGUI.MVP.2"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv4.setItem(14, item);

        // Donate
        item.setType(Material.WRITABLE_BOOK);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Donate"));
        lore.clear();
        for (String customLore : plugin.getConfig().getStringList("buyGUI.Donate.1"))
            lore.add(ChatColor.translateAlternateColorCodes('&', customLore));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv4.setItem(15, item);
    }
}