package net.silverstonemc.silverstoneglobal.commands.guis;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silverstonemc.silverstoneglobal.SilverstoneGlobal;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
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
        new BukkitRunnable() {
            @Override
            public void run() {
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
            }
        }.runTask(plugin);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(
                Component.text("Sorry, but only players can do that.").color(NamedTextColor.RED));
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
                player.sendMessage(Component.text("Chat color changed.").color(NamedTextColor.DARK_RED));
            }

            case 11 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &c");
                player.sendMessage(Component.text("Chat color changed.").color(NamedTextColor.RED));
            }

            case 12 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &6");
                player.sendMessage(Component.text("Chat color changed.").color(NamedTextColor.GOLD));
            }

            case 13 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &e");
                player.sendMessage(Component.text("Chat color changed.").color(NamedTextColor.YELLOW));
            }

            case 14 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &a");
                player.sendMessage(Component.text("Chat color changed.").color(NamedTextColor.GREEN));
            }

            case 15 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &2");
                player.sendMessage(Component.text("Chat color changed.").color(NamedTextColor.DARK_GREEN));
            }

            case 16 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &3");
                player.sendMessage(Component.text("Chat color changed.").color(NamedTextColor.DARK_AQUA));
            }

            case 19 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &9");
                player.sendMessage(Component.text("Chat color changed.").color(NamedTextColor.BLUE));
            }

            case 20 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &1");
                player.sendMessage(Component.text("Chat color changed.").color(NamedTextColor.DARK_BLUE));
            }

            case 21 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &5");
                player.sendMessage(Component.text("Chat color changed.").color(NamedTextColor.DARK_PURPLE));
            }

            case 22 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &d");
                player.sendMessage(Component.text("Chat color changed.").color(NamedTextColor.LIGHT_PURPLE));
            }

            case 23 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &8");
                player.sendMessage(Component.text("Chat color changed.").color(NamedTextColor.DARK_GRAY));
            }

            case 24 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &7");
                player.sendMessage(Component.text("Chat color changed.").color(NamedTextColor.GRAY));
            }

            case 25 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta setsuffix 500 &f");
                player.sendMessage(Component.text("Chat color changed."));
            }

            case 31 -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "hopecommander lpb user " + player.getName() + " meta removesuffix 500");
                player.sendMessage(Component.text("Chat color reset to default."));
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
        meta.displayName(Component.text(""));
        item.setItemMeta(meta);
        IntStream.rangeClosed(0, 44).boxed().toList().forEach(slot -> inv.setItem(slot, item));

        item.setType(Material.RED_CONCRETE);
        meta.displayName(
            Component.text("Dark Red").color(NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD));
        item.setItemMeta(meta);
        inv.setItem(10, item);

        item.setType(Material.RED_TERRACOTTA);
        meta.displayName(Component.text("Red").color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
        item.setItemMeta(meta);
        inv.setItem(11, item);

        item.setType(Material.ORANGE_CONCRETE);
        meta.displayName(Component.text("Gold").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        item.setItemMeta(meta);
        inv.setItem(12, item);

        item.setType(Material.YELLOW_CONCRETE);
        meta.displayName(Component.text("Yellow").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
        item.setItemMeta(meta);
        inv.setItem(13, item);

        item.setType(Material.LIME_CONCRETE);
        meta.displayName(Component.text("Green").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
        item.setItemMeta(meta);
        inv.setItem(14, item);

        item.setType(Material.GREEN_CONCRETE);
        meta.displayName(
            Component.text("Dark Green").color(NamedTextColor.DARK_GREEN).decorate(TextDecoration.BOLD));
        item.setItemMeta(meta);
        inv.setItem(15, item);

        item.setType(Material.LIGHT_BLUE_TERRACOTTA);
        meta.displayName(
            Component.text("Dark Aqua").color(NamedTextColor.DARK_AQUA).decorate(TextDecoration.BOLD));
        item.setItemMeta(meta);
        inv.setItem(16, item);

        item.setType(Material.BLUE_CONCRETE);
        meta.displayName(Component.text("Blue").color(NamedTextColor.BLUE).decorate(TextDecoration.BOLD));
        item.setItemMeta(meta);
        inv.setItem(19, item);

        item.setType(Material.BLUE_TERRACOTTA);
        meta.displayName(
            Component.text("Dark Blue").color(NamedTextColor.DARK_BLUE).decorate(TextDecoration.BOLD));
        item.setItemMeta(meta);
        inv.setItem(20, item);

        item.setType(Material.PURPLE_CONCRETE);
        meta.displayName(
            Component.text("Dark Purple").color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD));
        item.setItemMeta(meta);
        inv.setItem(21, item);

        item.setType(Material.MAGENTA_CONCRETE);
        meta.displayName(
            Component.text("Purple").color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));
        item.setItemMeta(meta);
        inv.setItem(22, item);

        item.setType(Material.GRAY_TERRACOTTA);
        meta.displayName(
            Component.text("Dark Gray").color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.BOLD));
        item.setItemMeta(meta);
        inv.setItem(23, item);

        item.setType(Material.LIGHT_GRAY_CONCRETE);
        meta.displayName(Component.text("Gray").color(NamedTextColor.GRAY).decorate(TextDecoration.BOLD));
        item.setItemMeta(meta);
        inv.setItem(24, item);

        item.setType(Material.WHITE_CONCRETE);
        meta.displayName(Component.text("White").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD));
        item.setItemMeta(meta);
        inv.setItem(25, item);

        item.setType(Material.BARRIER);
        meta.displayName(
            Component.text("Reset to Default").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD));
        item.setItemMeta(meta);
        inv.setItem(31, item);
    }
}
