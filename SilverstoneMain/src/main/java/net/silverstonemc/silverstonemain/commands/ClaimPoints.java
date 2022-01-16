package net.silverstonemc.silverstonemain.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silverstonemc.silverstonemain.SilverstoneMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
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
//            player.openInventory(inv);
            player.sendMessage("Coming soon!");

        } else if (args.length == 1) return false;
        else {
            if (!sender.hasPermission("silverstone.moderator")) {
                Bukkit.dispatchCommand(sender, "claimpoints");
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

    /*// On item click
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inv)) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        switch (event.getRawSlot()) {
            case 10 -> {
                // Bark / Bone
                bark(player);
                closeInv(player);
                if (player.getGameMode().equals(GameMode.ADVENTURE) && player.getWorld()
                        .getName()
                        .equalsIgnoreCase(plugin.getConfig().getString("empty-minigame-world")))
                    points.put(player, currentPoints + 1);
                tauntTimer(player);
            }

            case 11 -> {
                // Ding / Bell
                ding(player);
                closeInv(player);
                if (player.getGameMode().equals(GameMode.ADVENTURE) && player.getWorld()
                        .getName()
                        .equalsIgnoreCase(plugin.getConfig().getString("empty-minigame-world")))
                    points.put(player, currentPoints + 2);
                tauntTimer(player);
            }

            case 12 -> {
                // Scream / Tear
                scream(player);
                closeInv(player);
                if (player.getGameMode().equals(GameMode.ADVENTURE) && player.getWorld()
                        .getName()
                        .equalsIgnoreCase(plugin.getConfig().getString("empty-minigame-world")))
                    points.put(player, currentPoints + 3);
                tauntTimer(player);
            }

            case 13 -> {
                // Roar / Dragon Head
                roar(player);
                closeInv(player);
                if (player.getGameMode().equals(GameMode.ADVENTURE) && player.getWorld()
                        .getName()
                        .equalsIgnoreCase(plugin.getConfig().getString("empty-minigame-world")))
                    points.put(player, currentPoints + 5);
                tauntTimer(player);
            }

            case 14 -> {
                // Explosion / TNT
                explosion(player);
                closeInv(player);
                if (player.getGameMode().equals(GameMode.ADVENTURE) && player.getWorld()
                        .getName()
                        .equalsIgnoreCase(plugin.getConfig().getString("empty-minigame-world")))
                    points.put(player, currentPoints + 8);
                tauntTimer(player);
            }
        }
    }*/

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

        // 5
        item.setType(Material.SEA_LANTERN);
        meta.displayName(Component.text("10x10").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "\n&3&l5 &2Claim Points")));
        meta.lore(lore);
        item.setItemMeta(meta);
        inv.setItem(10, item);

        // 10
        meta.displayName(Component.text("400x400").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "\n&3&l10 &2Claim Points")));
        meta.lore(lore);
        item.setItemMeta(meta);
        inv.setItem(11, item);

        // 15
        meta.displayName(Component.text("900x900").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "\n&3&l5 &2Claim Points")));
        meta.lore(lore);
        item.setItemMeta(meta);
        inv.setItem(12, item);

        // 25
        meta.displayName(Component.text("2500x2500").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "\n&3&l5 &2Claim Points")));
        meta.lore(lore);
        item.setItemMeta(meta);
        inv.setItem(13, item);

        // 50
        meta.displayName(Component.text("1000x1000").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "\n&3&l5 &2Claim Points")));
        meta.lore(lore);
        item.setItemMeta(meta);
        inv.setItem(14, item);
    }
}
