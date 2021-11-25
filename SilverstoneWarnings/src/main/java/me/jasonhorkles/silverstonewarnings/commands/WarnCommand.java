package me.jasonhorkles.silverstonewarnings.commands;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Emoji;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.Button;
import me.jasonhorkles.silverstonewarnings.SilverstoneWarnings;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WarnCommand implements CommandExecutor, Listener {

    private static JavaPlugin plugin;

    public WarnCommand(JavaPlugin plugin) {
        WarnCommand.plugin = plugin;
    }

    private static Inventory mainInv;
    private static Inventory hackInv;
    private static Inventory chatInv;
    private static Inventory otherInv;
    private static final Map<Player, String> target = new HashMap<>();

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

    public void openInv(Player player, Inventory inv) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                player.openInventory(inv);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            }
        };
        task.runTask(plugin);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        // If console does the command
        if (!(sender instanceof Player)) switch (args.length) {
            case 0:
            case 1:
                return false;

            case 2: // Warn the targeted player
                if (checkReason(args[1])) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "reasons");
                    return true;
                }

                OfflinePlayer offlinePlayer = SilverstoneWarnings.getOfflinePlayer(sender, args[0]);
                if (offlinePlayer == null) return true;
                UUID uuid = offlinePlayer.getUniqueId();

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cWarning &7" + offlinePlayer.getName() + " &cfor reason: &7" + args[1]));
                warn(uuid, args[1]);
        }
        else switch (args.length) {
            case 0:
                return false;

            case 1: // Open GUI if no warning specified
                Player player = (Player) sender;

                OfflinePlayer offlinePlayer = SilverstoneWarnings.getOfflinePlayer(sender, args[0]);
                if (offlinePlayer == null) return true;

                target.put(player, offlinePlayer.getName());
                player.openInventory(mainInv);
                break;

            case 2: // Warn the targeted player
                if (checkReason(args[1])) {
                    player = (Player) sender;
                    player.performCommand("warn " + args[0]);
                    return true;
                }

                offlinePlayer = SilverstoneWarnings.getOfflinePlayer(sender, args[0]);
                if (offlinePlayer == null) return true;
                UUID uuid = offlinePlayer.getUniqueId();

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cWarning &7" + offlinePlayer.getName() + " &cfor reason: &7" + args[1]));
                warn(uuid, args[1]);
        }
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    public static void warn(UUID uuid, String reason) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        TextChannel discord = null;
        if (SilverstoneWarnings.discordEnabled)
            discord = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("warnings");

        // If player is not online, queue the warning
        if (!player.isOnline()) {
            SilverstoneWarnings.queue.getConfig().set("queue." + uuid, reason);
            SilverstoneWarnings.queue.saveConfig();
            for (Player online : Bukkit.getOnlinePlayers())
                if (online.hasPermission("sswarnings.warn"))
                    online.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cOffline player &7" + player.getName() + " &chas been warned for reason: &7" + reason));
            plugin.getLogger()
                    .info(ChatColor.translateAlternateColorCodes('&', "&cOffline player &7" + player.getName() + " &chas been warned for reason: &7" + reason));

            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor(player.getName() + " has a warning queued: " + reason, null, "https://crafatar.com/avatars/" + player
                    .getUniqueId() + "?overlay=true");
            embed.setColor(new Color(255, 217, 0));
            if (SilverstoneWarnings.discordEnabled) discord.sendMessageEmbeds(embed.build()).queue();
            return;
        }

        // If player doesn't have the reason in their data, add it
        if (!SilverstoneWarnings.data.getConfig().contains("data." + uuid + "." + reason))
            SilverstoneWarnings.data.getConfig().set("data." + uuid + "." + reason, 1);
        else { // If they already had the reason, add 1 to it
            int count = SilverstoneWarnings.data.getConfig().getInt("data." + uuid + "." + reason);
            SilverstoneWarnings.data.getConfig().set("data." + uuid + "." + reason, count + 1);
        }
        SilverstoneWarnings.data.saveConfig();

        // Grab the number of times the player has been warned
        int warningCount = SilverstoneWarnings.data.getConfig().getInt("data." + uuid + "." + reason);
        // Grab the amount of punishments in the config
        int punishmentCount = plugin.getConfig()
                .getConfigurationSection("reasons." + reason + ".add")
                .getKeys(false)
                .toArray().length;
        // Get the correct warning number
        int punishmentNumber = warningCount % punishmentCount;
        if (punishmentNumber == 0) punishmentNumber = punishmentCount;

        if (SilverstoneWarnings.discordEnabled) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor(player.getName() + " was warned for reason: " + reason + " | Warning " + punishmentNumber + "/" + punishmentCount, null, "https://crafatar.com/avatars/" + player
                    .getUniqueId() + "?overlay=true");
            embed.setColor(new Color(204, 27, 53));
            discord.sendMessageEmbeds(embed.build())
                    .setActionRow(
                            Button.success("removewarning: " + reason + " :" + player.getUniqueId(), "Undo '" + reason + "' warning")
                                    .withEmoji(Emoji.fromUnicode("↩")),
                            Button.primary("removewarningsilently: " + reason + " :" + player.getUniqueId(), "Undo '" + reason + "' warning silently")
                                    .withEmoji(Emoji.fromUnicode("↩")),
                            Button.secondary("clearwarning: " + reason + " :" + player.getUniqueId(), "Clear all '" + reason + "' warnings")
                                    .withEmoji(Emoji.fromUnicode("➖")),
                            Button.danger("clearallwarnings:" + player.getUniqueId(), "Clear all of " + player.getName() + "'s warnings")
                                    .withEmoji(Emoji.fromUnicode("❌")))
                    .queue();
        }

        // Warn the player
        ArrayList<String> cmdList = new ArrayList<>(plugin.getConfig()
                .getStringList("reasons." + reason + ".add." + punishmentNumber));
        for (String s : cmdList)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("{player}", player.getName()));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(mainInv) && !event.getInventory().equals(hackInv) && !event.getInventory()
                .equals(chatInv) && !event.getInventory().equals(otherInv))
            return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        if (event.getInventory().equals(mainInv)) switch (event.getRawSlot()) {
            case 2 -> openInv(player, hackInv);
            case 4 -> openInv(player, chatInv);
            case 6 -> openInv(player, otherInv);
        }
        else if (event.getInventory().equals(hackInv)) switch (event.getRawSlot()) {
            case 10 -> {
                closeInv(player);
                player.performCommand("warn " + target.get(player) + " block");
            }
            case 12 -> {
                closeInv(player);
                player.performCommand("warn " + target.get(player) + " hitbox");
            }
            case 14 -> {
                closeInv(player);
                player.performCommand("warn " + target.get(player) + " interact");
            }
            case 16 -> {
                closeInv(player);
                player.performCommand("warn " + target.get(player) + " jesus");
            }
            case 20 -> {
                closeInv(player);
                player.performCommand("warn " + target.get(player) + " killaura");
            }
            case 22 -> {
                closeInv(player);
                player.performCommand("warn " + target.get(player) + " move");
            }
            case 24 -> {
                closeInv(player);
                player.performCommand("warn " + target.get(player) + " scaffold");
            }
            case 28 -> {
                closeInv(player);
                player.performCommand("warn " + target.get(player) + " vehicle");
            }
            case 30 -> {
                closeInv(player);
                player.performCommand("warn " + target.get(player) + " velocity");
            }
            case 32 -> {
                closeInv(player);
                player.performCommand("warn " + target.get(player) + " xray");
            }
            case 34 -> {
                closeInv(player);
                player.performCommand("warn " + target.get(player) + " malicioushacks");
            }
            case 36 -> openInv(player, mainInv);
        }
        else if (event.getInventory().equals(chatInv)) switch (event.getRawSlot()) {
            case 11 -> {
                closeInv(player);
                player.performCommand("warn " + target.get(player) + " chat");
            }
            case 15 -> {
                closeInv(player);
                player.performCommand("warn " + target.get(player) + " nickname");
            }
            case 18 -> openInv(player, mainInv);
        }
        else if (event.getInventory().equals(otherInv)) switch (event.getRawSlot()) {
            case 11 -> {
                closeInv(player);
                player.performCommand("warn " + target.get(player) + " afk");
            }
            case 13 -> {
                closeInv(player);
                player.performCommand("warn " + target.get(player) + " exploits");
            }
            case 15 -> {
                closeInv(player);
                player.performCommand("warn " + target.get(player) + " griefing");
            }
            case 19 -> {
                closeInv(player);
                player.performCommand("warn " + target.get(player) + " skin");
            }
            case 21 -> {
                closeInv(player);
                player.performCommand("warn " + target.get(player) + " joinleavespam");
            }
            case 23 -> {
                closeInv(player);
                player.performCommand("warn " + target.get(player) + " lag");
            }
            case 25 -> {
                closeInv(player);
                player.performCommand("warn " + target.get(player) + " plot");
            }
            case 31 -> {
                closeInv(player);
                player.performCommand("warn " + target.get(player) + " other");
            }
            case 49 -> {
                closeInv(player);
                player.performCommand("warn " + target.get(player) + " punishevasion");
            }
            case 53 -> {
                closeInv(player);
                player.performCommand("warn " + target.get(player) + " banevasion");
            }
            case 45 -> openInv(player, mainInv);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private boolean checkReason(String reason) {
        ArrayList<String> reasonList = new ArrayList<>(plugin.getConfig()
                .getConfigurationSection("reasons")
                .getKeys(false));
        return !reasonList.contains(reason);
    }

    // Inventory items
    public static void createMainInv() {

        mainInv = Bukkit.createInventory(null, 9, Component.text(ChatColor.translateAlternateColorCodes('&', "&4&lSelect a Category")));

        ItemStack item = new ItemStack(Material.NETHERITE_BLOCK);
        ItemMeta meta = item.getItemMeta();

        // Hacking
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Hacking"));
        item.setItemMeta(meta);
        mainInv.setItem(2, item);

        // Chat
        item.setType(Material.BIRCH_SIGN);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Chat"));
        item.setItemMeta(meta);
        mainInv.setItem(4, item);

        // Other
        item.setType(Material.RED_CONCRETE);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Misc"));
        item.setItemMeta(meta);
        mainInv.setItem(6, item);
    }

    public static void createHackInv() {
        hackInv = Bukkit.createInventory(null, 45, Component.text(ChatColor.translateAlternateColorCodes('&', "&4&lSelect a Reason &8| &4&lHacking")));

        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        // Fill items
        meta.displayName(Component.text(ChatColor.BOLD + ""));
        List<Component> lore = new ArrayList<>();
        meta.lore(lore);
        item.setItemMeta(meta);
        IntStream.rangeClosed(0, 44).boxed().collect(Collectors.toList()).forEach(slot -> hackInv.setItem(slot, item));

        // Block
        item.setType(Material.NETHERITE_PICKAXE);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Block"));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7FastBreak")));
        meta.lore(lore);
        item.setItemMeta(meta);
        hackInv.setItem(10, item);

        // Hitbox / Reach
        item.setType(Material.FISHING_ROD);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Hitbox"));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Reach")));
        meta.lore(lore);
        item.setItemMeta(meta);
        hackInv.setItem(12, item);

        // Interact
        item.setType(Material.LEVER);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Interact"));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Illegal interactions with blocks/entities")));
        meta.lore(lore);
        item.setItemMeta(meta);
        hackInv.setItem(14, item);

        // Jesus
        item.setType(Material.WATER_BUCKET);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Jesus"));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Jesus hack or related")));
        meta.lore(lore);
        item.setItemMeta(meta);
        hackInv.setItem(16, item);

        // KillAura
        item.setType(Material.DIAMOND_SWORD);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "KillAura"));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7KillAura or related")));
        meta.lore(lore);
        item.setItemMeta(meta);
        hackInv.setItem(20, item);

        // Move
        item.setType(Material.NETHERITE_BOOTS);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Move"));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Speed / Fly / any other abnormal movement")));
        meta.lore(lore);
        item.setItemMeta(meta);
        hackInv.setItem(22, item);

        // Scaffold
        item.setType(Material.SCAFFOLDING);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Scaffold"));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Scaffold / Bridge or related")));
        meta.lore(lore);
        item.setItemMeta(meta);
        hackInv.setItem(24, item);

        // Vehicle
        item.setType(Material.DARK_OAK_BOAT);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Vehicle"));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Vehicle Speed / Flight")));
        meta.lore(lore);
        item.setItemMeta(meta);
        hackInv.setItem(28, item);

        // Velocity
        item.setType(Material.SHIELD);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Velocity"));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Velocity / Anti-Knockback")));
        meta.lore(lore);
        item.setItemMeta(meta);
        hackInv.setItem(30, item);

        // X-Ray
        item.setType(Material.JACK_O_LANTERN);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "X-Ray"));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7X-Ray hack / resource pack")));
        meta.lore(lore);
        item.setItemMeta(meta);
        hackInv.setItem(32, item);

        // Malicious
        item.setType(Material.PAPER);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Malicious Hacks"));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Any other hacks that aren't listed")));
        meta.lore(lore);
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
        hackInv.setItem(34, item);

        // Back
        item.setType(Material.ARROW);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Back"));
        lore.clear();
        meta.lore(lore);
        item.setItemMeta(meta);
        hackInv.setItem(36, item);
    }

    public static void createChatInv() {

        chatInv = Bukkit.createInventory(null, 27, Component.text(ChatColor.translateAlternateColorCodes('&', "&4&lSelect a Reason &8| &4&lChat")));

        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        // Fill items
        meta.displayName(Component.text(ChatColor.BOLD + ""));
        List<Component> lore = new ArrayList<>();
        meta.lore(lore);
        item.setItemMeta(meta);
        IntStream.rangeClosed(0, 26).boxed().collect(Collectors.toList()).forEach(slot -> chatInv.setItem(slot, item));

        // Chat Abuse
        item.setType(Material.OAK_SIGN);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Chat Abuse"));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Chat spam, bypassing swear filter, etc")));
        meta.lore(lore);
        item.setItemMeta(meta);
        chatInv.setItem(11, item);

        // Nickname
        item.setType(Material.WRITABLE_BOOK);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Nickname"));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Inappropriate nickname or is")));
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7trying to impersonate someone.")));
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Check if they have a nickname")));
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7with &3/realname <user>&7.")));
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Only member rank and higher")));
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7can set nicknames")));
        meta.lore(lore);
        item.setItemMeta(meta);
        chatInv.setItem(15, item);


        // Back
        item.setType(Material.ARROW);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Back"));
        lore.clear();
        meta.lore(lore);
        item.setItemMeta(meta);
        chatInv.setItem(18, item);
    }

    public static void createOtherInv() {

        otherInv = Bukkit.createInventory(null, 54, Component.text(ChatColor.translateAlternateColorCodes('&', "&4&lSelect a Reason &8| &4&lMisc")));

        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        // Fill items
        meta.displayName(Component.text(ChatColor.BOLD + ""));
        List<Component> lore = new ArrayList<>();
        meta.lore(lore);
        item.setItemMeta(meta);
        IntStream.rangeClosed(0, 53).boxed().collect(Collectors.toList()).forEach(slot -> otherInv.setItem(slot, item));

        // Anti-AFK Machine
        item.setType(Material.CLOCK);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Anti-AFK Machine"));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7If a player is using")));
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7an anti-AFK machine or macro")));
        meta.lore(lore);
        item.setItemMeta(meta);
        otherInv.setItem(11, item);

        // Exploits / Cheating
        item.setType(Material.EMERALD);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Exploits / Cheating"));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Any server exploits or")));
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7cheating in some way")));
        meta.lore(lore);
        item.setItemMeta(meta);
        otherInv.setItem(13, item);

        // Griefing
        item.setType(Material.TNT);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Griefing"));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Griefing, stealing inventories, etc")));
        meta.lore(lore);
        item.setItemMeta(meta);
        otherInv.setItem(15, item);

        // Inappropriate Skin
        item.setType(Material.DIORITE);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Inappropriate Skin"));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Jails the player for their skin.")));
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7If they don't change it when the jail")));
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7time expires, continue to warn them")));
        meta.lore(lore);
        item.setItemMeta(meta);
        otherInv.setItem(19, item);

        // Join / Leave Spam
        item.setType(Material.MAGENTA_GLAZED_TERRACOTTA);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Join / Leave Spam"));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Use if the player is repeatedly")));
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7joining and leaving the")));
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7server quickly.")));
        meta.lore(lore);
        item.setItemMeta(meta);
        otherInv.setItem(21, item);

        // Lag Machine
        item.setType(Material.PISTON);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Lag Machine"));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Attempting to make the server")));
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7or clients experience lag")));
        meta.lore(lore);
        item.setItemMeta(meta);
        otherInv.setItem(23, item);

        // Plot Abuse
        item.setType(Material.WOODEN_AXE);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Plot Abuse"));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Any violation of the plot rules.")));
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Examples: Inappropriate builds, illegal items,")));
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7trying to crash clients, etc")));
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Overrides the lag machine")));
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7reason if they're in the plots")));
        meta.lore(lore);
        item.setItemMeta(meta);
        otherInv.setItem(25, item);

        // Other
        item.setType(Material.REDSTONE_BLOCK);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Other"));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Punish for any unlisted reason")));
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7that still violates a rule")));
        meta.lore(lore);
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
        otherInv.setItem(31, item);

        // Punishment Evasion
        item.setType(Material.BARRIER);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Punishment Evasion"));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Bypassing any punishment with an alt")));
        meta.lore(lore);
        item.setItemMeta(meta);
        otherInv.setItem(49, item);

        // Permanent Ban Evasion
        item.setType(Material.BARRIER);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Ban Evasion"));
        lore.clear();
        lore.add(Component.text(ChatColor.translateAlternateColorCodes('&', "&7Bypassing a permanent ban with an alt")));
        meta.lore(lore);
        item.setItemMeta(meta);
        item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
        otherInv.setItem(53, item);

        // Back
        item.setType(Material.ARROW);
        meta.displayName(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Back"));
        lore.clear();
        meta.lore(lore);
        item.setItemMeta(meta);
        otherInv.setItem(45, item);
    }
}
