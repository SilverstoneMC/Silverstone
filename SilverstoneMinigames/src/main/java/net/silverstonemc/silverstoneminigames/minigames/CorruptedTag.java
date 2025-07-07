package net.silverstonemc.silverstoneminigames.minigames;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silverstonemc.silverstoneminigames.BossBarManager;
import net.silverstonemc.silverstoneminigames.CustomSkull;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CorruptedTag implements CommandExecutor, Listener {
    public CorruptedTag(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;
    private static BukkitRunnable bossBarUpdater;
    private static Inventory confirmTankInv;
    private static Inventory confirmNinjaInv;
    private static Inventory confirmRangedInv;
    private static Inventory kitInv;

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length > 1) {
            List<Entity> selector = Bukkit.selectEntities(sender, args[1]);

            switch (args[0].toLowerCase()) {
                case "start" -> {
                    try {
                        for (Entity entity : selector) {
                            Player player = Bukkit.getPlayer(entity.getName());
                            if (player == null) continue;

                            // Create the boss bar
                            new BossBarManager().createBossBar(
                                player,
                                Component.text(
                                    "Corruption",
                                    NamedTextColor.LIGHT_PURPLE,
                                    TextDecoration.BOLD),
                                0.0f,
                                BossBar.Color.PURPLE,
                                BossBar.Overlay.NOTCHED_10);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        sender.sendMessage(Component.text(
                            "Please provide a valid selector!",
                            NamedTextColor.RED));
                    }

                    if (bossBarUpdater != null) bossBarUpdater.cancel();

                    // Start the boss bar updater
                    bossBarUpdater = new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (Map.Entry<Player, BossBar> entry : BossBarManager.bossBars.entrySet()) {
                                Player players = entry.getKey();
                                if (!entry.getValue().name().toString().contains("Corruption")) continue;

                                //noinspection DataFlowIssue
                                float value = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(
                                    "mctagcorruption").getScore(players).getScore() / 10000.0f;
                                new BossBarManager().setBossBarProgress(players, value);
                            }
                        }
                    };
                    bossBarUpdater.runTaskTimer(plugin, 0, 10);
                }

                case "kitselect" -> {
                    try {
                        for (Entity entity : selector) {
                            Player player = Bukkit.getPlayer(entity.getName());
                            if (player == null) continue;

                            // Open the kit inventory
                            player.openInventory(kitInv);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        sender.sendMessage(Component.text(
                            "Please provide a valid selector!",
                            NamedTextColor.RED));
                    }
                }

                case "closeinv" -> {
                    try {
                        for (Entity entity : selector) {
                            Player player = Bukkit.getPlayer(entity.getName());
                            if (player == null) continue;

                            closeInv(player);
                            player.addScoreboardTag("CorruptedNinja");
                            player.sendMessage(Component.text(
                                "Time's up! You have the Ninja kit.",
                                NamedTextColor.RED));
                        }
                    } catch (IndexOutOfBoundsException e) {
                        sender.sendMessage(Component.text(
                            "Please provide a valid selector!",
                            NamedTextColor.RED));
                    }
                }

                case "corrupt" -> {
                    try {
                        for (Entity entity : selector) {
                            Player player = Bukkit.getPlayer(entity.getName());
                            if (player == null) continue;

                            new BossBarManager().setBossBarColor(
                                player,
                                BossBar.Color.RED,
                                NamedTextColor.RED);
                            player.addScoreboardTag("CorruptedHunter");
                        }
                    } catch (IndexOutOfBoundsException e) {
                        sender.sendMessage(Component.text(
                            "Please provide a valid selector!",
                            NamedTextColor.RED));
                    }
                }

                case "uncorrupt" -> {
                    try {
                        for (Entity entity : selector) {
                            Player player = Bukkit.getPlayer(entity.getName());
                            if (player == null) continue;

                            new BossBarManager().setBossBarColor(
                                player,
                                BossBar.Color.PURPLE,
                                NamedTextColor.LIGHT_PURPLE);
                            player.removeScoreboardTag("CorruptedHunter");
                        }
                    } catch (IndexOutOfBoundsException e) {
                        sender.sendMessage(Component.text(
                            "Please provide a valid selector!",
                            NamedTextColor.RED));
                    }
                }

                case "stop" -> {
                    try {
                        for (Entity entity : selector) {
                            Player player = Bukkit.getPlayer(entity.getName());
                            if (player == null) continue;

                            // Remove the boss bar
                            new BossBarManager().removeBossBar(player);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        sender.sendMessage(Component.text(
                            "Please provide a valid selector!",
                            NamedTextColor.RED));
                    }

                    // Stop the boss bar updater
                    if (bossBarUpdater != null) {
                        bossBarUpdater.cancel();
                        bossBarUpdater = null;
                    }
                }
            }
            return true;
        }
        return false;
    }

    // Kit inventory stuff

    private void closeInv(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
            }
        }.runTask(plugin);
    }

    private enum KitType {
        TANK, NINJA, RANGED
    }

    private void confirmInv(Player player, KitType kitType) {
        new BukkitRunnable() {
            @Override
            public void run() {
                switch (kitType) {
                    case TANK -> player.openInventory(confirmTankInv);
                    case NINJA -> player.openInventory(confirmNinjaInv);
                    case RANGED -> player.openInventory(confirmRangedInv);
                }
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
            }
        }.runTask(plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();

        if (!inventory.equals(kitInv) && !inventory.equals(confirmTankInv) && !inventory.equals(
            confirmNinjaInv) && !inventory.equals(confirmRangedInv)) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;

        event.setCancelled(true);

        if (inventory.equals(kitInv)) switch (event.getRawSlot()) {
            case 2 -> confirmInv(player, KitType.TANK);
            case 4 -> confirmInv(player, KitType.NINJA);
            case 6 -> confirmInv(player, KitType.RANGED);
        }
        else switch (event.getRawSlot()) {
            case 3 -> {
                String type = null;

                if (inventory.equals(confirmTankInv)) {
                    type = "Tank";

                    ItemStack item = new ItemStack(Material.IRON_PICKAXE);
                    player.getInventory().addItem(item);

                } else if (inventory.equals(confirmNinjaInv)) {
                    type = "Ninja";

                    ItemStack item = new ItemStack(Material.IRON_SWORD);
                    player.getInventory().addItem(item);

                } else if (inventory.equals(confirmRangedInv)) {
                    type = "Ranged";

                    ItemStack bow = new ItemStack(Material.BOW);
                    ItemStack crossbow = new ItemStack(Material.CROSSBOW);
                    ItemStack arrow = new ItemStack(Material.TIPPED_ARROW, 64);
                    PotionMeta arrowMeta = (PotionMeta) arrow.getItemMeta();
                    arrowMeta.setBasePotionType(PotionType.HEALING);
                    arrow.setItemMeta(arrowMeta);

                    player.getInventory().setItem(0, bow);
                    player.getInventory().setItem(2, crossbow);
                    player.getInventory().setItem(3, crossbow);
                    player.getInventory().setItem(4, crossbow);
                    player.getInventory().setItem(5, crossbow);
                    player.getInventory().setItem(6, crossbow);
                    player.getInventory().setItem(8, arrow);
                }

                player.addScoreboardTag("Corrupted" + type);
                player.addScoreboardTag("Ready");
                player.sendMessage(Component.text(
                    "You have selected the " + type + " kit!",
                    NamedTextColor.GREEN));
                closeInv(player);
            }

            case 5 -> {
                player.openInventory(kitInv);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();

        if (event.getReason() != InventoryCloseEvent.Reason.PLAYER) return;
        if (!inventory.equals(kitInv) && !inventory.equals(confirmTankInv) && !inventory.equals(
            confirmNinjaInv) && !inventory.equals(confirmRangedInv)) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                event.getPlayer().openInventory(kitInv);
            }
        }.runTask(plugin);
    }

    public void createInventories() {
        // Kits
        kitInv = Bukkit.createInventory(
            null,
            9,
            Component.text("Select a Kit", NamedTextColor.BLACK, TextDecoration.BOLD));

        kitInv.setItem(
            2, createKitItem(
                Material.IRON_CHESTPLATE,
                "Tank",
                "Pickaxe",
                List.of("20% corruption resistance", "10 hearts"),
                List.of("Much slower health regeneration")));

        kitInv.setItem(
            4, createKitItem(
                Material.IRON_SWORD,
                "Ninja",
                "Sword",
                List.of("3 jump boosts per life", "40% speed boost when Hunter"),
                List.of("7 hearts")));

        kitInv.setItem(
            6, createKitItem(
                Material.BOW,
                "Ranged",
                "Bow",
                List.of("Ranged attack"),
                List.of("No melee attack", "Limited arrows per life", "5 hearts")));

        // Confirm button
        ItemStack confirm = new CustomSkull().createCustomSkull(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDk5ODBjMWQyMTE4MDlhOWI2NTY1MDg4ZjU2YTM4ZjJlZjQ5MTE1YzEwNTRmYTY2MjQ1MTIyZTllZWVkZWNjMiJ9fX0=",
            "Confirm");
        SkullMeta confirmMeta = (SkullMeta) confirm.getItemMeta();
        confirmMeta.displayName(Component.text("Confirm", NamedTextColor.GREEN, TextDecoration.BOLD)
            .decoration(TextDecoration.ITALIC, false));
        confirm.setItemMeta(confirmMeta);

        // Cancel button
        ItemStack cancel = new ItemStack(Material.STRUCTURE_VOID);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.displayName(Component.text("Cancel", NamedTextColor.RED, TextDecoration.BOLD)
            .decoration(TextDecoration.ITALIC, false));
        cancel.setItemMeta(cancelMeta);

        // Tank
        confirmTankInv = Bukkit.createInventory(
            null,
            9,
            Component.text("Confirm Tank Kit?", NamedTextColor.BLACK, TextDecoration.BOLD));
        confirmTankInv.setItem(3, confirm);
        confirmTankInv.setItem(5, cancel);

        // Ninja
        confirmNinjaInv = Bukkit.createInventory(
            null,
            9,
            Component.text("Confirm Ninja Kit?", NamedTextColor.BLACK, TextDecoration.BOLD));
        confirmNinjaInv.setItem(3, confirm);
        confirmNinjaInv.setItem(5, cancel);

        // Ranged
        confirmRangedInv = Bukkit.createInventory(
            null,
            9,
            Component.text("Confirm Ranged Kit?", NamedTextColor.BLACK, TextDecoration.BOLD));
        confirmRangedInv.setItem(3, confirm);
        confirmRangedInv.setItem(5, cancel);
    }

    private ItemStack createKitItem(Material item, String title, String description, List<String> advantages, List<String> disadvantages) {
        ItemStack itemStack = new ItemStack(item);
        ItemMeta meta = itemStack.getItemMeta();

        // Custom potion data
        if (item == Material.POTION) {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.setColor(Color.RED);
            potionMeta.addItemFlags(ItemFlag.values());
        }

        // Title
        meta.displayName(Component.text(title, NamedTextColor.GREEN, TextDecoration.BOLD)
            .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));

        List<Component> loreList = new ArrayList<>();

        // Description
        loreList.add(Component.text(description, NamedTextColor.AQUA)
            .decoration(TextDecoration.ITALIC, false));
        if (!advantages.isEmpty() || !disadvantages.isEmpty()) loreList.add(Component.empty());

        // Advantages
        for (String advantage : advantages)
            loreList.add(Component.text("+ ", NamedTextColor.DARK_GREEN)
                .decoration(TextDecoration.ITALIC, false)
                .append(Component.text(advantage, NamedTextColor.DARK_AQUA)));

        // Disadvantages
        for (String disadvantage : disadvantages)
            loreList.add(Component.text("- ", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
                .append(Component.text(disadvantage, NamedTextColor.DARK_AQUA)));

        meta.lore(loreList);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
