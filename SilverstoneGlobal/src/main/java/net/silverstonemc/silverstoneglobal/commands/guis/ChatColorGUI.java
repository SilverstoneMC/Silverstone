package net.silverstonemc.silverstoneglobal.commands.guis;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
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
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("chatcolor");
                out.writeChar('4');
                player.sendPluginMessage(plugin, "silverstone:pluginmsg", out.toByteArray());

                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.DARK_RED));
            }

            case 11 -> {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("chatcolor");
                out.writeChar('c');
                player.sendPluginMessage(plugin, "silverstone:pluginmsg", out.toByteArray());

                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.RED));
            }

            case 12 -> {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("chatcolor");
                out.writeChar('6');
                player.sendPluginMessage(plugin, "silverstone:pluginmsg", out.toByteArray());

                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.GOLD));
            }

            case 13 -> {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("chatcolor");
                out.writeChar('e');
                player.sendPluginMessage(plugin, "silverstone:pluginmsg", out.toByteArray());

                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.YELLOW));
            }

            case 14 -> {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("chatcolor");
                out.writeChar('a');
                player.sendPluginMessage(plugin, "silverstone:pluginmsg", out.toByteArray());

                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.GREEN));
            }

            case 15 -> {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("chatcolor");
                out.writeChar('2');
                player.sendPluginMessage(plugin, "silverstone:pluginmsg", out.toByteArray());

                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.DARK_GREEN));
            }

            case 16 -> {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("chatcolor");
                out.writeChar('3');
                player.sendPluginMessage(plugin, "silverstone:pluginmsg", out.toByteArray());

                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.DARK_AQUA));
            }

            case 19 -> {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("chatcolor");
                out.writeChar('9');
                player.sendPluginMessage(plugin, "silverstone:pluginmsg", out.toByteArray());

                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.BLUE));
            }

            case 20 -> {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("chatcolor");
                out.writeChar('1');
                player.sendPluginMessage(plugin, "silverstone:pluginmsg", out.toByteArray());

                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.DARK_BLUE));
            }

            case 21 -> {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("chatcolor");
                out.writeChar('5');
                player.sendPluginMessage(plugin, "silverstone:pluginmsg", out.toByteArray());

                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.DARK_PURPLE));
            }

            case 22 -> {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("chatcolor");
                out.writeChar('d');
                player.sendPluginMessage(plugin, "silverstone:pluginmsg", out.toByteArray());

                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.LIGHT_PURPLE));
            }

            case 23 -> {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("chatcolor");
                out.writeChar('8');
                player.sendPluginMessage(plugin, "silverstone:pluginmsg", out.toByteArray());

                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.DARK_GRAY));
            }

            case 24 -> {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("chatcolor");
                out.writeChar('7');
                player.sendPluginMessage(plugin, "silverstone:pluginmsg", out.toByteArray());

                player.sendMessage(Component.text("Chat color changed.", NamedTextColor.GRAY));
            }

            case 25 -> {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("chatcolor");
                out.writeChar('f');
                player.sendPluginMessage(plugin, "silverstone:pluginmsg", out.toByteArray());

                player.sendMessage(Component.text("Chat color changed."));
            }

            case 31 -> {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("chatcolor");
                out.writeChar('r');
                player.sendPluginMessage(plugin, "silverstone:pluginmsg", out.toByteArray());

                player.sendMessage(Component.text("Chat color reset to default."));
            }

            default -> {
                return;
            }
        }

        closeInv(player);
    }

    public void createInv() {
        Inventory inventory = Bukkit.createInventory(null, 45,
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
