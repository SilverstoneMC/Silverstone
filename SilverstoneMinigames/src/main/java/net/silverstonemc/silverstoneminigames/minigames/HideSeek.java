package net.silverstonemc.silverstoneminigames.minigames;

import dev.triumphteam.gui.click.ClickContext;
import dev.triumphteam.gui.layout.GuiLayout;
import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.item.ItemBuilder;
import dev.triumphteam.gui.slot.Slot;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.silverstonemc.silverstoneminigames.SilverstoneMinigames;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HideSeek implements Listener {
    public HideSeek(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static final Map<Player, Integer> points = new HashMap<>();

    private final JavaPlugin plugin;
    private static final Map<Player, Long> cooldowns = new HashMap<>();
    private static final Map<Player, Long> spamCooldown = new HashMap<>();

    @EventHandler
    public void onBellClick(PlayerInteractEvent event) {
        System.out.println("EVENT FIRED");
        Player player = event.getPlayer();

        System.out.println(1);
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        System.out.println(2);
        if (player.getInventory().getItemInMainHand().getType() != Material.BELL) return;
        System.out.println(3);
        if (!player.getInventory().getItemInMainHand().hasItemMeta()) return;
        System.out.println(4);
        if (!player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()) return;
        System.out.println(5);
        if (!player.getWorld().getName().equalsIgnoreCase(SilverstoneMinigames.MINIGAME_WORLD)) return;
        System.out.println(6);

        //noinspection DataFlowIssue
        if (player.getInventory().getItemInMainHand().getItemMeta().displayName().equals(Component
            .text("Click to Taunt", NamedTextColor.AQUA, TextDecoration.BOLD)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))) {
            event.setCancelled(true);

            // Still on cooldown
            if (spamCooldown.containsKey(player))
                if (spamCooldown.get(player) > System.currentTimeMillis()) return;

            playerTaunt(player);
            spamCooldown.put(player, System.currentTimeMillis() + 1000);
        }
    }

    private void playerTaunt(Player player) {
        // Check if allowed to do command
        if (player.getScoreboardTags().contains("Seeker")) {
            player.sendMessage(Component.text("You must be a hider to use taunts!", NamedTextColor.RED));
            return;
        }

        // Check if on cooldown
        if (!player.hasPermission("silverstone.minigames.hideseek.taunt.bypasscooldown"))
            if (cooldowns.containsKey(player)) if (cooldowns.get(player) > System.currentTimeMillis()) {
                // Still on cooldown
                player.sendMessage(Component.text("You may use a taunt again in ", NamedTextColor.RED)
                    .append(Component.text(
                        (cooldowns.get(player) - System.currentTimeMillis()) / 1000,
                        NamedTextColor.GRAY)).append(Component.text(" seconds.", NamedTextColor.RED)));
                return;
            }

        // Open GUI
        System.out.println(7);
        inventory().open(player);
    }

    private Gui inventory() {
        final int rows = 5;

        return Gui.of(rows).title(Component.text("Taunts", NamedTextColor.DARK_RED, TextDecoration.BOLD))
            .statelessComponent(container -> {
                container.fill(
                    GuiLayout.box(Slot.min(1), Slot.max(rows)),
                    ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());

                container.setItem(
                    10, ItemBuilder.from(Material.BONE).name(Component.text(
                            "Bark",
                            NamedTextColor.AQUA,
                            TextDecoration.BOLD)).lore(getItemLore("<dark_aqua><b>1</b> Taunt Point"))
                        .asGuiItem((player, context) -> {
                            bark(player);
                            addPoints(player, 1);
                            tauntTimer(player, context);
                        }));

                container.setItem(
                    11, ItemBuilder.from(Material.BELL).name(Component.text(
                            "Ding",
                            NamedTextColor.AQUA,
                            TextDecoration.BOLD))
                        .lore(getItemLore("<dark_aqua><b>2</b> <dark_green>Taunt Points"))
                        .asGuiItem((player, context) -> {
                            ding(player);
                            addPoints(player, 2);
                            tauntTimer(player, context);
                        }));

                container.setItem(
                    12, ItemBuilder.from(Material.GHAST_TEAR).name(Component.text(
                            "Scream",
                            NamedTextColor.AQUA,
                            TextDecoration.BOLD))
                        .lore(getItemLore("<dark_aqua><b>3</b> <dark_green>Taunt Points"))
                        .asGuiItem((player, context) -> {
                            scream(player);
                            addPoints(player, 3);
                            tauntTimer(player, context);
                        }));

                container.setItem(
                    13, ItemBuilder.from(Material.DRAGON_HEAD).name(Component.text(
                            "Roar",
                            NamedTextColor.AQUA,
                            TextDecoration.BOLD))
                        .lore(getItemLore("<dark_aqua><b>5</b> <dark_green>Taunt Points"))
                        .asGuiItem((player, context) -> {
                            roar(player);
                            addPoints(player, 5);
                            tauntTimer(player, context);
                        }));

                container.setItem(
                    14, ItemBuilder.from(Material.TNT).name(Component.text(
                            "Explosion",
                            NamedTextColor.AQUA,
                            TextDecoration.BOLD))
                        .lore(getItemLore("<dark_aqua><b>7</b> <dark_green>Taunt Points"))
                        .asGuiItem((player, context) -> {
                            explosion(player);
                            addPoints(player, 7);
                            tauntTimer(player, context);
                        }));

                container.setItem(
                    15, ItemBuilder.from(Material.FIREWORK_ROCKET).name(Component.text(
                            "Fireworks",
                            NamedTextColor.AQUA,
                            TextDecoration.BOLD))
                        .lore(getItemLore("<dark_aqua><b>9</b> <dark_green>Taunt Points"))
                        .asGuiItem((player, context) -> {
                            fireworks(player);
                            addPoints(player, 9);
                            tauntTimer(player, context);
                        }));

                container.setItem(
                    16, ItemBuilder.from(Material.TOTEM_OF_UNDYING).name(Component.text(
                            "Boom",
                            NamedTextColor.AQUA,
                            TextDecoration.BOLD))
                        .lore(getItemLore("<dark_aqua><b>10</b> <dark_green>Taunt Points"))
                        .asGuiItem((player, context) -> {
                            boom(player);
                            addPoints(player, 10);
                            tauntTimer(player, context);
                        }));

                TextComponent notEnough = Component.text(
                    "You don't have enough points to do that!",
                    NamedTextColor.RED);

                // Random taunt
                container.setItem(
                    29, ItemBuilder.skull().texture(
                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDZiYTYzMzQ0ZjQ5ZGQxYzRmNTQ4OGU5MjZiZjNkOWUyYjI5OTE2YTZjNTBkNjEwYmI0MGE1MjczZGM4YzgyIn19fQ==")
                        .name(Component
                            .text("Taunt at random player", NamedTextColor.AQUA, TextDecoration.BOLD)
                            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                        .lore(getItemLore(
                            "<dark_aqua>Costs <dark_green>45</dark_green> Taunt Points",
                            "",
                            "<gray>Sends a random taunt",
                            "<gray>to a random hider",
                            "<#858585><i>(This can include yourself!)")).asGuiItem((player, context) -> {

                            if (getPoints(player) >= 45) {
                                Location loc = new Location(
                                    Bukkit.getWorld(SilverstoneMinigames.MINIGAME_WORLD),
                                    -372,
                                    9,
                                    -65);
                                loc.getBlock().setType(Material.REDSTONE_BLOCK);

                                addPoints(player, -45);
                                tauntTimer(player, context);
                            } else player.sendMessage(notEnough);
                        }));

                // Darkness
                container.setItem(
                    30, ItemBuilder.from(Material.TINTED_GLASS).name(Component.text(
                        "Give seekers darkness",
                        NamedTextColor.AQUA,
                        TextDecoration.BOLD)).lore(getItemLore(
                        "<dark_aqua>Costs <dark_green><b>60</b></dark_green> Taunt Points",
                        "",
                        "<gray>Gives all seekers",
                        "<gray>darkness for 10 seconds")).asGuiItem((player, context) -> {
                        if (getPoints(player) >= 60) {
                            Location loc = new Location(
                                Bukkit.getWorld(SilverstoneMinigames.MINIGAME_WORLD),
                                -372,
                                9,
                                -61);
                            loc.getBlock().setType(Material.REDSTONE_BLOCK);

                            addPoints(player, -60);
                            tauntTimer(player, context);
                        } else player.sendMessage(notEnough);
                    }));

                // Slowness
                container.setItem(
                    31,
                    ItemBuilder.from(Material.NETHERITE_BOOTS).name(Component.text(
                        "Give seekers slowness",
                        NamedTextColor.AQUA,
                        TextDecoration.BOLD)).lore(getItemLore(
                        "<dark_aqua>Costs <dark_green><b>60</b></dark_green> Taunt Points",
                        "",
                        "<gray>Slows all seekers",
                        "<gray>down for 15 seconds")).asGuiItem((player, context) -> {
                        if (getPoints(player) >= 60) {
                            Location loc = new Location(
                                Bukkit.getWorld(SilverstoneMinigames.MINIGAME_WORLD),
                                -374,
                                9,
                                -62);
                            loc.getBlock().setType(Material.REDSTONE_BLOCK);

                            addPoints(player, -60);
                            tauntTimer(player, context);
                        } else player.sendMessage(notEnough);
                    }));

                // Blindness
                container.setItem(
                    32, ItemBuilder.from(Material.NETHERITE_HELMET).name(Component.text("Give seekers blindness",
                        NamedTextColor.AQUA,
                        TextDecoration.BOLD)).lore(getItemLore(
                        "<dark_aqua>Costs <dark_green><b>90</b></dark_green> Taunt Points",
                        "",
                        "<gray>Blinds all seekers",
                        "<gray>for 10 seconds")).asGuiItem((player, context) -> {
                        if (getPoints(player) >= 90) {
                            Location loc = new Location(
                                Bukkit.getWorld(SilverstoneMinigames.MINIGAME_WORLD),
                                -376,
                                9,
                                -62);
                            loc.getBlock().setType(Material.REDSTONE_BLOCK);

                            addPoints(player, -90);
                            tauntTimer(player, context);
                        } else player.sendMessage(notEnough);
                    }));

                // Instant kill seeker
                container.setItem(
                    33, ItemBuilder.from(Material.NETHERITE_SWORD).name(Component.text("Insta-kill a random seeker",
                        NamedTextColor.AQUA,
                        TextDecoration.BOLD)).lore(getItemLore(
                        "<dark_aqua>Costs <dark_green><b>120</b></dark_green> Taunt Points",
                        "",
                        "<gray>Instantly kills a random seeker")).asGuiItem((player, context) -> {
                        if (getPoints(player) >= 120) {
                            Location loc = new Location(
                                Bukkit.getWorld(SilverstoneMinigames.MINIGAME_WORLD),
                                -372,
                                13,
                                -61);
                            loc.getBlock().setType(Material.REDSTONE_BLOCK);

                            addPoints(player, -120);
                            tauntTimer(player, context);
                        } else player.sendMessage(notEnough);
                    }));
            }).build();
    }

    private List<Component> getItemLore(String... lore) {
        return Arrays.stream(lore).map(customLore -> MiniMessage.miniMessage()
            .deserialize("<!i>" + customLore)).toList();
    }

    private void addPoints(Player player, int pointsToAdd) {
        if (player.getGameMode() == GameMode.ADVENTURE && player.getWorld().getName().equalsIgnoreCase(
            SilverstoneMinigames.MINIGAME_WORLD)) points.put(
            player,
            points.getOrDefault(player, 0) + pointsToAdd);

        // Get the player's updated points
        int newPoints = points.get(player);

        player.sendMessage(Component.text("You now have ", NamedTextColor.DARK_GREEN)
            .append(Component.text(newPoints, NamedTextColor.DARK_AQUA))
            .append(Component.text(" taunt points.", NamedTextColor.DARK_GREEN)));
    }

    private int getPoints(Player player) {
        return points.getOrDefault(player, 0);
    }

    public void tauntTimer(Player player, ClickContext context) {
        if (!player.hasPermission("silverstone.minigames.hideseek.taunt.bypasscooldown")) {
            cooldowns.put(player, System.currentTimeMillis() + 15000);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.hasPermission("silverstone.minigames.hideseek.taunt"))
                        player.showTitle(Title.title(
                            Component.empty(),
                            Component.text("You may now taunt again.", NamedTextColor.RED),
                            Title.DEFAULT_TIMES));
                }
            }.runTaskLater(plugin, 300);

            context.guiView().close();
        }
    }

    //
    // Taunts
    //

    // Bark / Bone
    public void bark(Player player) {
        player.getWorld().playSound(
            player.getLocation(),
            Sound.ENTITY_WOLF_AMBIENT,
            SoundCategory.MASTER,
            2.0f,
            1.0f);
    }

    // Ding / Bell
    public void ding(Player player) {
        player.getWorld().playSound(
            player.getLocation(),
            Sound.BLOCK_BELL_USE,
            SoundCategory.MASTER,
            2.25f,
            1.0f);
    }

    // Scream / Tear
    public void scream(Player player) {
        player.getWorld().playSound(
            player.getLocation(),
            Sound.ENTITY_GHAST_HURT,
            SoundCategory.MASTER,
            2.5f,
            1.25f);
    }

    // Roar / Dragon Head
    public void roar(Player player) {
        player.getWorld().playSound(
            player.getLocation(),
            Sound.ENTITY_ENDER_DRAGON_GROWL,
            SoundCategory.MASTER,
            3.0f,
            1.0f);
    }

    // Explosion / TNT
    public void explosion(Player player) {
        player.getWorld().playSound(
            player.getLocation(),
            Sound.ENTITY_GENERIC_EXPLODE,
            SoundCategory.MASTER,
            3.5f,
            0.7f);
        player.getWorld().spawnParticle(
            Particle.EXPLOSION,
            player.getLocation().add(0, 0.5, 0),
            2,
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            null,
            true);
    }

    // Fireworks / Rocket
    public void fireworks(Player player) {
        Location loc = player.getLocation();
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK_ROCKET);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(0);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).withColor(Color.BLUE)
            .with(FireworkEffect.Type.STAR).withFlicker().build());

        fw.setFireworkMeta(fwm);
    }

    // Boom / Totem
    public void boom(Player player) {
        player.getWorld().playSound(
            player.getLocation(),
            Sound.ITEM_TOTEM_USE,
            SoundCategory.MASTER,
            4.0f,
            0.75f);
        player.getWorld().spawnParticle(
            Particle.TOTEM_OF_UNDYING,
            player.getLocation().add(0, 0.5, 0),
            1000,
            -2.0f,
            5.0f,
            -2.0f,
            0.0f,
            null,
            true);
    }
}