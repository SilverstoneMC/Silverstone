package net.silverstonemc.silverstoneminigames.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

public record Minigames(JavaPlugin plugin) implements CommandExecutor, Listener {
    public void closeInv(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
            }
        }.runTask(plugin);
    }

    private static Inventory htpInv;
    private static Inventory gameInv;

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Sorry, but only players can do that.").color(NamedTextColor.RED));
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("htp")) player.openInventory(htpInv);

        if (cmd.getName().equalsIgnoreCase("minigame")) player.openInventory(gameInv);
        return true;
    }

    // On item click
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(htpInv) && !event.getInventory().equals(gameInv)) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        if (event.getInventory().equals(htpInv)) switch (event.getRawSlot()) {
            case 10 -> {
                closeInv(player);
                player.sendMessage(Component.text(ChatColor.GREEN + "Click ").append(
                    Component.text(ChatColor.AQUA + "here").decorate(TextDecoration.UNDERLINED).clickEvent(
                        ClickEvent.openUrl(
                            "https://github.com/SilverstoneMC/Silverstone/wiki/Minigames#boat-racing"))));
            }
            case 11 -> {
                closeInv(player);
                player.sendMessage(Component.text(ChatColor.GREEN + "Click ").append(
                    Component.text(ChatColor.AQUA + "here").decorate(TextDecoration.UNDERLINED).clickEvent(
                        ClickEvent.openUrl(
                            "https://github.com/SilverstoneMC/Silverstone/wiki/Minigames#death-run"))));
            }
            case 12 -> {
                closeInv(player);
                player.sendMessage(Component.text(ChatColor.GREEN + "Click ").append(
                    Component.text(ChatColor.AQUA + "here").decorate(TextDecoration.UNDERLINED).clickEvent(
                        ClickEvent.openUrl(
                            "https://github.com/SilverstoneMC/Silverstone/wiki/Minigames#flying-course"))));
            }
            case 13 -> {
                closeInv(player);
                player.sendMessage(Component.text(ChatColor.GREEN + "Click ").append(
                    Component.text(ChatColor.AQUA + "here").decorate(TextDecoration.UNDERLINED).clickEvent(
                        ClickEvent.openUrl(
                            "https://github.com/SilverstoneMC/Silverstone/wiki/Minigames#hide--seek"))));
            }
            case 14 -> {
                closeInv(player);
                player.sendMessage(Component.text(ChatColor.GREEN + "Click ").append(
                    Component.text(ChatColor.AQUA + "here").decorate(TextDecoration.UNDERLINED).clickEvent(
                        ClickEvent.openUrl(
                            "https://github.com/SilverstoneMC/Silverstone/wiki/Minigames#mazes"))));
            }
            case 15 -> {
                closeInv(player);
                player.sendMessage(Component.text(ChatColor.GREEN + "Click ").append(
                    Component.text(ChatColor.AQUA + "here").decorate(TextDecoration.UNDERLINED).clickEvent(
                        ClickEvent.openUrl(
                            "https://github.com/SilverstoneMC/Silverstone/wiki/Minigames#mini-golf"))));
            }
            case 16 -> {
                closeInv(player);
                player.sendMessage(Component.text(ChatColor.GREEN + "Click ").append(
                    Component.text(ChatColor.AQUA + "here").decorate(TextDecoration.UNDERLINED).clickEvent(
                        ClickEvent.openUrl(
                            "https://github.com/SilverstoneMC/Silverstone/wiki/Minigames#parkour"))));
            }
            case 21 -> {
                closeInv(player);
                player.sendMessage(Component.text(ChatColor.GREEN + "Click ").append(
                    Component.text(ChatColor.AQUA + "here").decorate(TextDecoration.UNDERLINED).clickEvent(
                        ClickEvent.openUrl(
                            "https://github.com/SilverstoneMC/Silverstone/wiki/Minigames#pvp"))));
            }
            case 22 -> {
                closeInv(player);
                player.sendMessage(Component.text(ChatColor.GREEN + "Click ").append(
                    Component.text(ChatColor.AQUA + "here").decorate(TextDecoration.UNDERLINED).clickEvent(
                        ClickEvent.openUrl(
                            "https://github.com/SilverstoneMC/Silverstone/wiki/Minigames#spleef"))));
            }
            case 23 -> {
                closeInv(player);
                player.sendMessage(Component.text(ChatColor.GREEN + "Click ").append(
                    Component.text(ChatColor.AQUA + "here").decorate(TextDecoration.UNDERLINED).clickEvent(
                        ClickEvent.openUrl(
                            "https://github.com/SilverstoneMC/Silverstone/wiki/Minigames#tnt-run"))));
            }
        }
        else switch (event.getRawSlot()) {
            case 3 -> {
                closeInv(player);
                ArrayList<String> games = new ArrayList<>(
                    plugin.getConfig().getStringList("random-game.singleplayer"));
                Random r = new Random();
                player.sendMessage(ChatColor.GREEN + "You should play " + ChatColor.AQUA + games.get(
                    r.nextInt(games.size())));
            }
            case 5 -> {
                closeInv(player);
                ArrayList<String> games = new ArrayList<>(
                    plugin.getConfig().getStringList("random-game.multiplayer"));
                Random r = new Random();
                player.sendMessage(ChatColor.GREEN + "You should play " + ChatColor.AQUA + games.get(
                    r.nextInt(games.size())));
            }
        }
    }

    // Inventory items
    public static void createHtpInv() {
        htpInv = Bukkit.createInventory(null, 36,
            Component.text(ChatColor.translateAlternateColorCodes('&', "&8&lSelect a Minigame")));

        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        // Fill items
        meta.displayName(Component.text(ChatColor.BOLD + ""));
        item.setItemMeta(meta);
        IntStream.rangeClosed(0, 35).boxed().toList().forEach(slot -> htpInv.setItem(slot, item));

        // Boat Racing
        item.setType(Material.SPRUCE_BOAT);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Boat Racing"));
        item.setItemMeta(meta);
        htpInv.setItem(10, item);

        // Death Run
        item.setType(Material.NETHERITE_BOOTS);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Death Run"));
        item.setItemMeta(meta);
        htpInv.setItem(11, item);

        // Flying Course
        item.setType(Material.ELYTRA);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Flying Course"));
        item.setItemMeta(meta);
        htpInv.setItem(12, item);

        // Hide & Seek
        item.setType(Material.COMPASS);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Hide & Seek"));
        item.setItemMeta(meta);
        htpInv.setItem(13, item);

        // Mazes
        item.setType(Material.JUNGLE_LEAVES);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Mazes"));
        item.setItemMeta(meta);
        htpInv.setItem(14, item);

        // Mini Golf
        item.setType(Material.SNOWBALL);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Mini Golf"));
        item.setItemMeta(meta);
        htpInv.setItem(15, item);

        // Parkour
        item.setType(Material.SLIME_BLOCK);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Parkour"));
        item.setItemMeta(meta);
        htpInv.setItem(16, item);

        // PvP
        item.setType(Material.DIAMOND_SWORD);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "PvP"));
        item.setItemMeta(meta);
        htpInv.setItem(21, item);

        // Spleef
        item.setType(Material.NETHERITE_SHOVEL);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "Spleef"));
        item.setItemMeta(meta);
        htpInv.setItem(22, item);

        // TNT Run
        item.setType(Material.TNT);
        meta.displayName(Component.text(ChatColor.AQUA + "" + ChatColor.BOLD + "TNT Run"));
        item.setItemMeta(meta);
        htpInv.setItem(23, item);
    }

    public static void createGameInv() {
        gameInv = Bukkit.createInventory(null, 9,
            Component.text(ChatColor.translateAlternateColorCodes('&', "&8&lSelect Player Count")));

        // 1
        ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();

        GameProfile skullProfile = new GameProfile(UUID.randomUUID(), null);
        skullProfile.getProperties().put("textures", new Property("textures",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDJhNmYwZTg0ZGFlZmM4YjIxYWE5OTQxNWIxNmVkNWZkYWE2ZDhkYzBjM2NkNTkxZjQ5Y2E4MzJiNTc1In19fQ=="));

        try {
            Field field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, skullProfile);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        skullMeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', "&7&l1 Player")));
        skullItem.setItemMeta(skullMeta);
        gameInv.setItem(3, skullItem);

        // 2
        skullProfile = new GameProfile(UUID.randomUUID(), null);
        skullProfile.getProperties().put("textures", new Property("textures",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWEyZDg5MWM2YWU5ZjZiYWEwNDBkNzM2YWI4NGQ0ODM0NGJiNmI3MGQ3ZjFhMjgwZGQxMmNiYWM0ZDc3NyJ9fX0="));

        try {
            Field field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, skullProfile);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        skullMeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', "&7&l2+ Players")));
        skullItem.setItemMeta(skullMeta);
        gameInv.setItem(5, skullItem);
    }
}
