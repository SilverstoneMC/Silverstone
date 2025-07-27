package net.silverstonemc.silverstoneglobal.commands.guis;

import dev.triumphteam.gui.click.ClickContext;
import dev.triumphteam.gui.layout.GuiLayout;
import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.item.ItemBuilder;
import dev.triumphteam.gui.slot.Slot;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ChatColorGUI implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Sorry, but only players can do that.", NamedTextColor.RED));
            return true;
        }

        // Create the inventory and open it
        inventory().open(player);
        return true;
    }

    private Gui inventory() {
        final int rows = 5;

        return Gui.of(rows).title(Component.text(
            "Chat Colors",
            TextColor.fromHexString("#a62828"),
            TextDecoration.BOLD)).statelessComponent(container -> {
            container.fill(
                GuiLayout.box(Slot.min(1), Slot.max(rows)),
                ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());

            container.setItem(
                10,
                ItemBuilder.from(Material.RED_CONCRETE).name(getItemName("Dark Red", NamedTextColor.DARK_RED))
                    .asGuiItem((player, context) -> setChatColor(
                        player,
                        '4',
                        NamedTextColor.DARK_RED,
                        context)));

            container.setItem(
                11,
                ItemBuilder.from(Material.RED_TERRACOTTA).name(getItemName("Red", NamedTextColor.RED))
                    .asGuiItem((player, context) -> setChatColor(player, 'c', NamedTextColor.RED, context)));

            container.setItem(
                12,
                ItemBuilder.from(Material.ORANGE_CONCRETE).name(getItemName("Gold", NamedTextColor.GOLD))
                    .asGuiItem((player, context) -> setChatColor(player, '6', NamedTextColor.GOLD, context)));

            container.setItem(
                13,
                ItemBuilder.from(Material.YELLOW_CONCRETE).name(getItemName("Yellow", NamedTextColor.YELLOW))
                    .asGuiItem((player, context) -> setChatColor(
                        player,
                        'e',
                        NamedTextColor.YELLOW,
                        context)));

            container.setItem(
                14,
                ItemBuilder.from(Material.LIME_CONCRETE).name(getItemName("Green", NamedTextColor.GREEN))
                    .asGuiItem((player, context) -> setChatColor(
                        player,
                        'a',
                        NamedTextColor.GREEN,
                        context)));

            container.setItem(
                15,
                ItemBuilder.from(Material.GREEN_CONCRETE)
                    .name(getItemName("Dark Green", NamedTextColor.DARK_GREEN))
                    .asGuiItem((player, context) -> setChatColor(
                        player,
                        '2',
                        NamedTextColor.DARK_GREEN,
                        context)));

            container.setItem(
                16,
                ItemBuilder.from(Material.LIGHT_BLUE_TERRACOTTA)
                    .name(getItemName("Dark Aqua", NamedTextColor.DARK_AQUA))
                    .asGuiItem((player, context) -> setChatColor(
                        player,
                        '3',
                        NamedTextColor.DARK_AQUA,
                        context)));

            container.setItem(
                19,
                ItemBuilder.from(Material.BLUE_CONCRETE).name(getItemName("Blue", NamedTextColor.BLUE))
                    .asGuiItem((player, context) -> setChatColor(player, '9', NamedTextColor.BLUE, context)));

            container.setItem(
                20,
                ItemBuilder.from(Material.BLUE_TERRACOTTA)
                    .name(getItemName("Dark Blue", NamedTextColor.DARK_BLUE))
                    .asGuiItem((player, context) -> setChatColor(
                        player,
                        '1',
                        NamedTextColor.DARK_BLUE,
                        context)));

            container.setItem(
                21,
                ItemBuilder.from(Material.PURPLE_CONCRETE)
                    .name(getItemName("Dark Purple", NamedTextColor.DARK_PURPLE))
                    .asGuiItem((player, context) -> setChatColor(
                        player,
                        '5',
                        NamedTextColor.DARK_PURPLE,
                        context)));

            container.setItem(
                22,
                ItemBuilder.from(Material.MAGENTA_CONCRETE)
                    .name(getItemName("Purple", NamedTextColor.LIGHT_PURPLE))
                    .asGuiItem((player, context) -> setChatColor(
                        player,
                        'd',
                        NamedTextColor.LIGHT_PURPLE,
                        context)));

            container.setItem(
                23,
                ItemBuilder.from(Material.GRAY_TERRACOTTA)
                    .name(getItemName("Dark Gray", NamedTextColor.DARK_GRAY))
                    .asGuiItem((player, context) -> setChatColor(
                        player,
                        '8',
                        NamedTextColor.DARK_GRAY,
                        context)));

            container.setItem(
                24,
                ItemBuilder.from(Material.LIGHT_GRAY_CONCRETE).name(getItemName("Gray", NamedTextColor.GRAY))
                    .asGuiItem((player, context) -> setChatColor(player, '7', NamedTextColor.GRAY, context)));

            container.setItem(
                25,
                ItemBuilder.from(Material.WHITE_CONCRETE).name(getItemName("White", NamedTextColor.WHITE))
                    .asGuiItem((player, context) -> setChatColor(
                        player,
                        'f',
                        NamedTextColor.WHITE,
                        context)));

            container.setItem(
                31,
                ItemBuilder.from(Material.BARRIER).name(getItemName("Reset to Default", NamedTextColor.WHITE))
                    .asGuiItem((player, context) -> setChatColor(
                        player,
                        'r',
                        NamedTextColor.WHITE,
                        context)));
        }).build();
    }

    private TextComponent getItemName(String name, NamedTextColor color) {
        Map<TextDecoration, TextDecoration.State> noItalic = Map.of(
            TextDecoration.ITALIC,
            TextDecoration.State.FALSE);

        return Component.text(name, color, TextDecoration.BOLD).decorations(noItalic);
    }

    private void setChatColor(HumanEntity player, char value, NamedTextColor color, ClickContext context) {
        // Tried using the API but this just works much easier lol
        if (value == 'r') {
            Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "lp user " + player.getName() + " meta removesuffix 500");

            player.sendMessage(Component.text("Chat color reset to default."));

        } else {
            Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "lp user " + player.getName() + " meta setsuffix 500 &" + value);

            player.sendMessage(Component.text("Chat color changed.", color));
        }

        context.guiView().close();

        Player player2 = (Player) player;
        player2.playSound(player2.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.UI, 1, 1);
    }
}
