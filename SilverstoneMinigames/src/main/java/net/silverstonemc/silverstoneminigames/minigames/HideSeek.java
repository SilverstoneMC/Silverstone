package net.silverstonemc.silverstoneminigames.minigames;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.silverstonemc.silverstoneminigames.CustomSkull;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.IntStream;

@SuppressWarnings("DataFlowIssue")
public record HideSeek(JavaPlugin plugin) implements CommandExecutor, Listener {
    private static final Map<Player, Long> cooldowns = new HashMap<>();
    private static final Map<Player, Integer> points = new HashMap<>();
    private static final Map<Player, Long> spamCooldown = new HashMap<>();
    private static Inventory inv;

    public void closeInv(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.closeInventory();
            }
        }.runTask(plugin);
    }

    public void tauntTimer(Player player) {
        if (!player.hasPermission("silverstone.minigames.hideseek.taunt.bypasscooldown")) {
            cooldowns.put(player, System.currentTimeMillis() + 15000);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.hasPermission("silverstone.minigames.hideseek.taunt")) player.showTitle(
                        Title.title(Component.text(""),
                            Component.text("You may now taunt again.").color(NamedTextColor.RED),
                            Title.DEFAULT_TIMES));
                }
            }.runTaskLater(plugin, 300);
        }
    }

    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("hsresettauntpoints")) {
            points.clear();
            sender.sendMessage(Component.text("Points reset!").color(NamedTextColor.GREEN));
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("hsrandomtaunt")) {
            if (args.length > 0) {
                List<Entity> player = Bukkit.selectEntities(sender, args[0]);
                Player randomPlayer;
                try {
                    randomPlayer = (Player) player.get(0);
                } catch (IndexOutOfBoundsException e) {
                    sender.sendMessage(
                        Component.text("Please provide a valid selector!").color(NamedTextColor.RED));
                    return true;
                }

                Random r = new Random();
                int randomTaunt = r.nextInt(7) + 1;

                switch (randomTaunt) {
                    case 1 -> bark(randomPlayer);
                    case 2 -> ding(randomPlayer);
                    case 3 -> scream(randomPlayer);
                    case 4 -> roar(randomPlayer);
                    case 5 -> explosion(randomPlayer);
                    case 6 -> fireworks(randomPlayer);
                    case 7 -> boom(randomPlayer);
                }
            } else sender.sendMessage(
                Component.text("Please provide a valid selector!").color(NamedTextColor.RED));
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("hsheartbeat")) {
            if (args.length > 0) for (Entity players : Bukkit.selectEntities(sender, args[0])) {
                if (!(players instanceof Player player)) continue;
                // 0-5 sec
                playHeartbeat(player, 24, 8, 5);

                // 6-10 sec
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        playHeartbeat(player, 20, 6, 3);
                    }
                }.runTaskLater(plugin, 6 * 20L);

                // 11-15 sec
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        playHeartbeat(player, 16, 5, 3);
                    }
                }.runTaskLater(plugin, 11 * 20L);
            }
            else sender.sendMessage(
                Component.text("Please provide a valid selector!").color(NamedTextColor.RED));
            return true;
        }
        return false;
    }

    // On bell click
    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction()
            .equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (player.getInventory().getItemInMainHand().getType() != Material.BELL) return;
        if (!player.getInventory().getItemInMainHand().hasItemMeta()) return;
        if (!player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()) return;
        if (!player.getWorld().getName()
            .equalsIgnoreCase(plugin.getConfig().getString("minigame-world"))) return;

        if (player.getInventory().getItemInMainHand().getItemMeta().displayName().equals(
            Component.text("Taunt Menu",NamedTextColor.AQUA,TextDecoration.BOLD)
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
            player.sendMessage(
                Component.text("You must be a hider to use taunts!").color(NamedTextColor.RED));
            return;
        }

        // Check if on cooldown
        if (!player.hasPermission("silverstone.minigames.hideseek.taunt.bypasscooldown"))
            if (cooldowns.containsKey(player)) if (cooldowns.get(player) > System.currentTimeMillis()) {
                // Still on cooldown
                player.sendMessage(Component.text("You may use a taunt again in ", NamedTextColor.RED).append(
                    Component.text((cooldowns.get(player) - System.currentTimeMillis()) / 1000,
                        NamedTextColor.GRAY)).append(Component.text(" seconds.", NamedTextColor.RED)));
                return;
            }

        // Open GUI
        player.openInventory(inv);
    }

    private void playHeartbeat(Player player, int speed1, int speed2, int stopAfter) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam("Hiders")
                    .getEntries().contains(player.getName())) {
                    this.cancel();
                    return;
                }

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.PLAYERS,
                    2, 0);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3, 0, false, false, false));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.removePotionEffect(PotionEffectType.SLOW);
                    }
                }.runTaskLater(plugin, 2);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM,
                            SoundCategory.PLAYERS, 2, 0);
                        player.addPotionEffect(
                            new PotionEffect(PotionEffectType.SLOW, 3, 1, false, false, false));
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.removePotionEffect(PotionEffectType.SLOW);
                            }
                        }.runTaskLater(plugin, 2);
                    }
                }.runTaskLater(plugin, speed2);
            }
        };
        task.runTaskTimer(plugin, 0, speed1);

        new BukkitRunnable() {
            @Override
            public void run() {
                task.cancel();
            }
        }.runTaskLater(plugin, (stopAfter * 20L) + 20L);
    }

    // On item click
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inv)) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack item = new ItemStack(Material.BELL);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Taunt").color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        item.setItemMeta(meta);

        int currentPoints = 0;

        // Check if they have points already, and if so, save it
        if (points.containsKey(player)) currentPoints = points.get(player);

        switch (event.getRawSlot()) {
            case 10 -> {
                // Bark / Bone
                bark(player);
                closeInv(player);
                if (player.getGameMode().equals(GameMode.ADVENTURE) && player.getWorld().getName()
                    .equalsIgnoreCase(plugin.getConfig().getString("minigame-world")))
                    points.put(player, currentPoints + 1);
                tauntTimer(player);
            }

            case 11 -> {
                // Ding / Bell
                ding(player);
                closeInv(player);
                if (player.getGameMode().equals(GameMode.ADVENTURE) && player.getWorld().getName()
                    .equalsIgnoreCase(plugin.getConfig().getString("minigame-world")))
                    points.put(player, currentPoints + 2);
                tauntTimer(player);
            }

            case 12 -> {
                // Scream / Tear
                scream(player);
                closeInv(player);
                if (player.getGameMode().equals(GameMode.ADVENTURE) && player.getWorld().getName()
                    .equalsIgnoreCase(plugin.getConfig().getString("minigame-world")))
                    points.put(player, currentPoints + 3);
                tauntTimer(player);
            }

            case 13 -> {
                // Roar / Dragon Head
                roar(player);
                closeInv(player);
                if (player.getGameMode().equals(GameMode.ADVENTURE) && player.getWorld().getName()
                    .equalsIgnoreCase(plugin.getConfig().getString("minigame-world")))
                    points.put(player, currentPoints + 5);
                tauntTimer(player);
            }

            case 14 -> {
                // Explosion / TNT
                explosion(player);
                closeInv(player);
                if (player.getGameMode().equals(GameMode.ADVENTURE) && player.getWorld().getName()
                    .equalsIgnoreCase(plugin.getConfig().getString("minigame-world")))
                    points.put(player, currentPoints + 8);
                tauntTimer(player);
            }

            case 15 -> {
                // Fireworks / Firework Rocket
                fireworks(player);
                closeInv(player);
                if (player.getGameMode().equals(GameMode.ADVENTURE) && player.getWorld().getName()
                    .equalsIgnoreCase(plugin.getConfig().getString("minigame-world")))
                    points.put(player, currentPoints + 13);
                tauntTimer(player);
            }

            case 16 -> {
                // Boom / Totem
                boom(player);
                closeInv(player);
                if (player.getGameMode().equals(GameMode.ADVENTURE) && player.getWorld().getName()
                    .equalsIgnoreCase(plugin.getConfig().getString("minigame-world")))
                    points.put(player, currentPoints + 15);
                tauntTimer(player);
            }

            case 28 -> {
                // Random taunt random player
                if (currentPoints >= 60) {
                    closeInv(player);
                    if (player.getGameMode().equals(GameMode.ADVENTURE) && player.getWorld().getName()
                        .equalsIgnoreCase(plugin.getConfig().getString("minigame-world")))
                        points.put(player, currentPoints - 60);
                    Location loc = new Location(
                        Bukkit.getWorld(plugin.getConfig().getString("minigame-world")), -372, 9, -65);
                    loc.getBlock().setType(Material.REDSTONE_BLOCK);
                    tauntTimer(player);
                } else player.sendMessage(
                    Component.text("You don't have enough points to do that!").color(NamedTextColor.RED));
            }

            case 31 -> {
                // Seekers slowness
                if (currentPoints >= 100) {
                    closeInv(player);
                    if (player.getGameMode().equals(GameMode.ADVENTURE) && player.getWorld().getName()
                        .equalsIgnoreCase(plugin.getConfig().getString("minigame-world")))
                        points.put(player, currentPoints - 100);
                    Location loc = new Location(
                        Bukkit.getWorld(plugin.getConfig().getString("minigame-world")), -374, 9, -62);
                    loc.getBlock().setType(Material.REDSTONE_BLOCK);
                    tauntTimer(player);
                } else player.sendMessage(
                    Component.text("You don't have enough points to do that!").color(NamedTextColor.RED));
            }

            case 34 -> {
                // Seekers blindness
                if (currentPoints >= 125) {
                    closeInv(player);
                    if (player.getGameMode().equals(GameMode.ADVENTURE) && player.getWorld().getName()
                        .equalsIgnoreCase(plugin.getConfig().getString("minigame-world")))
                        points.put(player, currentPoints - 125);
                    Location loc = new Location(
                        Bukkit.getWorld(plugin.getConfig().getString("minigame-world")), -376, 9, -62);
                    loc.getBlock().setType(Material.REDSTONE_BLOCK);
                    tauntTimer(player);
                } else player.sendMessage(
                    Component.text("You don't have enough points to do that!").color(NamedTextColor.RED));
            }
        }

        // Set bell points if in adventure
        if (player.getGameMode().equals(GameMode.ADVENTURE) && player.getWorld().getName()
            .equalsIgnoreCase(plugin.getConfig().getString("minigame-world"))) {
            int newPoints = 0;
            // Check if they have points already, and if so, save it
            if (points.containsKey(player)) newPoints = points.get(player);
            if (newPoints > 0 && newPoints <= 125) {
                item.setAmount(newPoints);
                item.getItemMeta().displayName(
                    Component.text("Taunt", NamedTextColor.AQUA, TextDecoration.BOLD)
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                player.getInventory().setItem(0, item);
            } else if (newPoints == 0) {
                item.setAmount(1);
                item.getItemMeta().displayName(
                    Component.text("Taunt", NamedTextColor.AQUA, TextDecoration.BOLD)
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                player.getInventory().setItem(0, item);
            } else if (newPoints > 125) {
                item.setAmount(125);
                item.getItemMeta().displayName(
                    Component.text("Taunt", NamedTextColor.AQUA, TextDecoration.BOLD)
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                player.getInventory().setItem(0, item);
            }
        }
    }

    // Inventory items
    public void createInv() {
        Inventory inventory = Bukkit.createInventory(null, 45,
            Component.text("Taunts").color(NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD));

        // Filler
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(""));
        item.setItemMeta(meta);
        IntStream.rangeClosed(0, 44).boxed().toList().forEach(slot -> inventory.setItem(slot, item));

        inventory.setItem(10, getItem(Material.BONE, "Bark", "<dark_aqua><b>1</b> <dark_green>Taunt Point"));
        inventory.setItem(11, getItem(Material.BELL, "Ding", "<dark_aqua><b>2</b> <dark_green>Taunt Points"));
        inventory.setItem(12,
            getItem(Material.GHAST_TEAR, "Scream", "<dark_aqua><b>3</b> <dark_green>Taunt Points"));
        inventory.setItem(13,
            getItem(Material.DRAGON_HEAD, "Roar", "<dark_aqua><b>5</b> <dark_green>Taunt Points"));
        inventory.setItem(14,
            getItem(Material.TNT, "Explosion", "<dark_aqua><b>8</b> <dark_green>Taunt Points"));
        inventory.setItem(15,
            getItem(Material.FIREWORK_ROCKET, "Fireworks", "<dark_aqua><b>13</b> <dark_green>Taunt Points"));
        inventory.setItem(16,
            getItem(Material.TOTEM_OF_UNDYING, "Boom", "<dark_aqua><b>15</b> <dark_green>Taunt Points"));

        // Random taunt
        ItemStack skullItem = new CustomSkull().createCustomSkull(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDZiYTYzMzQ0ZjQ5ZGQxYzRmNTQ4OGU5MjZiZjNkOWUyYjI5OTE2YTZjNTBkNjEwYmI0MGE1MjczZGM4YzgyIn19fQ==",
            "Taunt");
        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();

        skullMeta.displayName(
            Component.text("Taunt at random player").color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD)
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

        List<Component> skullLore = new ArrayList<>();
        skullLore.add(Component.text("Costs ", NamedTextColor.DARK_AQUA)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
            .append(Component.text("60", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
            .append(Component.text(" Taunt Points", NamedTextColor.DARK_AQUA)));
        skullLore.add(Component.text(""));
        skullLore.add(Component.text("Sends a random taunt", NamedTextColor.GRAY)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        skullLore.add(Component.text("to a random hider", NamedTextColor.GRAY)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        skullLore.add(Component.text("(This can include yourself!)", TextColor.fromHexString("#858585"),
            TextDecoration.ITALIC));

        skullMeta.lore(skullLore);
        skullItem.setItemMeta(skullMeta);

        inventory.setItem(28, skullItem);

        // Slowness
        inventory.setItem(31, getItem(Material.NETHERITE_BOOTS, "Give seekers slowness",
            "<dark_aqua>Costs <dark_green><b>100</b> <dark_aqua>Taunt Points", "", "<gray>Slows all seekers",
            "<gray>down for 15 seconds"));

        // Blindness
        inventory.setItem(34, getItem(Material.NETHERITE_HELMET, "Give seekers blindness",
            "<dark_aqua>Costs <dark_green><b>125</b> <dark_aqua>Taunt Points", "", "<gray>Gives all seekers",
            "<gray>blindness for 10 seconds"));

        inv = inventory;
    }

    private ItemStack getItem(Material item, String title, String... lore) {
        ItemStack itemStack = new ItemStack(item);
        ItemMeta meta = itemStack.getItemMeta();
        List<Component> parsedLore = new ArrayList<>();

        meta.displayName(Component.text(title).color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        for (String customLore : lore)
            parsedLore.add(MiniMessage.miniMessage().deserialize("<!i>" + customLore));
        meta.lore(parsedLore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    // Bark / Bone
    private void bark(Player player) {
        player.getWorld()
            .playSound(player.getLocation(), Sound.ENTITY_WOLF_AMBIENT, SoundCategory.MASTER, 2f, 1f);
    }

    // Ding / Bell
    private void ding(Player player) {
        player.getWorld()
            .playSound(player.getLocation(), Sound.BLOCK_BELL_USE, SoundCategory.MASTER, 2.25f, 1f);
    }

    // Scream / Tear
    private void scream(Player player) {
        player.getWorld()
            .playSound(player.getLocation(), Sound.ENTITY_GHAST_HURT, SoundCategory.MASTER, 2.5f, 1.25f);
    }

    // Roar / Dragon Head
    private void roar(Player player) {
        player.getWorld()
            .playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.MASTER, 3f, 1f);
    }

    // Explosion / TNT
    private void explosion(Player player) {
        player.getWorld()
            .playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.MASTER, 3.5f, 0.7f);
        player.getWorld()
            .spawnParticle(Particle.EXPLOSION_HUGE, player.getLocation().add(0, 0.5, 0), 2, 0f, 0f, 0f, 0f,
                null, true);
    }

    // Fireworks / Rocket
    private void fireworks(Player player) {
        Location loc = player.getLocation();
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(0);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).withColor(Color.BLUE)
            .with(FireworkEffect.Type.STAR).withFlicker().build());

        fw.setFireworkMeta(fwm);
    }

    // Boom / Totem
    private void boom(Player player) {
        player.getWorld()
            .playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, SoundCategory.MASTER, 4f, 0.75f);
        player.getWorld()
            .spawnParticle(Particle.TOTEM, player.getLocation().add(0, 0.5, 0), 1000, -2f, 5f, -2f, 0f, null,
                true);
    }
}