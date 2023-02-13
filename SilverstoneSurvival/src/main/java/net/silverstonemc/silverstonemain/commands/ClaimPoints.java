package net.silverstonemc.silverstonemain.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silverstonemc.silverstonemain.SilverstoneMain;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public record ClaimPoints(JavaPlugin plugin) implements CommandExecutor, Listener {

    private static Inventory inv;

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
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Component.text("Sorry, but only players can do that.").color(NamedTextColor.RED));
                return true;
            }

            if (player.getGameMode().equals(GameMode.SURVIVAL) && player.getWorld().getName().startsWith("survival"))
                player.openInventory(inv);
            else player.sendMessage(Component.text("You must be in survival to do that!").color(NamedTextColor.RED));

        } else if (args.length == 1) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Component.text("Sorry, but only players can do that.").color(NamedTextColor.RED));
                return true;
            }

            if (args[0].equalsIgnoreCase("view"))
                player.sendMessage(Component.text("You have " + SilverstoneMain.data.getConfig()
                                .getInt("data." + player.getUniqueId() + ".claim-points", 0) + " Claim Points.")
                        .color(NamedTextColor.DARK_GREEN));
            else return false;

        } else {
            if (!sender.hasPermission("silverstone.moderator")) {
                Bukkit.dispatchCommand(sender, "claimpoints " + args[0]);
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) return false;

            switch (args[0].toLowerCase()) {
                case "give" -> {
                    if (args.length < 3) return false;
                    try {
                        giveClaimPoints(target, Integer.parseInt(args[2]));
                        sender.sendMessage(Component.text("Gave " + Integer.parseInt(args[2]) + " Claim Points to " + target.getName() + "!")
                                .color(NamedTextColor.GREEN));
                    } catch (NumberFormatException ignored) {
                        return false;
                    }
                }

                case "take" -> {
                    if (args.length < 3) return false;
                    try {
                        if (takeClaimPoints(target, Integer.parseInt(args[2])))
                            sender.sendMessage(Component.text("Took " + Integer.parseInt(args[2]) + " Claim Points from " + target.getName() + "!")
                                    .color(NamedTextColor.YELLOW));
                        else
                            sender.sendMessage(Component.text(target.getName() + " doesn't have " + Integer.parseInt(args[2]) + " Claim Points!")
                                    .color(NamedTextColor.RED));
                    } catch (NumberFormatException ignored) {
                        return false;
                    }
                }

                case "view" -> sender.sendMessage(Component.text(target.getName() + " has " + SilverstoneMain.data.getConfig()
                                .getInt("data." + target.getUniqueId() + ".claim-points", 0) + " Claim Points.")
                        .color(NamedTextColor.DARK_GREEN));

                default -> {
                    return false;
                }
            }
        }
        return true;
    }

    public static void giveClaimPoints(Player player, int amount) {
        SilverstoneMain.data.getConfig()
                .set("data." + player.getUniqueId() + ".claim-points", SilverstoneMain.data.getConfig()
                        .getInt("data." + player.getUniqueId() + ".claim-points", 0) + amount);
        SilverstoneMain.data.saveConfig();
    }

    public static boolean takeClaimPoints(Player player, int amount) {
        int currentPoints = SilverstoneMain.data.getConfig()
                .getInt("data." + player.getUniqueId() + ".claim-points", 0);
        int newPoints = currentPoints - amount;
        if (newPoints < 0) return false;

        SilverstoneMain.data.getConfig().set("data." + player.getUniqueId() + ".claim-points", newPoints);
        SilverstoneMain.data.saveConfig();
        return true;
    }

    private void notEnoughPoints(Player player) {
        player.sendMessage(Component.text("You don't have enough Claim Points to purchase that! Type ")
                .color(NamedTextColor.RED)
                .append(Component.text("/claimpoints view")
                        .color(NamedTextColor.GRAY)
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/claimpoints view")))
                .append(Component.text(" to view your points.").color(NamedTextColor.RED)));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, SoundCategory.MASTER, 1, 0.7f);
    }

    // On item click
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inv)) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        switch (event.getRawSlot()) {
            case 11 -> {
                // 1
                if (takeClaimPoints(player, 1)) {
                    closeInv(player);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ps give 4 " + player.getName());
                } else notEnoughPoints(player);
            }

            case 12 -> {
                // 4
                if (takeClaimPoints(player, 4)) {
                    closeInv(player);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ps give 9 " + player.getName());
                } else notEnoughPoints(player);
            }

            case 13 -> {
                // 9
                if (takeClaimPoints(player, 9)) {
                    closeInv(player);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ps give 14 " + player.getName());
                } else notEnoughPoints(player);
            }

            case 14 -> {
                // 25
                if (takeClaimPoints(player, 25)) {
                    closeInv(player);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ps give 24 " + player.getName());
                } else notEnoughPoints(player);
            }

            case 15 -> {
                // 99
                if (takeClaimPoints(player, 99)) {
                    closeInv(player);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ps give 49 " + player.getName());
                } else notEnoughPoints(player);
            }
        }
    }

    // Inventory items
    public static void createInv() {
        inv = Bukkit.createInventory(null, 27, Component.text(ChatColor.translateAlternateColorCodes('&', "&2&lRedeem Claim Points")));

        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = new ArrayList<>();

        // Fill items
        meta.displayName(Component.text(ChatColor.BOLD + ""));
        item.setItemMeta(meta);
        IntStream.rangeClosed(0, 26).boxed().toList().forEach(slot -> inv.setItem(slot, item));

        // 81
        item.setType(Material.IRON_BLOCK);
        meta.displayName(Component.text("9x9")
                .color(NamedTextColor.GREEN)
                .decorate(TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&3&l1 &2Claim Point")));
        meta.lore(lore);
        item.setItemMeta(meta);
        inv.setItem(11, item);

        // 361
        item.setType(Material.GOLD_BLOCK);
        meta.displayName(Component.text("19x19")
                .color(NamedTextColor.GREEN)
                .decorate(TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&3&l4 &2Claim Points")));
        meta.lore(lore);
        item.setItemMeta(meta);
        inv.setItem(12, item);

        // 841
        item.setType(Material.EMERALD_BLOCK);
        meta.displayName(Component.text("29x29")
                .color(NamedTextColor.GREEN)
                .decorate(TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&3&l9 &2Claim Points")));
        meta.lore(lore);
        item.setItemMeta(meta);
        inv.setItem(13, item);

        // 2401
        item.setType(Material.DIAMOND_BLOCK);
        meta.displayName(Component.text("49x49")
                .color(NamedTextColor.GREEN)
                .decorate(TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&3&l25 &2Claim Points")));
        meta.lore(lore);
        item.setItemMeta(meta);
        inv.setItem(14, item);

        // 9801
        item.setType(Material.NETHERITE_BLOCK);
        meta.displayName(Component.text("99x99")
                .color(NamedTextColor.GREEN)
                .decorate(TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&3&l99 &2Claim Points")));
        meta.lore(lore);
        item.setItemMeta(meta);
        inv.setItem(15, item);
    }
}
