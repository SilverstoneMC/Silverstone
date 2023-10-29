package net.silverstonemc.silverstoneminigames.commands;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.silverstonemc.silverstoneminigames.CustomSkull;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class Minigames implements CommandExecutor, Listener {
    private static Inventory gameInv;
    private static Inventory htpInv;
    private final JavaPlugin plugin;

    public Minigames(JavaPlugin plugin) {
        this.plugin = plugin;
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

    public void initializeInventories() {
        gameInv = Bukkit.createInventory(null, 9,
            Component.text("Select Player Count", NamedTextColor.DARK_GRAY, TextDecoration.BOLD));
        htpInv = Bukkit.createInventory(null, 36,
            Component.text("Select a Minigame", NamedTextColor.DARK_GRAY, TextDecoration.BOLD));

        // HTP
        // Filler
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(""));
        item.setItemMeta(meta);
        IntStream.rangeClosed(0, 35).boxed().toList().forEach(slot -> htpInv.setItem(slot, item));

        htpInv.setItem(10, createHtpGuiItem(Material.SPRUCE_BOAT, "Boat Racing"));
        htpInv.setItem(11, createHtpGuiItem(Material.NETHERITE_BOOTS, "Death Run"));
        htpInv.setItem(12, createHtpGuiItem(Material.ELYTRA, "Flying Course"));
        htpInv.setItem(13, createHtpGuiItem(Material.COMPASS, "Hide & Seek"));
        htpInv.setItem(14, createHtpGuiItem(Material.JUNGLE_LEAVES, "Mazes"));
        htpInv.setItem(15, createHtpGuiItem(Material.SNOWBALL, "Mini Golf"));
        htpInv.setItem(16, createHtpGuiItem(Material.SLIME_BLOCK, "Parkour"));
        htpInv.setItem(21, createHtpGuiItem(Material.DIAMOND_SWORD, "PvP"));
        htpInv.setItem(22, createHtpGuiItem(Material.NETHERITE_SHOVEL, "Spleef"));
        htpInv.setItem(23, createHtpGuiItem(Material.TNT, "TNT Run"));

        // Game
        // 1
        ItemStack onePlayer = new CustomSkull().createCustomSkull(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDJhNmYwZTg0ZGFlZmM4YjIxYWE5OTQxNWIxNmVkNWZkYWE2ZDhkYzBjM2NkNTkxZjQ5Y2E4MzJiNTc1In19fQ==",
            "GameInv1");
        SkullMeta onePlayerMeta = (SkullMeta) onePlayer.getItemMeta();

        onePlayerMeta.displayName(Component.text("1 Player", NamedTextColor.GRAY, TextDecoration.BOLD)
            .decoration(TextDecoration.ITALIC, false));
        onePlayer.setItemMeta(onePlayerMeta);
        gameInv.setItem(3, onePlayer);

        // 2
        ItemStack twoPlusPlayers = new CustomSkull().createCustomSkull(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWEyZDg5MWM2YWU5ZjZiYWEwNDBkNzM2YWI4NGQ0ODM0NGJiNmI3MGQ3ZjFhMjgwZGQxMmNiYWM0ZDc3NyJ9fX0=",
            "GameInv2");
        SkullMeta twoPlayerMeta = (SkullMeta) twoPlusPlayers.getItemMeta();

        twoPlayerMeta.displayName(Component.text("2+ Players", NamedTextColor.GRAY, TextDecoration.BOLD)
            .decoration(TextDecoration.ITALIC, false));
        twoPlusPlayers.setItemMeta(twoPlayerMeta);
        gameInv.setItem(5, twoPlusPlayers);
    }

    protected ItemStack createHtpGuiItem(final Material material, final String itemName) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.displayName(Component.text(itemName, NamedTextColor.AQUA, TextDecoration.BOLD)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

        // Set the item flags
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(meta);

        return item;
    }

    @EventHandler
    public void onHtpInventoryClick(final InventoryClickEvent event) {
        if (!event.getInventory().equals(htpInv)) return;

        event.setCancelled(true);

        final ItemStack clickedItem = event.getCurrentItem();

        // Verify current item is not null
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        Player player = (Player) event.getWhoClicked();

        switch (event.getRawSlot()) {
            case 10 -> openBook(player, "Boat Racing", """
                Race your boat to reach the finish line before the other players. 1 of the 4 available maps will be randomly selected.
                                
                <b>The goal:</b> Get to the finish line first.""");

            case 11 -> openBook(player, "Death Run", """
                    A random person is selected to choose a map. Once the map is chosen, another random person is selected to be the killer.""",
                """
                    <b>The goal (runners):</b> Race to be the first person to reach the finish line. Be wary of the killer as they have multiple ways to slow you down.""",
                """
                    <b>The goal (killer):</b> Slow everyone down as much as possible. Killing players will give you a temporary speed boost. If the timer finishes before anyone makes it to the end, you win!""");

            case 12 -> openBook(player, "Flying Course", """
                Fly through the rings in the correct order without touching anything. The arrows on the ground are boost pads, and the arrows on the rings point to the next ring.

                <b>The goal:</b> Reach the finish area.""");

            case 13 -> openBook(player, "Hide & Seek", """
                    A random player is selected to be the seeker. An additional seeker will be selected if there are 5 or more players, and a third seeker will be chosen if there are 7 or more players.""",
                """
                    Each hider will be disguised as one of seven random blocks. The possible disguises are Grass Block, Stone, Dark Prismarine, Spruce Leaves, Oak Leaves, Poppy, and Cornflower, in no particular order.""",
                """
                    The seeker's job is to try and kill all the disguised hiders within 10 minutes. Hints will be given out whenever the timer in your hotbar reaches 0.""",
                """
                    The amount of remaining hiders correspond to the block(s) displayed in your hotbar.
                    Hiders may use taunts to play sounds and effects at their location once every 15 seconds. Each taunt will provide a different number of Taunt Points.""",
                """
                    The number of bells in a hider's hotbar corresponds to the amount of Taunt Points they currently have, with a maximum display limit of 125 (there is no internal Taunt Point limit, however).""",
                """
                    <b>The goal (hiders):</b> Avoid getting caught and killed by the seeker(s). If there are hiders remaining when the 10 minutes is up, the hiders will win.""",
                """
                    <b>The goal (seekers):</b> Kill every hider. If there are no more hiders before the 10 minutes is up, the seekers will win.""");

            case 14 -> openBook(player, "Mazes", """
                A random maze that has no players in it will be assigned to a random player in the maze lobby. There are 8 different mazes.

                <b>The goal:</b> Find the golden pressure plate at the end of the maze.""");

            case 15 -> openBook(player, "Mini Golf", """
                    Select your ball.

                    Stand on the <b>black</b> carpet and press <b><key:key.drop></b> to throw the ball towards the hole or the next checkpoint.
                    Checkpoints are <b><gray>gray</gray></b> carpet.""", """
                    If the ball misses and stops on any <b><dark_green>green</dark_green></b> or <b><dark_red>red</dark_red></b> carpet, retrieve the ball and shoot again from the latest checkpoint. Once the ball makes it in the hole, move on to the next level.""",
                """
                    <b>The goal:</b> Complete the final hole and finish the game in as few strokes as possible.""");

            case 16 -> openBook(player, "Parkour", """
                <b>The goal:</b> Get to the end without rage quitting.""");

            case 21 -> openBook(player, "PvP", """
                                        
                    <b>1v1</b>
                    Kill the other player with your fist!

                    <b>The goal:</b> Be the last person standing.""", """
                                        
                    <b>FFA</b>
                    Kill the other players with your fist! Killing others will result in you being healed.

                    <b>The goal:</b> Don't die.""", """
                                        
                    <b>Sumo</b>
                    Hit the other player off the edge of the map. If nobody has died after 1.5 minutes, you will get Knockback Sticks (Knockback I).""",
                """
                                        
                    <b>Sumo (Continued)</b>
                    If everyone is still alive after an additional 1.5 minutes, you will get Knockback Rods (Knockback II).

                    <b>The goal:</b> Be the last person standing.""");

            case 22 -> openBook(player, "Spleef", """
                    When the game begins, you will be given a shovel, bow, and some arrows. The the shovel has Efficiency V, the bow has Punch I, and the arrows have Knockback I. You will receive 9 arrows per layer, with 1 layer for each person (max 5 layers).""",
                """
                    You can override this by choosing <b>FORCE 5 LAYERS</b> instead of <b>START DEFAULT</b>.

                    <b>The goal:</b> Get the other players out by making them fall into the water at the bottom. The last person standing is the winner.""");

            case 23 -> openBook(player, "TNT Run", """
                You will start with 5 double jumps. Click with the feather in hand to consume a double jump.

                <b>The goal:</b> Get the other players out by making them drop through the bottom layer.""");
        }
    }

    private void openBook(Player player, String title, String... pages) {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);

        Component newTitle = Component.text(title);
        Component author = Component.text("JasonHorkles");
        List<Component> newPages = new ArrayList<>();
        newPages.add(Component.text().append(Component.text(title + "\n").decorate(TextDecoration.BOLD))
            .append(MiniMessage.miniMessage().deserialize(pages[0])).build());

        if (pages.length > 1) for (int i = 1; i < pages.length; i++)
            newPages.add(MiniMessage.miniMessage().deserialize("<b>" + title + "</b>\n" + pages[i]));

        player.openBook(Book.book(newTitle, author, newPages));
    }

    @EventHandler
    public void onGameInventoryClick(final InventoryClickEvent event) {
        if (!event.getInventory().equals(gameInv)) return;

        event.setCancelled(true);

        final ItemStack clickedItem = event.getCurrentItem();

        // Verify current item is not null
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        Player player = (Player) event.getWhoClicked();

        switch (event.getRawSlot()) {
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

    private void closeInv(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
            }
        }.runTask(plugin);
    }
}
