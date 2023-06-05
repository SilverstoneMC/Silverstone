package net.silverstonemc.silverstoneminigames.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
    private static Inventory gameInv;
    private static Inventory htpInv;

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
                player.sendMessage(Component.text("Click ").color(NamedTextColor.GREEN).append(
                    Component.text("here").color(NamedTextColor.AQUA).decorate(TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.openUrl(
                            "https://github.com/SilverstoneMC/Silverstone/wiki/Minigames#boat-racing"))));
            }

            case 11 -> {
                closeInv(player);
                player.sendMessage(Component.text("Click ").color(NamedTextColor.GREEN).append(
                    Component.text("here").color(NamedTextColor.AQUA).decorate(TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.openUrl(
                            "https://github.com/SilverstoneMC/Silverstone/wiki/Minigames#death-run"))));
            }

            case 12 -> {
                closeInv(player);
                player.sendMessage(Component.text("Click ").color(NamedTextColor.GREEN).append(
                    Component.text("here").color(NamedTextColor.AQUA).decorate(TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.openUrl(
                            "https://github.com/SilverstoneMC/Silverstone/wiki/Minigames#flying-course"))));
            }

            case 13 -> {
                closeInv(player);
                player.sendMessage(Component.text("Click ").color(NamedTextColor.GREEN).append(
                    Component.text("here").color(NamedTextColor.AQUA).decorate(TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.openUrl(
                            "https://github.com/SilverstoneMC/Silverstone/wiki/Minigames#hide--seek"))));
            }

            case 14 -> {
                closeInv(player);
                player.sendMessage(Component.text("Click ").color(NamedTextColor.GREEN).append(
                    Component.text("here").color(NamedTextColor.AQUA).decorate(TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.openUrl(
                            "https://github.com/SilverstoneMC/Silverstone/wiki/Minigames#mazes"))));
            }

            case 15 -> {
                closeInv(player);
                player.sendMessage(Component.text("Click ").color(NamedTextColor.GREEN).append(
                    Component.text("here").color(NamedTextColor.AQUA).decorate(TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.openUrl(
                            "https://github.com/SilverstoneMC/Silverstone/wiki/Minigames#mini-golf"))));
            }

            case 16 -> {
                closeInv(player);
                player.sendMessage(Component.text("Click ").color(NamedTextColor.GREEN).append(
                    Component.text("here").color(NamedTextColor.AQUA).decorate(TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.openUrl(
                            "https://github.com/SilverstoneMC/Silverstone/wiki/Minigames#parkour"))));
            }

            case 21 -> {
                closeInv(player);
                player.sendMessage(Component.text("Click ").color(NamedTextColor.GREEN).append(
                    Component.text("here").color(NamedTextColor.AQUA).decorate(TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.openUrl(
                            "https://github.com/SilverstoneMC/Silverstone/wiki/Minigames#pvp"))));
            }

            case 22 -> {
                closeInv(player);
                player.sendMessage(Component.text("Click ").color(NamedTextColor.GREEN).append(
                    Component.text("here").color(NamedTextColor.AQUA).decorate(TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.openUrl(
                            "https://github.com/SilverstoneMC/Silverstone/wiki/Minigames#spleef"))));
            }

            case 23 -> {
                closeInv(player);
                player.sendMessage(Component.text("Click ").color(NamedTextColor.GREEN).append(
                    Component.text("here").color(NamedTextColor.AQUA).decorate(TextDecoration.UNDERLINED)
                        .clickEvent(ClickEvent.openUrl(
                            "https://github.com/SilverstoneMC/Silverstone/wiki/Minigames#tnt-run"))));
            }
        }

        else switch (event.getRawSlot()) {
            case 3 -> {
                closeInv(player);
                ArrayList<String> games = new ArrayList<>(
                    plugin.getConfig().getStringList("random-game.singleplayer"));
                Random r = new Random();
                player.sendMessage(Component.text("You should play ").color(NamedTextColor.GREEN)
                    .append(Component.text(games.get(r.nextInt(games.size()))).color(NamedTextColor.AQUA)));
            }

            case 5 -> {
                closeInv(player);
                ArrayList<String> games = new ArrayList<>(
                    plugin.getConfig().getStringList("random-game.multiplayer"));
                Random r = new Random();
                player.sendMessage(Component.text("You should play ").color(NamedTextColor.GREEN)
                    .append(Component.text(games.get(r.nextInt(games.size()))).color(NamedTextColor.AQUA)));
            }
        }
    }

    // Inventory items
    public void createHtpInv() {
        Inventory inventory = Bukkit.createInventory(null, 36,
            Component.text("Select a Minigame").color(NamedTextColor.DARK_GRAY)
                .decorate(TextDecoration.BOLD));

        // Filler
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(""));
        item.setItemMeta(meta);
        IntStream.rangeClosed(0, 35).boxed().toList().forEach(slot -> inventory.setItem(slot, item));

        inventory.setItem(10, getItem(Material.SPRUCE_BOAT, "Boat Racing"));
        inventory.setItem(11, getItem(Material.NETHERITE_BOOTS, "Death Run"));
        inventory.setItem(12, getItem(Material.ELYTRA, "Flying Course"));
        inventory.setItem(13, getItem(Material.COMPASS, "Hide & Seek"));
        inventory.setItem(14, getItem(Material.JUNGLE_LEAVES, "Mazes"));
        inventory.setItem(15, getItem(Material.SNOWBALL, "Mini Golf"));
        inventory.setItem(16, getItem(Material.SLIME_BLOCK, "Parkour"));
        inventory.setItem(21, getItem(Material.DIAMOND_SWORD, "PvP"));
        inventory.setItem(22, getItem(Material.NETHERITE_SHOVEL, "Spleef"));
        inventory.setItem(23, getItem(Material.TNT, "TNT Run"));

        htpInv = inventory;
    }

    private ItemStack getItem(Material item, String title) {
        ItemStack itemStack = new ItemStack(item);
        ItemMeta meta = itemStack.getItemMeta();

        meta.displayName(Component.text(title).color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD)
            .decoration(TextDecoration.ITALIC, false));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public void createGameInv() {
        Inventory inventory = Bukkit.createInventory(null, 9,
            Component.text("Select Player Count").color(NamedTextColor.DARK_GRAY)
                .decorate(TextDecoration.BOLD));

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

        skullMeta.displayName(
            Component.text("1 Player").color(NamedTextColor.GRAY).decorate(TextDecoration.BOLD));
        skullItem.setItemMeta(skullMeta);
        inventory.setItem(3, skullItem);

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

        skullMeta.displayName(
            Component.text("2+ Players").color(NamedTextColor.GRAY).decorate(TextDecoration.BOLD));
        skullItem.setItemMeta(skullMeta);
        inventory.setItem(5, skullItem);

        gameInv = inventory;
    }
}
