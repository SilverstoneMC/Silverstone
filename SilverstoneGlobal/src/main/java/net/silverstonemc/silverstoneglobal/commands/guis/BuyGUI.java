package net.silverstonemc.silverstoneglobal.commands.guis;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class BuyGUI implements CommandExecutor, Listener {
    public static Inventory inv;

    private static SilverstoneGlobal plugin;

    public BuyGUI(SilverstoneGlobal plugin) {
        BuyGUI.plugin = plugin;
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
        Inventory inventory = event.getInventory();

        if (!inventory.equals(inv)) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;

        event.setCancelled(true);

        switch (event.getRawSlot()) {
            case 11 -> {
                // Member
                player.sendMessage(Component.text("\nPurchase the ").color(NamedTextColor.GREEN)
                    .append(Component.text("Member ").color(NamedTextColor.AQUA))
                    .append(Component.text("rank ").color(NamedTextColor.GREEN)).append(
                        Component.text("here").color(NamedTextColor.AQUA).clickEvent(
                            ClickEvent.openUrl("https://silverstone.craftingstore.net/category/261250"))));
                closeInv(player);
            }
            case 12 -> {
                // VIP
                player.sendMessage(Component.text("\nPurchase the ").color(NamedTextColor.GREEN)
                    .append(Component.text("VIP ").color(NamedTextColor.AQUA))
                    .append(Component.text("rank ").color(NamedTextColor.GREEN)).append(
                        Component.text("here").color(NamedTextColor.AQUA).clickEvent(
                            ClickEvent.openUrl("https://silverstone.craftingstore.net/category/261250"))));
                closeInv(player);
            }
            case 13 -> {
                // VIP+
                player.sendMessage(Component.text("\nPurchase the ").color(NamedTextColor.GREEN)
                    .append(Component.text("VIP+ ").color(NamedTextColor.AQUA))
                    .append(Component.text("rank ").color(NamedTextColor.GREEN)).append(
                        Component.text("here").color(NamedTextColor.AQUA).clickEvent(
                            ClickEvent.openUrl("https://silverstone.craftingstore.net/category/261250"))));
                closeInv(player);
            }
            case 14 -> {
                // MVP
                player.sendMessage(Component.text("\nPurchase the ").color(NamedTextColor.GREEN)
                    .append(Component.text("MVP ").color(NamedTextColor.AQUA))
                    .append(Component.text("rank ").color(NamedTextColor.GREEN)).append(
                        Component.text("here").color(NamedTextColor.AQUA).clickEvent(
                            ClickEvent.openUrl("https://silverstone.craftingstore.net/category/261250"))));
                closeInv(player);
            }
            case 15 -> {
                // Donate
                player.sendMessage(Component.text("\nDonate to the server ").color(NamedTextColor.GREEN)
                    .append(Component.text("here").color(NamedTextColor.AQUA).clickEvent(
                        ClickEvent.openUrl("https://silverstone.craftingstore.net/category/261655"))));
                closeInv(player);
            }
        }
    }

    public Inventory createInv() {
        Inventory inventory = Bukkit.createInventory(null, 27,
            Component.text("Available Ranks").color(TextColor.fromHexString("#048a3e"))
                .decorate(TextDecoration.BOLD));


        // Filler
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(""));
        item.setItemMeta(meta);
        IntStream.rangeClosed(0, 26).boxed().toList().forEach(slot -> inventory.setItem(slot, item));

        inventory.setItem(11, getItem(Material.IRON_INGOT, "Member"));
        inventory.setItem(12, getItem(Material.EMERALD, "VIP"));
        inventory.setItem(13, getItem(Material.DIAMOND, "VIP+"));
        inventory.setItem(14, getItem(Material.NETHERITE_INGOT, "MVP"));
        inventory.setItem(15, getItem(Material.WRITABLE_BOOK, "Donate"));

        return inventory;
    }

    private ItemStack getItem(Material item, String rank) {
        ItemStack itemStack = new ItemStack(item);
        ItemMeta meta = itemStack.getItemMeta();
        List<Component> lore = new ArrayList<>();

        meta.displayName(Component.text(rank).color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD)
            .decoration(TextDecoration.ITALIC, false));
        for (String customLore : plugin.getConfig().getStringList("buy-gui." + rank.toLowerCase()))
            lore.add(MiniMessage.miniMessage().deserialize("<!i>" + customLore));
        meta.lore(lore);
        itemStack.setItemMeta(meta);

        return itemStack;
    }
}