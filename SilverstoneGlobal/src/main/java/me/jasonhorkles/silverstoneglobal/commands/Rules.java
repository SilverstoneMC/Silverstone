package me.jasonhorkles.silverstoneglobal.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.jasonhorkles.silverstoneglobal.SilverstoneGlobal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("deprecation")
public class Rules implements CommandExecutor, Listener {

    private static SilverstoneGlobal plugin;

    public Rules(SilverstoneGlobal plugin) {
        Rules.plugin = plugin;
    }

    private static Inventory inv;
    private static Player playerTarget;

    public void closeInv(Player player) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            }
        };
        task.runTask(plugin);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length > 0) {
            playerTarget = Bukkit.getPlayer(args[0]);
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
                        online.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe rules have been sent to &7" + playerTarget
                                .getName()));
                sender.sendMessage(ChatColor.YELLOW + "The rules have been sent to " + ChatColor.AQUA + playerTarget.getName());
                return true;
            }
            if (sender.hasPermission("silverstone.trialmod")) {
                player.openInventory(inv);
                return true;
            }
        }
        // Send rules
        for (String header : plugin.getConfig().getStringList("rules.header"))
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', header));
        for (int x = 1; x <= plugin.getConfig().getInt("rules.number-of-rules"); x++)
            if (x == 2) sender.sendMessage(Component.text()
                    .content("2").color(NamedTextColor.DARK_GREEN)
                    .append(Component.text()
                            .content(" | ")
                            .color(NamedTextColor.GRAY))
                    .append(Component.text()
                            .content("Hacked and ")
                            .color(TextColor.fromHexString("#23B8CF")))
                    .append(Component.text()
                            .content("other modified clients*")
                            .color(TextColor.fromHexString("#15E8E8"))
                            .decoration(TextDecoration.UNDERLINED, true)
                            .clickEvent(ClickEvent.openUrl("https://github.com/JasonHorkles/Silverstone/wiki/Modifications")))
                    .append(Component.text()
                            .content(" are prohibited")
                            .color(TextColor.fromHexString("#23B8CF"))));
            else sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "" + plugin.getConfig()
                    .getString("rules." + x)));
        for (String footer : plugin.getConfig().getStringList("rules.footer"))
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', footer));
        return true;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inv)) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        int rule = 1;

        // Send rule to player
        switch (event.getRawSlot()) {
            case 10 -> {
                // 1
                playerTarget.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
                        .getString("rules.rule-prefix") + plugin.getConfig().getString("rules.1")));
                closeInv(player);
            }
            case 11 -> {
                // 2
                playerTarget.sendMessage(Component.text()
                        .content("2").color(NamedTextColor.DARK_GREEN)
                        .append(Component.text()
                                .content(" | ")
                                .color(NamedTextColor.GRAY))
                        .append(Component.text()
                                .content("Hacked and ")
                                .color(TextColor.fromHexString("#23B8CF")))
                        .append(Component.text()
                                .content("other modified clients*")
                                .color(TextColor.fromHexString("#15E8E8"))
                                .decoration(TextDecoration.UNDERLINED, true)
                                .clickEvent(ClickEvent.openUrl("https://github.com/JasonHorkles/Silverstone/wiki/Modifications")))
                        .append(Component.text()
                                .content(" are prohibited")
                                .color(TextColor.fromHexString("#23B8CF"))));
                rule = 2;
                closeInv(player);
            }
            case 12 -> {
                // 3
                playerTarget.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
                        .getString("rules.rule-prefix") + plugin.getConfig().getString("rules.3")));
                rule = 3;
                closeInv(player);
            }
            case 13 -> {
                // 4
                playerTarget.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
                        .getString("rules.rule-prefix") + plugin.getConfig().getString("rules.4")));
                rule = 4;
                closeInv(player);
            }
            case 14 -> {
                // 5
                playerTarget.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
                        .getString("rules.rule-prefix") + plugin.getConfig().getString("rules.5")));
                rule = 5;
                closeInv(player);
            }
            case 15 -> {
                // 6
                playerTarget.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
                        .getString("rules.rule-prefix") + plugin.getConfig().getString("rules.6")));
                rule = 6;
                closeInv(player);
            }
            case 16 -> {
                // 7
                playerTarget.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
                        .getString("rules.rule-prefix") + plugin.getConfig().getString("rules.7")));
                rule = 7;
                closeInv(player);
            }
            case 19 -> {
                // 8
                playerTarget.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
                        .getString("rules.rule-prefix") + plugin.getConfig().getString("rules.8")));
                rule = 8;
                closeInv(player);
            }
            case 20 -> {
                // 9
                playerTarget.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
                        .getString("rules.rule-prefix") + plugin.getConfig().getString("rules.9")));
                rule = 9;
                closeInv(player);
            }
            case 21 -> {
                // 10
                playerTarget.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
                        .getString("rules.rule-prefix") + plugin.getConfig().getString("rules.10")));
                rule = 10;
                closeInv(player);
            }
            case 22 -> {
                // 11
                playerTarget.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
                        .getString("rules.rule-prefix") + plugin.getConfig().getString("rules.11")));
                rule = 11;
                closeInv(player);
            }
            case 25 -> {
                // All
                playerTarget.performCommand("rules");
                rule = -1;
                closeInv(player);
            }
        }

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
    public static void createInv() {

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
