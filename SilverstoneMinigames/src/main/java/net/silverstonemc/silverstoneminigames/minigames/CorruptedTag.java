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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CorruptedTag implements CommandExecutor, Listener {
    public CorruptedTag(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;
    private static BukkitRunnable bossBarUpdater = null;
    private static Inventory confirmTankInv;
    private static Inventory confirmNinjaInv;
    private static Inventory confirmHealerInv;
    private static Inventory confirmKBInv;
    private static Inventory confirmNothingInv;
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
                            new BossBarManager().createBossBar(player,
                                Component.text("Corruption",
                                    NamedTextColor.LIGHT_PURPLE,
                                    TextDecoration.BOLD),
                                0f,
                                BossBar.Color.PURPLE,
                                BossBar.Overlay.NOTCHED_10);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        sender.sendMessage(Component.text("Please provide a valid selector!",
                            NamedTextColor.RED));
                    }

                    if (bossBarUpdater != null) bossBarUpdater.cancel();

                    // Start the boss bar updater
                    bossBarUpdater = new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (Player players : BossBarManager.bossBars.keySet()) {
                                if (!BossBarManager.bossBars.get(players).name().toString().contains(
                                    "Corruption")) continue;

                                //noinspection DataFlowIssue
                                float value = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(
                                    "mctagcorruption").getScore(players).getScore() / 10000f;
                                new BossBarManager().setBossBarProgress(players, value);
                            }
                        }
                    };
                    //todo remove arg value after development
                    bossBarUpdater.runTaskTimer(plugin, 0, Long.parseLong(args[2]));
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
                        sender.sendMessage(Component.text("Please provide a valid selector!",
                            NamedTextColor.RED));
                    }
                }

                case "corrupt" -> {
                    try {
                        for (Entity entity : selector) {
                            Player player = Bukkit.getPlayer(entity.getName());
                            if (player == null) continue;

                            new BossBarManager().setBossBarColor(player,
                                BossBar.Color.RED,
                                NamedTextColor.RED);
                            player.addScoreboardTag("Corrupted");
                        }
                    } catch (IndexOutOfBoundsException e) {
                        sender.sendMessage(Component.text("Please provide a valid selector!",
                            NamedTextColor.RED));
                    }
                }

                case "uncorrupt" -> {
                    try {
                        for (Entity entity : selector) {
                            Player player = Bukkit.getPlayer(entity.getName());
                            if (player == null) continue;

                            new BossBarManager().setBossBarColor(player,
                                BossBar.Color.PURPLE,
                                NamedTextColor.LIGHT_PURPLE);
                            player.removeScoreboardTag("Corrupted");
                            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(
                                potionEffect.getType()));
                        }
                    } catch (IndexOutOfBoundsException e) {
                        sender.sendMessage(Component.text("Please provide a valid selector!",
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
                        sender.sendMessage(Component.text("Please provide a valid selector!",
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
        TANK, NINJA, HEALER, KNOCKBACK, NOTHING
    }

    private void confirmInv(Player player, KitType kitType) {
        new BukkitRunnable() {
            @Override
            public void run() {
                switch (kitType) {
                    case TANK -> player.openInventory(confirmTankInv);
                    case NINJA -> player.openInventory(confirmNinjaInv);
                    case HEALER -> player.openInventory(confirmHealerInv);
                    case KNOCKBACK -> player.openInventory(confirmKBInv);
                    case NOTHING -> player.openInventory(confirmNothingInv);
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
            confirmNinjaInv) && !inventory.equals(confirmHealerInv) && !inventory.equals(confirmKBInv) && !inventory.equals(
            confirmNothingInv)) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;

        event.setCancelled(true);

        if (inventory.equals(kitInv)) switch (event.getRawSlot()) {
            case 0 -> confirmInv(player, KitType.TANK);
            case 2 -> confirmInv(player, KitType.NINJA);
            case 4 -> confirmInv(player, KitType.HEALER);
            case 6 -> confirmInv(player, KitType.KNOCKBACK);
            case 8 -> confirmInv(player, KitType.NOTHING);
        }
        else switch (event.getRawSlot()) {
            case 3 -> {
                String type;

                player.getInventory().clear();

                if (inventory.equals(confirmTankInv)) {
                    type = "Tank";

                    ItemStack shield = new ItemStack(Material.SHIELD);
                    player.getInventory().setItem(EquipmentSlot.OFF_HAND, shield);

                    ItemStack axe = new ItemStack(Material.IRON_AXE);
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), axe);

                } else if (inventory.equals(confirmNinjaInv)) {
                    type = "Ninja";

                    ItemStack sword = new ItemStack(Material.IRON_SWORD);
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), sword);

                } else if (inventory.equals(confirmHealerInv)) {
                    type = "Healer";

                    ItemStack hoe = new ItemStack(Material.IRON_HOE);
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), hoe);

                } else if (inventory.equals(confirmKBInv)) {
                    type = "Knockbacker9000";

                    ItemStack trident = new ItemStack(Material.TRIDENT);
                    ItemMeta tridentMeta = trident.getItemMeta();
                    tridentMeta.addEnchant(Enchantment.LOYALTY, 1, false);
                    trident.setItemMeta(tridentMeta);
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), trident);

                } else type = "Nothing";

                player.addScoreboardTag("Corrupted" + type);
                player.addScoreboardTag("Ready");
                player.sendMessage(Component.text("You have selected the " + type + " kit!",
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
            confirmNinjaInv) && !inventory.equals(confirmHealerInv) && !inventory.equals(confirmKBInv) && !inventory.equals(
            confirmNothingInv)) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                event.getPlayer().openInventory(kitInv);
            }
        }.runTask(plugin);
    }

    public void createInventories() {
        // Kits
        kitInv = Bukkit.createInventory(null,
            9,
            Component.text("Select a Kit", NamedTextColor.BLACK, TextDecoration.BOLD));

        kitInv.setItem(0, createKitItem(Material.SHIELD,
            "Tank",
            "Axe & Shield",
            List.of("20% corruption resistance"),
            List.of("Much slower health regeneration")));

        kitInv.setItem(2, createKitItem(Material.IRON_SWORD,
            "Ninja",
            "Sword & 3 Double Jumps",
            List.of("10% speed boost when Hunter"),
            List.of("Slower health regeneration")));

        kitInv.setItem(4, createKitItem(Material.POTION,
            "Healer",
            "Hoe",
            List.of("Faster health regeneration"),
            List.of("10% faster corruption")));

        kitInv.setItem(6, createKitItem(Material.TRIDENT,
            "Knockbacker9000",
            "Trident",
            List.of("Ranged attack"),
            List.of("Glowing at all times")));

        kitInv.setItem(8,
            createKitItem(Material.BARRIER,
                "Nothing",
                "No advantages or disadvantages",
                List.of(),
                List.of()));

        // Confirm button
        final ItemStack confirm = new CustomSkull().createCustomSkull(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDk5ODBjMWQyMTE4MDlhOWI2NTY1MDg4ZjU2YTM4ZjJlZjQ5MTE1YzEwNTRmYTY2MjQ1MTIyZTllZWVkZWNjMiJ9fX0=",
            "Confirm");
        SkullMeta confirmMeta = (SkullMeta) confirm.getItemMeta();
        confirmMeta.displayName(Component.text("Confirm", NamedTextColor.GREEN, TextDecoration.BOLD)
            .decoration(TextDecoration.ITALIC, false));
        confirm.setItemMeta(confirmMeta);

        // Cancel button
        final ItemStack cancel = new ItemStack(Material.STRUCTURE_VOID);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.displayName(Component.text("Cancel", NamedTextColor.RED, TextDecoration.BOLD)
            .decoration(TextDecoration.ITALIC, false));
        cancel.setItemMeta(cancelMeta);

        // Tank
        confirmTankInv = Bukkit.createInventory(null,
            9,
            Component.text("Confirm Tank Kit?", NamedTextColor.BLACK, TextDecoration.BOLD));
        confirmTankInv.setItem(3, confirm);
        confirmTankInv.setItem(5, cancel);

        // Ninja
        confirmNinjaInv = Bukkit.createInventory(null,
            9,
            Component.text("Confirm Ninja Kit?", NamedTextColor.BLACK, TextDecoration.BOLD));
        confirmNinjaInv.setItem(3, confirm);
        confirmNinjaInv.setItem(5, cancel);

        // Healer
        confirmHealerInv = Bukkit.createInventory(null,
            9,
            Component.text("Confirm Healer Kit?", NamedTextColor.BLACK, TextDecoration.BOLD));
        confirmHealerInv.setItem(3, confirm);
        confirmHealerInv.setItem(5, cancel);

        // Knockback
        confirmKBInv = Bukkit.createInventory(null,
            9,
            Component.text("Confirm Knockbacker Kit?", NamedTextColor.BLACK, TextDecoration.BOLD));
        confirmKBInv.setItem(3, confirm);
        confirmKBInv.setItem(5, cancel);

        // Nothing
        confirmNothingInv = Bukkit.createInventory(null,
            9,
            Component.text("Confirm Nothing Kit?", NamedTextColor.BLACK, TextDecoration.BOLD));
        confirmNothingInv.setItem(3, confirm);
        confirmNothingInv.setItem(5, cancel);
    }

    private ItemStack createKitItem(Material item, String title, String description, List<String> advantages, List<String> disadvantages) {
        ItemStack itemStack = new ItemStack(item);
        ItemMeta meta = itemStack.getItemMeta();

        // Custom potion data
        if (item == Material.POTION) {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.setColor(Color.RED);
            potionMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        }

        // Title
        meta.displayName(Component.text(title, NamedTextColor.GREEN, TextDecoration.BOLD)
            .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));

        ArrayList<Component> loreList = new ArrayList<>();

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
