package net.silverstonemc.silverstoneminigames.minigames;

import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.item.ItemBuilder;
import dev.triumphteam.gui.paper.builder.item.SkullBuilder;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silverstonemc.silverstoneminigames.BossBarManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
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

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length > 1) {
            List<Entity> selector = Bukkit.selectEntities(sender, args[1]);

            switch (args[0].toLowerCase()) {
                case "start" -> {
                    for (Entity entity : selector) {
                        Player player = Bukkit.getPlayer(entity.getName());
                        if (player == null) continue;

                        // Create the boss bar
                        new BossBarManager().createBossBar(
                            player,
                            Component.text("Corruption", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD),
                            0.0f,
                            BossBar.Color.PURPLE,
                            BossBar.Overlay.NOTCHED_10);
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
                    for (Entity entity : selector) {
                        Player player = Bukkit.getPlayer(entity.getName());
                        if (player == null) continue;

                        mainInventory(player).open(player);
                    }
                }

                case "closeinv" -> {
                    for (Entity entity : selector) {
                        Player player = Bukkit.getPlayer(entity.getName());
                        if (player == null) continue;

                        player.addScoreboardTag("Ready");
                        player.addScoreboardTag("CorruptedNinja");
                        player.sendMessage(Component.text(
                            "Time's up! You have the Ninja kit.",
                            NamedTextColor.RED));
                        player.playSound(
                            player.getLocation(),
                            Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO,
                            SoundCategory.UI,
                            1,
                            0.55f);

                        ItemStack item = new ItemStack(Material.IRON_SWORD);
                        player.getInventory().addItem(item);

                        // Close the inventory in the next tick to ensure the tag is added first
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.getOpenInventory().close();
                            }
                        }.runTask(plugin);
                    }
                }

                case "corrupt" -> {
                    for (Entity entity : selector) {
                        Player player = Bukkit.getPlayer(entity.getName());
                        if (player == null) continue;

                        new BossBarManager().setBossBarColor(player, BossBar.Color.RED, NamedTextColor.RED);
                        player.addScoreboardTag("CorruptedHunter");
                    }
                }

                case "uncorrupt" -> {
                    for (Entity entity : selector) {
                        Player player = Bukkit.getPlayer(entity.getName());
                        if (player == null) continue;

                        new BossBarManager().setBossBarColor(
                            player,
                            BossBar.Color.PURPLE,
                            NamedTextColor.LIGHT_PURPLE);
                        player.removeScoreboardTag("CorruptedHunter");
                    }
                }

                case "stop" -> {
                    for (Entity entity : selector) {
                        Player player = Bukkit.getPlayer(entity.getName());
                        if (player == null) continue;

                        // Remove the boss bar
                        new BossBarManager().removeBossBar(player);
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

    private enum KitType {
        TANK("Tank"), NINJA("Ninja"), RANGED("Ranged");

        private final String name;

        KitType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private Gui mainInventory(Player closedPlayer) {
        final int rows = 1;
        boolean[] isClicked = {false}; // Using array to allow modification in lambda - this shouldn't be needed with a future version of the library

        return Gui.of(rows).title(Component.text("Select a Kit", NamedTextColor.BLACK, TextDecoration.BOLD))
            .statelessComponent(container -> {

                container.setItem(
                    2, ItemBuilder.from(Material.IRON_CHESTPLATE).name(getItemName("Tank")).lore(getItemLore(
                            "Weapon: Pickaxe",
                            List.of("20% corruption resistance", "10 hearts"),
                            List.of("Much slower health regeneration"))).flags(ItemFlag.values())
                        .asGuiItem((player, context) -> {
                            playClick(player);
                            isClicked[0] = true;
                            confirmInventory(player, KitType.TANK).open(player);
                        }));

                container.setItem(
                    4, ItemBuilder.from(Material.IRON_SWORD).name(getItemName("Ninja")).lore(getItemLore(
                        "Weapon: Sword",
                        List.of("3 jump boosts per life", "40% speed boost when Hunter"),
                        List.of("7 hearts"))).flags(ItemFlag.values()).asGuiItem((player, context) -> {
                        playClick(player);
                        isClicked[0] = true;
                        confirmInventory(player, KitType.NINJA).open(player);
                    }));

                container.setItem(
                    6, ItemBuilder.from(Material.BOW).name(getItemName("Ranged")).lore(getItemLore(
                            "Weapon: Bow",
                            List.of("Ranged attack"),
                            List.of("No melee attack", "Limited arrows per life", "5 hearts")))
                        .flags(ItemFlag.values()).asGuiItem((player, context) -> {
                            playClick(player);
                            isClicked[0] = true;
                            confirmInventory(player, KitType.RANGED).open(player);
                        }));

            }).onClose(() -> {
                //future: replace with close reason when available
                if (isClicked[0]) return;
                if (!closedPlayer.getScoreboardTags().contains("Ready")) mainInventory(closedPlayer).open(
                    closedPlayer);
            }).build();
    }

    private Gui confirmInventory(Player closedPlayer, KitType kitType) {
        final int rows = 1;
        boolean[] isClicked = {false}; // Using array to allow modification in lambda - this shouldn't be needed with a future version of the library

        return Gui.of(rows).title(Component.text(
            "Confirm " + kitType.getName() + " Kit?",
            NamedTextColor.BLACK,
            TextDecoration.BOLD)).statelessComponent(container -> {

            SkullBuilder confirmItem = ItemBuilder.skull().texture(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDk5ODBjMWQyMTE4MDlhOWI2NTY1MDg4ZjU2YTM4ZjJlZjQ5MTE1YzEwNTRmYTY2MjQ1MTIyZTllZWVkZWNjMiJ9fX0=")
                .name(Component.text("Confirm", NamedTextColor.GREEN, TextDecoration.BOLD)
                    .decoration(TextDecoration.ITALIC, false));

            ItemBuilder cancelItem = ItemBuilder.from(Material.STRUCTURE_VOID).name(Component
                .text("Cancel", NamedTextColor.RED, TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));

            container.setItem(
                3, confirmItem.asGuiItem((player, context) -> {
                    playClick(player);

                    switch (kitType) {
                        case TANK -> {
                            ItemStack item = new ItemStack(Material.IRON_PICKAXE);
                            player.getInventory().addItem(item);
                        }

                        case NINJA -> {
                            ItemStack item = new ItemStack(Material.IRON_SWORD);
                            player.getInventory().addItem(item);
                        }

                        case RANGED -> {
                            ItemStack bow = new ItemStack(Material.BOW);
                            ItemStack crossbow = new ItemStack(Material.CROSSBOW);
                            ItemStack arrow = new ItemStack(Material.TIPPED_ARROW, 64);
                            PotionMeta arrowMeta = (PotionMeta) arrow.getItemMeta();
                            arrowMeta.setBasePotionType(PotionType.HEALING);
                            arrow.setItemMeta(arrowMeta);

                            player.getInventory().setItem(0, bow);
                            for (int slot = 2; slot <= 6; slot++)
                                player.getInventory().setItem(slot, crossbow);
                            player.getInventory().setItem(8, arrow);
                        }
                    }

                    player.addScoreboardTag("Corrupted" + kitType.getName());
                    player.addScoreboardTag("Ready");
                    player.sendMessage(Component.text(
                        "You have selected the " + kitType.getName() + " kit!",
                        NamedTextColor.GREEN));

                    isClicked[0] = true;
                    context.guiView().close();
                }));

            container.setItem(
                5, cancelItem.asGuiItem((player, context) -> {
                    playClick(player);
                    isClicked[0] = true;
                    mainInventory(player).open(player);
                }));

        }).onClose(() -> {
            //future: replace with close reason when available
            if (isClicked[0]) return;
            if (!closedPlayer.getScoreboardTags().contains("Ready")) mainInventory(closedPlayer).open(
                closedPlayer);
        }).build();
    }

    private TextComponent getItemName(String name) {
        Map<TextDecoration, TextDecoration.State> noItalic = Map.of(
            TextDecoration.ITALIC,
            TextDecoration.State.FALSE);

        return Component.text(name, NamedTextColor.GREEN, TextDecoration.BOLD).decorations(noItalic);
    }

    private List<Component> getItemLore(String description, List<String> advantages, List<String> disadvantages) {
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

        return loreList;
    }

    private void playClick(Player player) {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.UI, 1, 1);
    }
}
