package me.jasonhorkles.silverstoneglobal.commands.guis;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.jasonhorkles.silverstoneglobal.SilverstoneGlobal;
import net.kyori.adventure.text.Component;
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
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SocialGUI implements CommandExecutor, Listener {

    private static SilverstoneGlobal plugin;

    public SocialGUI(SilverstoneGlobal plugin) {
        SocialGUI.plugin = plugin;
    }

    private static Inventory inv;

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

    @SuppressWarnings("ConstantConditions")
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        switch (cmd.getName().toLowerCase()) {
            case "social" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(ChatColor.RED + "Sorry, but only players can do that.");
                    return true;
                }
                // Open GUI
                player.openInventory(inv);
            }
            case "discord" -> {
                sender.sendMessage("");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
                        .getString("socialGUI.Discord.message")));
            }
            case "github" -> {
                sender.sendMessage("");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
                        .getString("socialGUI.GitHub.message")));
            }
            case "instagram" -> {
                sender.sendMessage("");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
                        .getString("socialGUI.Instagram.message")));
            }
            case "twitch" -> {
                sender.sendMessage("");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
                        .getString("socialGUI.Twitch.message")));
            }
            case "twitter" -> {
                sender.sendMessage("");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
                        .getString("socialGUI.Twitter.message")));
            }
            case "youtube" -> {
                sender.sendMessage("");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
                        .getString("socialGUI.YouTube.message")));
            }
        }
        return true;
    }


    // On item click
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inv)) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        switch (event.getRawSlot()) {
            case 10 -> {
                // Discord
                player.performCommand("discord");
                closeInv(player);
            }
            case 11 -> {
                // GitHub
                player.performCommand("github");
                closeInv(player);
            }
            case 12 -> {
                // Instagram
                player.performCommand("instagram");
                closeInv(player);
            }
            case 14 -> {
                // Twitch
                player.performCommand("twitch");
                closeInv(player);
            }
            case 15 -> {
                // Twitter
                player.performCommand("twitter");
                closeInv(player);
            }
            case 16 -> {
                // YouTube
                player.performCommand("youtube");
                closeInv(player);
            }
        }
    }


    // Inventory items
    public static void createInv() {

        inv = Bukkit.createInventory(null, 27, Component.text(ChatColor.translateAlternateColorCodes('&', "&d&lSocial Media")));

        // Fill items
        ItemStack glassItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glassItem.getItemMeta();

        glassMeta.displayName(Component.text(ChatColor.BOLD + ""));
        glassItem.setType(Material.BLACK_STAINED_GLASS_PANE);
        glassItem.setItemMeta(glassMeta);
        IntStream.rangeClosed(0, 26).boxed().collect(Collectors.toList()).forEach(slot -> inv.setItem(slot, glassItem));

        // Discord
        ItemStack itemDiscord = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta metaDiscord = (SkullMeta) itemDiscord.getItemMeta();

        GameProfile profileDiscord = new GameProfile(UUID.randomUUID(), null);
        profileDiscord.getProperties()
                .put("textures", new Property("textures", plugin.getConfig().getString("socialGUI.Discord.skullID")));

        try {
            Field field = metaDiscord.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(metaDiscord, profileDiscord);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        itemDiscord.setType(Material.PLAYER_HEAD);
        metaDiscord.displayName(Component.text("Discord")
                .color(TextColor.fromHexString("#7289da"))
                .decorate(TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));
        itemDiscord.setItemMeta(metaDiscord);
        inv.setItem(10, itemDiscord);

        // GitHub
        ItemStack itemGitHub = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta metaGitHub = (SkullMeta) itemGitHub.getItemMeta();

        GameProfile profileGitHub = new GameProfile(UUID.randomUUID(), null);
        profileGitHub.getProperties()
                .put("textures", new Property("textures", plugin.getConfig().getString("socialGUI.GitHub.skullID")));

        try {
            Field field = metaGitHub.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(metaGitHub, profileGitHub);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        itemGitHub.setType(Material.PLAYER_HEAD);
        metaGitHub.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', "&f&lGitHub")));
        itemGitHub.setItemMeta(metaGitHub);
        inv.setItem(11, itemGitHub);

        // Instagram
        ItemStack itemInstagram = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta metaInstagram = (SkullMeta) itemInstagram.getItemMeta();

        GameProfile profileInstagram = new GameProfile(UUID.randomUUID(), null);
        profileInstagram.getProperties()
                .put("textures", new Property("textures", plugin.getConfig().getString("socialGUI.Instagram.skullID")));

        try {
            Field field = metaInstagram.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(metaInstagram, profileInstagram);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        itemInstagram.setType(Material.PLAYER_HEAD);
        metaInstagram.displayName(Component.text("Instagram")
                .color(TextColor.fromHexString("#E1306C"))
                .decorate(TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));
        itemInstagram.setItemMeta(metaInstagram);
        inv.setItem(12, itemInstagram);

        // Twitch
        ItemStack itemTwitch = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta metaTwitch = (SkullMeta) itemTwitch.getItemMeta();

        GameProfile profileTwitch = new GameProfile(UUID.randomUUID(), null);
        profileTwitch.getProperties()
                .put("textures", new Property("textures", plugin.getConfig().getString("socialGUI.Twitch.skullID")));

        try {
            Field field = metaTwitch.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(metaTwitch, profileTwitch);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        itemTwitch.setType(Material.PLAYER_HEAD);
        metaTwitch.displayName(Component.text("Twitch")
                .color(TextColor.fromHexString("#6441a5"))
                .decorate(TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));
        itemTwitch.setItemMeta(metaTwitch);
        inv.setItem(14, itemTwitch);

        // Twitter
        ItemStack itemTwitter = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta metaTwitter = (SkullMeta) itemTwitter.getItemMeta();

        GameProfile profileTwitter = new GameProfile(UUID.randomUUID(), null);
        profileTwitter.getProperties()
                .put("textures", new Property("textures", plugin.getConfig().getString("socialGUI.Twitter.skullID")));

        try {
            Field field = metaTwitter.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(metaTwitter, profileTwitter);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        itemTwitter.setType(Material.PLAYER_HEAD);
        metaTwitter.displayName(Component.text("Twitter")
                .color(TextColor.fromHexString("#1DA1F2"))
                .decorate(TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));
        itemTwitter.setItemMeta(metaTwitter);
        inv.setItem(15, itemTwitter);

        // YouTube
        ItemStack itemYouTube = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta metaYouTube = (SkullMeta) itemYouTube.getItemMeta();

        GameProfile profileYouTube = new GameProfile(UUID.randomUUID(), null);
        profileYouTube.getProperties()
                .put("textures", new Property("textures", plugin.getConfig().getString("socialGUI.YouTube.skullID")));

        try {
            Field field = metaYouTube.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(metaYouTube, profileYouTube);
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        itemYouTube.setType(Material.PLAYER_HEAD);
        metaYouTube.displayName(Component.text("YouTube")
                .color(TextColor.fromHexString("#f2182f"))
                .decorate(TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));
        itemYouTube.setItemMeta(metaYouTube);
        inv.setItem(16, itemYouTube);

    }
}