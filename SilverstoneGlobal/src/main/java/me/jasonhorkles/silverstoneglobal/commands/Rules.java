package me.jasonhorkles.silverstoneglobal.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings({"deprecation", "ConstantConditions"})
public record Rules(JavaPlugin plugin) implements CommandExecutor, Listener {

    private static Inventory inv;
    private static final Map<Player, Player> target = new HashMap<>();

    public void closeInv(Player player) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                target.remove(player);
            }
        };
        task.runTask(plugin);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length > 0) {
            Player playerTarget = Bukkit.getPlayer(args[0]);
            // If player is null, cancel the command
            if (playerTarget == null) {
                sender.sendMessage(ChatColor.RED + "Please provide an online player!");
                return true;
            }
            // If console did the command
            if (!(sender instanceof Player player)) {
                playerTarget.performCommand("rules");
                for (Player online : Bukkit.getOnlinePlayers())
                    if (online.hasPermission("silverstone.trialmod"))
                        online.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe rules have been sent to &7" + playerTarget.getName()));
                sender.sendMessage(ChatColor.YELLOW + "The rules have been sent to " + ChatColor.AQUA + playerTarget.getName());
                return true;
            }

            target.put(player, playerTarget);

            if (sender.hasPermission("silverstone.trialmod")) {
                player.openInventory(inv);
                return true;
            }
        }

        // Send rules
        for (String header : plugin.getConfig().getStringList("rules.header"))
            sender.sendMessage(MiniMessage.miniMessage()
                    .parse(header));
        for (int x = 1; x <= plugin.getConfig().getInt("rules.number-of-rules"); x++)
            sender.sendMessage(MiniMessage.miniMessage()
                    .parse(plugin.getConfig()
                            .getString("rules.rule-prefix")
                            .replace("{#}", String.valueOf(x)) + plugin.getConfig().getString("rules." + x)));
        for (String footer : plugin.getConfig().getStringList("rules.footer"))
            sender.sendMessage(MiniMessage.miniMessage()
                    .parse(footer));
        return true;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inv)) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        Player playerTarget = target.get(player);
        int rule;

        // Send rule to player
        switch (event.getRawSlot()) {
            case 10 -> rule = 1;
            case 11 -> rule = 2;
            case 12 -> rule = 3;
            case 13 -> rule = 4;
            case 14 -> rule = 5;
            case 15 -> rule = 6;
            case 16 -> rule = 7;
            case 19 -> rule = 8;
            case 20 -> rule = 9;
            case 21 -> rule = 10;
            case 22 -> rule = 11;
            case 25 -> {
                // All
                playerTarget.performCommand("rules");
                rule = -1;
            }
            default -> {
                return;
            }
        }

        if (rule > 0) playerTarget.sendMessage(MiniMessage.miniMessage()
                .parse("<dark_green>Rule " + plugin.getConfig()
                        .getString("rules.rule-prefix")
                        .replace("{#}", String.valueOf(rule)) + plugin.getConfig().getString("rules." + rule)));

        closeInv(player);

        // Send message to players with silverstone.trialmod perm
        if (rule == -1) {
            for (Player online : Bukkit.getOnlinePlayers())
                if (online.hasPermission("silverstone.trialmod"))
                    online.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe rules have been sent to &7" + playerTarget
                            .getName()));
        } else for (Player online : Bukkit.getOnlinePlayers())
            if (online.hasPermission("silverstone.trialmod"))
                online.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cRule &7" + rule + " &chas been sent to &7" + playerTarget
                        .getName()));
    }


    // Inventory items
    public void createInv() {

        inv = Bukkit.createInventory(null, 36, Component.text(ChatColor.translateAlternateColorCodes('&', "&4&lSend Rule")));

        // Fill items
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text(ChatColor.BOLD + ""));
        item.setType(Material.BLACK_STAINED_GLASS_PANE);
        item.setItemMeta(meta);
        IntStream.rangeClosed(0, 35).boxed().collect(Collectors.toList()).forEach(slot -> inv.setItem(slot, item));

        // 1
        ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
        List<String> skullLore = new ArrayList<>();

        GameProfile skullProfile = new GameProfile(UUID.randomUUID(), null);
        skullProfile.getProperties()
                .put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDJhNmYwZTg0ZGFlZmM4YjIxYWE5OTQxNWIxNmVkNWZkYWE2ZDhkYzBjM2NkNTkxZjQ5Y2E4MzJiNTc1In19fQ=="));

        try {
            Field field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, skullProfile);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        skullMeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', "&b&l1")));
        for (String rule : plugin.getConfig().getStringList("rules.GUI.1"))
            skullLore.add(ChatColor.translateAlternateColorCodes('&', rule));
        skullMeta.setLore(skullLore);
        skullItem.setItemMeta(skullMeta);
        inv.setItem(10, skullItem);

        // 2
        skullLore.clear();
        skullProfile = new GameProfile(UUID.randomUUID(), null);
        skullProfile.getProperties()
                .put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTZmYWI5OTFkMDgzOTkzY2I4M2U0YmNmNDRhMGI2Y2VmYWM2NDdkNDE4OWVlOWNiODIzZTljYzE1NzFlMzgifX19"));

        try {
            Field field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, skullProfile);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        skullMeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', "&b&l2")));
        for (String rule : plugin.getConfig().getStringList("rules.GUI.2"))
            skullLore.add(ChatColor.translateAlternateColorCodes('&', rule));
        skullMeta.setLore(skullLore);
        skullItem.setItemMeta(skullMeta);
        inv.setItem(11, skullItem);

        // 3
        skullLore.clear();
        skullProfile = new GameProfile(UUID.randomUUID(), null);
        skullProfile.getProperties()
                .put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2QzMTliOTM0M2YxN2EzNTYzNmJjYmMyNmI4MTk2MjVhOTMzM2RlMzczNjExMWYyZTkzMjgyN2M4ZTc0OSJ9fX0="));

        try {
            Field field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, skullProfile);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        skullMeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', "&b&l3")));
        for (String rule : plugin.getConfig().getStringList("rules.GUI.3"))
            skullLore.add(ChatColor.translateAlternateColorCodes('&', rule));
        skullMeta.setLore(skullLore);
        skullItem.setItemMeta(skullMeta);
        inv.setItem(12, skullItem);

        // 4
        skullLore.clear();
        skullProfile = new GameProfile(UUID.randomUUID(), null);
        skullProfile.getProperties()
                .put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDE5OGQ1NjIxNjE1NjExNDI2NTk3M2MyNThmNTdmYzc5ZDI0NmJiNjVlM2M3N2JiZTgzMTJlZTM1ZGI2In19fQ=="));

        try {
            Field field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, skullProfile);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        skullMeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', "&b&l4")));
        for (String rule : plugin.getConfig().getStringList("rules.GUI.4"))
            skullLore.add(ChatColor.translateAlternateColorCodes('&', rule));
        skullMeta.setLore(skullLore);
        skullItem.setItemMeta(skullMeta);
        inv.setItem(13, skullItem);

        // 5
        skullLore.clear();
        skullProfile = new GameProfile(UUID.randomUUID(), null);
        skullProfile.getProperties()
                .put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2ZiOTFiYjk3NzQ5ZDZhNmVlZDQ0NDlkMjNhZWEyODRkYzRkZTZjMzgxOGVlYTVjN2UxNDlkZGRhNmY3YzkifX19"));

        try {
            Field field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, skullProfile);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        skullMeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', "&b&l5")));
        for (String rule : plugin.getConfig().getStringList("rules.GUI.5"))
            skullLore.add(ChatColor.translateAlternateColorCodes('&', rule));
        skullMeta.setLore(skullLore);
        skullItem.setItemMeta(skullMeta);
        inv.setItem(14, skullItem);

        // 6
        skullLore.clear();
        skullProfile = new GameProfile(UUID.randomUUID(), null);
        skullProfile.getProperties()
                .put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWM2MTNmODBhNTU0OTE4YzdhYjJjZDRhMjc4NzUyZjE1MTQxMmE0NGE3M2Q3YTI4NmQ2MWQ0NWJlNGVhYWUxIn19fQ=="));

        try {
            Field field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, skullProfile);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        skullMeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', "&b&l6")));
        for (String rule : plugin.getConfig().getStringList("rules.GUI.6"))
            skullLore.add(ChatColor.translateAlternateColorCodes('&', rule));
        skullMeta.setLore(skullLore);
        skullItem.setItemMeta(skullMeta);
        inv.setItem(15, skullItem);

        // 7
        skullLore.clear();
        skullProfile = new GameProfile(UUID.randomUUID(), null);
        skullProfile.getProperties()
                .put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWUxOThmZDgzMWNiNjFmMzkyN2YyMWNmOGE3NDYzYWY1ZWEzYzdlNDNiZDNlOGVjN2QyOTQ4NjMxY2NlODc5In19fQ=="));

        try {
            Field field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, skullProfile);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        skullMeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', "&b&l7")));
        for (String rule : plugin.getConfig().getStringList("rules.GUI.7"))
            skullLore.add(ChatColor.translateAlternateColorCodes('&', rule));
        skullMeta.setLore(skullLore);
        skullItem.setItemMeta(skullMeta);
        inv.setItem(16, skullItem);

        // 8
        skullLore.clear();
        skullProfile = new GameProfile(UUID.randomUUID(), null);
        skullProfile.getProperties()
                .put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODRhZDEyYzJmMjFhMTk3MmYzZDJmMzgxZWQwNWE2Y2MwODg0ODlmY2ZkZjY4YTcxM2IzODc0ODJmZTkxZTIifX19"));

        try {
            Field field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, skullProfile);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        skullMeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', "&b&l8")));
        for (String rule : plugin.getConfig().getStringList("rules.GUI.8"))
            skullLore.add(ChatColor.translateAlternateColorCodes('&', rule));
        skullMeta.setLore(skullLore);
        skullItem.setItemMeta(skullMeta);
        inv.setItem(19, skullItem);

        // 9
        skullLore.clear();
        skullProfile = new GameProfile(UUID.randomUUID(), null);
        skullProfile.getProperties()
                .put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWY3YWEwZDk3OTgzY2Q2N2RmYjY3YjdkOWQ5YzY0MWJjOWFhMzRkOTY2MzJmMzcyZDI2ZmVlMTlmNzFmOGI3In19fQ=="));

        try {
            Field field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, skullProfile);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        skullMeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', "&b&l9")));
        for (String rule : plugin.getConfig().getStringList("rules.GUI.9"))
            skullLore.add(ChatColor.translateAlternateColorCodes('&', rule));
        skullMeta.setLore(skullLore);
        skullItem.setItemMeta(skullMeta);
        inv.setItem(20, skullItem);

        // 10
        skullLore.clear();
        skullProfile = new GameProfile(UUID.randomUUID(), null);
        skullProfile.getProperties()
                .put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBjZjk3OTRmYmMwODlkYWIwMzcxNDFmNjc4NzVhYjM3ZmFkZDEyZjNiOTJkYmE3ZGQyMjg4ZjFlOTg4MzYifX19"));

        try {
            Field field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, skullProfile);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        skullMeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', "&b&l10")));
        for (String rule : plugin.getConfig().getStringList("rules.GUI.10"))
            skullLore.add(ChatColor.translateAlternateColorCodes('&', rule));
        skullMeta.setLore(skullLore);
        skullItem.setItemMeta(skullMeta);
        inv.setItem(21, skullItem);

        // 11
        skullLore.clear();
        skullProfile = new GameProfile(UUID.randomUUID(), null);
        skullProfile.getProperties()
                .put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzk5N2U3YzE5NGM0NzAyY2QyMTQ0MjhlMWY1ZTY0NjE1NzI2YTUyZjdjNmUzYTMzNzg5MzA5MWU3ODY3MjJhIn19fQ=="));

        try {
            Field field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, skullProfile);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        skullMeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', "&b&l11")));
        for (String rule : plugin.getConfig().getStringList("rules.GUI.11"))
            skullLore.add(ChatColor.translateAlternateColorCodes('&', rule));
        skullMeta.setLore(skullLore);
        skullItem.setItemMeta(skullMeta);
        inv.setItem(22, skullItem);

        // Send All
        skullLore.clear();
        skullProfile = new GameProfile(UUID.randomUUID(), null);
        skullProfile.getProperties()
                .put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWEyZDg5MWM2YWU5ZjZiYWEwNDBkNzM2YWI4NGQ0ODM0NGJiNmI3MGQ3ZjFhMjgwZGQxMmNiYWM0ZDc3NyJ9fX0="));

        try {
            Field field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, skullProfile);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        skullMeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', "&b&lSend all rules")));
        skullMeta.setLore(skullLore);
        skullItem.setItemMeta(skullMeta);
        inv.setItem(25, skullItem);
    }
}
