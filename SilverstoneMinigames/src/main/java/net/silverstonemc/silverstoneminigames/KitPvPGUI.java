package net.silverstonemc.silverstoneminigames;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.stream.IntStream;

public record KitPvPGUI(JavaPlugin plugin) implements CommandExecutor, Listener {
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
            sender.sendMessage(ChatColor.RED + "Sorry, but only players can do that.");
            return true;
        }
        player.openInventory(inv);
        return true;
    }

    // On item click
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inv)) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        if (player.getGameMode() != GameMode.ADVENTURE) {
            player.sendMessage(ChatColor.RED + "You can't do that right now!");
            return;
        }

        switch (event.getRawSlot()) {
            case 10 -> {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
                player.getInventory().clear();
                player.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
                player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
                player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
                player.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
                player.getInventory().setItem(0, new ItemStack(Material.WOODEN_SWORD));
            }
            case 11 -> {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
                player.getInventory().clear();
                player.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
                player.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
                player.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
                player.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
                player.getInventory().setItem(0, new ItemStack(Material.STONE_AXE));
                ItemStack potion = new ItemStack(Material.SPLASH_POTION);
                PotionMeta pm = (PotionMeta) potion.getItemMeta();
                pm.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 600, 0, false, true), true);
                pm.setColor(Color.YELLOW);
                pm.displayName(Component.text("Splash Potion of Swiftness").color(NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false));
                potion.setItemMeta(pm);
                player.getInventory().setItem(1, potion);
            }
            case 12 -> {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
                player.getInventory().clear();
                ItemStack potion = new ItemStack(Material.POTION);
                PotionMeta pm = (PotionMeta) potion.getItemMeta();
                pm.clearCustomEffects();
                pm.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 300, 1, false, true),
                    true);
                pm.setColor(Color.RED);
                pm.displayName(Component.text("Potion of Regeneration").color(NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false));
                potion.setItemMeta(pm);
                player.getInventory().setItem(0, potion);
                potion = new ItemStack(Material.SPLASH_POTION, 2);
                pm = (PotionMeta) potion.getItemMeta();
                pm.clearCustomEffects();
                pm.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 1, 0, false, true), true);
                pm.setColor(Color.BLACK);
                pm.displayName(Component.text("Splash Potion of Harming").color(NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false));
                potion.setItemMeta(pm);
                player.getInventory().setItem(1, potion);
                potion = new ItemStack(Material.SPLASH_POTION, 2);
                pm = (PotionMeta) potion.getItemMeta();
                pm.clearCustomEffects();
                pm.addCustomEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1, false, true), true);
                pm.setColor(Color.PURPLE);
                pm.displayName(Component.text("Splash Potion of Slowness").color(NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false));
                potion.setItemMeta(pm);
                player.getInventory().setItem(2, potion);
                potion = new ItemStack(Material.SPLASH_POTION, 2);
                pm = (PotionMeta) potion.getItemMeta();
                pm.clearCustomEffects();
                pm.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0, false, true), true);
                pm.setColor(Color.GRAY);
                pm.displayName(Component.text("Splash Potion of Blindness").color(NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false));
                potion.setItemMeta(pm);
                player.getInventory().setItem(3, potion);
                potion = new ItemStack(Material.SPLASH_POTION);
                pm = (PotionMeta) potion.getItemMeta();
                pm.clearCustomEffects();
                pm.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 300, 0, false, true), true);
                pm.setColor(Color.RED);
                pm.displayName(Component.text("Splash Potion of Poison").color(NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false));
                potion.setItemMeta(pm);
                player.getInventory().setItem(4, potion);
                player.getInventory().setItem(8, new ItemStack(Material.MILK_BUCKET));
            }
            case 13 -> {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
                player.getInventory().clear();
                player.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
                player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
                player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
                player.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
                ItemStack bow = new ItemStack(Material.BOW);
                bow.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
                player.getInventory().setItem(0, bow);
                player.getInventory().setItem(1, new ItemStack(Material.ARROW, 64));
            }
            case 14 -> {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
                player.getInventory().clear();
                player.getInventory().setItem(0, new ItemStack(Material.TNT));
            }
            case 16 -> closeInv(player);
        }
    }

    // On item click
    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction()
            .equals(Action.RIGHT_CLICK_BLOCK)) if (player.getWorld().getName()
            .equalsIgnoreCase(plugin.getConfig().getString("empty-minigame-world")))
            if (player.getGameMode() == GameMode.ADVENTURE)
                if (player.getInventory().getItemInMainHand().getType() == Material.TNT) {
                    event.setCancelled(true);
                    player.getInventory().clear();
                    player.getWorld().spawn(player.getLocation(), TNTPrimed.class).setFuseTicks(10);
                }
    }

    // Inventory items
    public static void createInv() {
        inv = Bukkit.createInventory(null, 27,
            Component.text(ChatColor.translateAlternateColorCodes('&', "&4&lSelect a Kit")));

        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        // Fill items
        meta.displayName(Component.text(ChatColor.BOLD + ""));
        item.setItemMeta(meta);
        IntStream.rangeClosed(0, 26).boxed().toList().forEach(slot -> inv.setItem(slot, item));

        // Basic
        item.setType(Material.WOODEN_SWORD);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Basic"));
        item.setItemMeta(meta);
        inv.setItem(10, item);

        // Berserker
        item.setType(Material.CHAINMAIL_HELMET);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Berserker"));
        item.setItemMeta(meta);
        inv.setItem(11, item);

        // Potion Master
        item.setType(Material.POTION);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Potion Master"));
        meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        item.setItemMeta(meta);
        inv.setItem(12, item);

        // Archer
        item.setType(Material.BOW);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Archer"));
        item.setItemMeta(meta);
        inv.setItem(13, item);

        // Demolitionist
        item.setType(Material.TNT);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Demolitionist"));
        item.setItemMeta(meta);
        inv.setItem(14, item);

        // Close
        item.setType(Material.BARRIER);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Close"));
        item.setItemMeta(meta);
        inv.setItem(16, item);
    }
}