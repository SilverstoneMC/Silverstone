package net.silverstonemc.silverstoneglobal.commands.guis;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.stream.IntStream;

public class ChatColorGUI implements CommandExecutor, Listener {
    public ChatColorGUI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;
    private static Inventory inv;

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
            sender.sendMessage(Component.text("Sorry, but only players can do that.", NamedTextColor.RED));
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
                setChatColor(player, '4');
                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.DARK_RED));
            }

            case 11 -> {
                setChatColor(player, 'c');
                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.RED));
            }

            case 12 -> {
                setChatColor(player, '6');
                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.GOLD));
            }

            case 13 -> {
                setChatColor(player, 'e');
                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.YELLOW));
            }

            case 14 -> {
                setChatColor(player, 'a');
                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.GREEN));
            }

            case 15 -> {
                setChatColor(player, '2');
                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.DARK_GREEN));
            }

            case 16 -> {
                setChatColor(player, '3');
                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.DARK_AQUA));
            }

            case 19 -> {
                setChatColor(player, '9');
                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.BLUE));
            }

            case 20 -> {
                setChatColor(player, '1');
                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.DARK_BLUE));
            }

            case 21 -> {
                setChatColor(player, '5');
                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.DARK_PURPLE));
            }

            case 22 -> {
                setChatColor(player, 'd');
                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.LIGHT_PURPLE));
            }

            case 23 -> {
                setChatColor(player, '8');
                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.DARK_GRAY));
            }

            case 24 -> {
                setChatColor(player, '7');
                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.GRAY));
            }

            case 25 -> {
                setChatColor(player, 'f');
                player.sendMessage(Component.text("Chat color changed."));
            }

            case 31 -> {
                setChatColor(player, 'r');
                player.sendMessage(Component.text("Chat color reset to default."));
            }

            default -> {
                return;
            }
        }

        closeInv(player);
    }

    private void setChatColor(Player player, char value) {
        // Tried using the API but this just works much better lol
        if (value == 'r') Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(),
            "lp user " + player.getName() + " meta removesuffix 500");

        else Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(),
            "lp user " + player.getName() + " meta setsuffix 500 &" + value);
    }

    public void createInv() {
        Inventory inventory = Bukkit.createInventory(
            null,
            45,
            Component.text("Chat Colors", TextColor.fromHexString("#a62828"), TextDecoration.BOLD));

        // Filler
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.empty());
        item.setItemMeta(meta);
        IntStream.rangeClosed(0, 44).boxed().toList().forEach(slot -> inventory.setItem(slot, item));

        item.setType(Material.RED_CONCRETE);
        meta.displayName(Component.text("Dark Red", NamedTextColor.DARK_RED, TextDecoration.BOLD)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        item.setItemMeta(meta);
        inventory.setItem(10, item);

        item.setType(Material.RED_TERRACOTTA);
        meta.displayName(Component.text("Red", NamedTextColor.RED, TextDecoration.BOLD)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        item.setItemMeta(meta);
        inventory.setItem(11, item);

        item.setType(Material.ORANGE_CONCRETE);
        meta.displayName(Component.text("Gold", NamedTextColor.GOLD, TextDecoration.BOLD)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        item.setItemMeta(meta);
        inventory.setItem(12, item);

        item.setType(Material.YELLOW_CONCRETE);
        meta.displayName(Component.text("Yellow", NamedTextColor.YELLOW, TextDecoration.BOLD)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        item.setItemMeta(meta);
        inventory.setItem(13, item);

        item.setType(Material.LIME_CONCRETE);
        meta.displayName(Component.text("Green", NamedTextColor.GREEN, TextDecoration.BOLD)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        item.setItemMeta(meta);
        inventory.setItem(14, item);

        item.setType(Material.GREEN_CONCRETE);
        meta.displayName(Component.text("Dark Green", NamedTextColor.DARK_GREEN, TextDecoration.BOLD)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        item.setItemMeta(meta);
        inventory.setItem(15, item);

        item.setType(Material.LIGHT_BLUE_TERRACOTTA);
        meta.displayName(Component.text("Dark Aqua", NamedTextColor.DARK_AQUA, TextDecoration.BOLD)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        item.setItemMeta(meta);
        inventory.setItem(16, item);

        item.setType(Material.BLUE_CONCRETE);
        meta.displayName(Component.text("Blue", NamedTextColor.BLUE, TextDecoration.BOLD)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        item.setItemMeta(meta);
        inventory.setItem(19, item);

        item.setType(Material.BLUE_TERRACOTTA);
        meta.displayName(Component.text("Dark Blue", NamedTextColor.DARK_BLUE, TextDecoration.BOLD)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        item.setItemMeta(meta);
        inventory.setItem(20, item);

        item.setType(Material.PURPLE_CONCRETE);
        meta.displayName(Component.text("Dark Purple", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        item.setItemMeta(meta);
        inventory.setItem(21, item);

        item.setType(Material.MAGENTA_CONCRETE);
        meta.displayName(Component.text("Purple", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        item.setItemMeta(meta);
        inventory.setItem(22, item);

        item.setType(Material.GRAY_TERRACOTTA);
        meta.displayName(Component.text("Dark Gray", NamedTextColor.DARK_GRAY, TextDecoration.BOLD)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        item.setItemMeta(meta);
        inventory.setItem(23, item);

        item.setType(Material.LIGHT_GRAY_CONCRETE);
        meta.displayName(Component.text("Gray", NamedTextColor.GRAY, TextDecoration.BOLD)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        item.setItemMeta(meta);
        inventory.setItem(24, item);

        item.setType(Material.WHITE_CONCRETE);
        meta.displayName(Component.text("White", NamedTextColor.WHITE, TextDecoration.BOLD)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        item.setItemMeta(meta);
        inventory.setItem(25, item);

        item.setType(Material.BARRIER);
        meta.displayName(Component.text("Reset to Default", NamedTextColor.WHITE, TextDecoration.BOLD)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        item.setItemMeta(meta);
        inventory.setItem(31, item);

        inv = inventory;
    }
}
