package net.silverstonemc.silverstoneminigames.commands;

import dev.triumphteam.gui.layout.GuiLayout;
import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.item.ItemBuilder;
import dev.triumphteam.gui.slot.Slot;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HowToPlay implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Sorry, but only players can do that.", NamedTextColor.RED));
            return true;
        }

        inventory().open(player);
        return true;
    }

    private Gui inventory() {
        final int rows = 4;

        return Gui.of(rows).title(Component.text(
            "Select a Minigame",
            NamedTextColor.DARK_GRAY,
            TextDecoration.BOLD)).statelessComponent(container -> {
            container.fill(
                GuiLayout.box(Slot.min(1), Slot.max(rows)),
                ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());

            container.setItem(
                10,
                ItemBuilder.from(Material.SPRUCE_BOAT).name(getItemName("Boat Racing"))
                    .asGuiItem((player, context) -> openBook(
                        player, "Boat Racing", """
                            Race your boat to reach the finish line before the other players. 1 of the 4 available maps will be randomly selected.
                            
                            <b>The goal:</b> Get to the finish line first.""")));

            container.setItem(
                11,
                ItemBuilder.from(Material.SKELETON_SKULL).name(getItemName("Corrupted Tag"))
                    .asGuiItem((player, context) -> openBook(
                        player,
                        "Corrupted Tag",
                        """
                            A random player is selected to be the hunter. Their job is to try and kill another player to transfer the corruption to them.""",
                        """
                            If a player becomes fully corrupted, the corruption will be transferred to the nearest player. The game will end when only one person remains.""",
                        """
                            <b>The goal (runners):</b> Avoid getting killed by the hunter. Kill them if you must.""",
                        """
                            <b>The goal (hunter):</b> Kill any player to stop your corruption.""")));

            container.setItem(
                12,
                ItemBuilder.from(Material.NETHERITE_BOOTS).name(getItemName("Death Run"))
                    .flags(ItemFlag.values()).asGuiItem((player, context) -> openBook(
                        player,
                        "Death Run",
                        """
                            A random person is selected to choose a map. Once the map is chosen, another random person is selected to be the killer.""",
                        """
                            <b>The goal (runners):</b> Race to be the first person to reach the finish line. Be wary of the killer as they have multiple ways to slow you down.""",
                        """
                            <b>The goal (killer):</b> Slow everyone down as much as possible. Killing players will give you a temporary speed boost. If the timer finishes before anyone makes it to the end, you win!""")));

            container.setItem(
                13,
                ItemBuilder.from(Material.ELYTRA).name(getItemName("Flying Course"))
                    .asGuiItem((player, context) -> openBook(
                        player, "Flying Course", """
                            Fly through the rings in the correct order without touching anything. The arrows on the ground are boost pads, and the arrows on the rings point to the next ring.
                            
                            <b>The goal:</b> Reach the finish area.""")));

            container.setItem(
                14,
                ItemBuilder.from(Material.SPYGLASS).name(getItemName("Hide & Seek"))
                    .asGuiItem((player, context) -> openBook(
                        player,
                        "Hide & Seek",
                        """
                            A random player is selected to be the seeker. An additional seeker will be selected if there are 5 or more players, and a third seeker will be chosen if there are 7 or more players.""",
                        """
                            Each hider will be disguised as one of seven random blocks. The possible disguises are Grass Block, Stone, Dark Prismarine, Spruce Leaves, Oak Leaves, Poppy, and Cornflower, in no particular order.""",
                        """
                            The seeker's job is to try and kill all the disguised hiders within 10 minutes. Hints will be given out whenever the timer in your hotbar reaches 0.""",
                        """
                            The amount of remaining hiders correspond to the block(s) displayed in your hotbar.
                            Hiders may use taunts to play sounds and effects at their location once every 15 seconds. Each taunt will provide a different number of Taunt Points.""",
                        """
                            <b>The goal (hiders):</b> Avoid getting caught and killed by the seeker(s). If there are hiders remaining when the 10 minutes are up, the hiders will win.""",
                        """
                            <b>The goal (seekers):</b> Kill every hider. If there are no more hiders before the 10 minutes are up, the seekers will win.""")));

            container.setItem(
                15,
                ItemBuilder.from(Material.COMPASS).name(getItemName("Mazes"))
                    .asGuiItem((player, context) -> openBook(
                        player, "Mazes", """
                            A random maze that has no players in it will be assigned to a random player in the maze lobby. There are 8 different mazes.
                            
                            <b>The goal:</b> Find the golden pressure plate at the end of the maze.""")));

            container.setItem(
                16,
                ItemBuilder.from(Material.SNOWBALL).name(getItemName("Mini Golf"))
                    .asGuiItem((player, context) -> openBook(
                        player,
                        "Mini Golf",
                        """
                            Select your ball.
                            
                            Stand on the <b>black</b> carpet and press <b><key:key.drop></b> to throw the ball towards the hole or the next checkpoint.
                            Checkpoints are <b><gray>gray</gray></b> carpet.""",
                        """
                            If the ball misses and stops on any <b><dark_green>green</dark_green></b> or <b><dark_red>red</dark_red></b> carpet, retrieve the ball and shoot again from the latest checkpoint. Once the ball makes it in the hole, move on to the next level.""",
                        """
                            <b>The goal:</b> Complete the final hole and finish the game in as few strokes as possible.""")));

            container.setItem(
                19,
                ItemBuilder.from(Material.SLIME_BLOCK).name(getItemName("Parkour"))
                    .asGuiItem((player, context) -> openBook(
                        player, "Parkour", """
                            <b>The goal:</b> Get to the end without rage quitting.""")));

            container.setItem(
                21,
                ItemBuilder.from(Material.DIAMOND_SWORD).name(getItemName("PvP")).flags(ItemFlag.values())
                    .asGuiItem((player, context) -> openBook(
                        player,
                        "PvP",
                        """
                            
                            <b>1v1</b>
                            Kill the other player with your fist!
                            
                            <b>The goal:</b> Be the last person standing.""",
                        """
                            
                            <b>FFA</b>
                            Kill the other players with your fist! Killing others will result in you being healed.
                            
                            <b>The goal:</b> Don't die.""",
                        """
                            
                            <b>Sumo</b>
                            Hit the other player off the edge of the map. If nobody has died after 1 minute, you will get Knockback Sticks (Knockback I).""",
                        """
                            
                            <b>Sumo (Continued)</b>
                            If everyone is still alive after an additional 1 minute, you will get Knockback Rods (Knockback II).
                            
                            <b>The goal:</b> Be the last person standing.""")));

            container.setItem(
                23,
                ItemBuilder.from(Material.NETHERITE_SHOVEL).name(getItemName("Spleef"))
                    .flags(ItemFlag.values()).asGuiItem((player, context) -> openBook(
                        player,
                        "Spleef",
                        """
                            When the game begins, you will be given a shovel, bow, and some arrows. The the shovel has Efficiency V, the bow has Punch I, and the arrows have Knockback I. You will receive 9 arrows per layer, with 1 layer for each person (max 5 layers).""",
                        """
                            You can override this by choosing <b>FORCE 5 LAYERS</b> instead of <b>START DEFAULT</b>.
                            
                            <b>The goal:</b> Get the other players out by making them fall into the water at the bottom. The last person standing is the winner.""")));

            container.setItem(
                25,
                ItemBuilder.from(Material.TNT).name(getItemName("TNT Run"))
                    .asGuiItem((player, context) -> openBook(
                        player, "TNT Run", """
                            You will start with 5 double jumps. Click with the feather in hand to consume a double jump.
                            
                            <b>The goal:</b> Get the other players out by making them drop through the bottom layer.""")));
        }).build();
    }

    private TextComponent getItemName(String name) {
        Map<TextDecoration, TextDecoration.State> noItalic = Map.of(
            TextDecoration.ITALIC,
            TextDecoration.State.FALSE);

        return Component.text(name, NamedTextColor.AQUA, TextDecoration.BOLD).decorations(noItalic);
    }

    private void openBook(Player player, String title, String... pages) {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.UI, 1, 1);

        Component newTitle = Component.text(title);
        Component author = Component.text("JasonHorkles");
        List<Component> newPages = new ArrayList<>();
        newPages.add(Component.text().append(Component.text(title + "\n").decorate(TextDecoration.BOLD))
            .append(MiniMessage.miniMessage().deserialize(pages[0])).build());

        if (pages.length > 1) for (int i = 1; i < pages.length; i++)
            newPages.add(MiniMessage.miniMessage().deserialize("<b>" + title + "</b>\n" + pages[i]));

        player.openBook(Book.book(newTitle, author, newPages));
    }
}
