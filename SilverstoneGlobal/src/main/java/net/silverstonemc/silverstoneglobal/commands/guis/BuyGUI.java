package net.silverstonemc.silverstoneglobal.commands.guis;

import dev.triumphteam.gui.click.ClickContext;
import dev.triumphteam.gui.layout.GuiLayout;
import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.item.ItemBuilder;
import dev.triumphteam.gui.slot.Slot;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BuyGUI implements CommandExecutor, Listener {
    public BuyGUI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;

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
        final int rows = 3;

        return Gui.of(rows).title(Component.text(
            "Available Ranks",
            TextColor.fromHexString("#048a3e"),
            TextDecoration.BOLD)).statelessComponent(container -> {
            container.fill(
                GuiLayout.box(Slot.min(1), Slot.max(rows)),
                ItemBuilder.from(Material.BLACK_STAINED_GLASS_PANE).name(Component.empty()).asGuiItem());

            container.setItem(
                11,
                ItemBuilder.from(Material.IRON_INGOT).name(getItemName(Rank.MEMBER))
                    .lore(getItemLore(Rank.MEMBER)).asGuiItem((player, context) -> sendMessage(
                        player,
                        "Member",
                        "https://silverstone.craftingstore.net/category/261250",
                        context)));

            container.setItem(
                12,
                ItemBuilder.from(Material.EMERALD).name(getItemName(Rank.VIP)).lore(getItemLore(Rank.VIP))
                    .asGuiItem((player, context) -> sendMessage(
                        player,
                        "VIP",
                        "https://silverstone.craftingstore.net/category/261250",
                        context)));

            container.setItem(
                13,
                ItemBuilder.from(Material.DIAMOND).name(getItemName(Rank.VIP_PLUS))
                    .lore(getItemLore(Rank.VIP_PLUS)).asGuiItem((player, context) -> sendMessage(
                        player,
                        "VIP+",
                        "https://silverstone.craftingstore.net/category/261250",
                        context)));

            container.setItem(
                14,
                ItemBuilder.from(Material.NETHERITE_INGOT).name(getItemName(Rank.MVP))
                    .lore(getItemLore(Rank.MVP)).asGuiItem((player, context) -> sendMessage(
                        player,
                        "MVP",
                        "https://silverstone.craftingstore.net/category/261250",
                        context)));

            container.setItem(
                15,
                ItemBuilder.from(Material.WRITABLE_BOOK).name(getItemName(Rank.DONATE))
                    .lore(getItemLore(Rank.DONATE)).asGuiItem((player, context) -> sendMessage(
                        player,
                        "Donate",
                        "https://silverstone.craftingstore.net/category/261655",
                        context)));
        }).build();
    }

    private enum Rank {
        MEMBER("Member"),
        VIP("VIP"),
        VIP_PLUS("VIP+"),
        MVP("MVP"),
        DONATE("Donate");

        private final String name;

        Rank(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private TextComponent getItemName(Rank rank) {
        NamedTextColor color = switch (rank) {
            case VIP -> NamedTextColor.GREEN;
            case VIP_PLUS -> NamedTextColor.GOLD;
            case MVP -> NamedTextColor.LIGHT_PURPLE;
            default -> NamedTextColor.DARK_GREEN;
        };

        return Component.text(rank.getName(), color, TextDecoration.BOLD)
            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    private List<Component> getItemLore(Rank rank) {
        return plugin.getConfig().getStringList("buy-gui." + rank.getName().toLowerCase()).stream().map(
            customLore -> MiniMessage.miniMessage().deserialize("<!i>" + customLore)).toList();
    }

    private void sendMessage(Player player, String rank, String url, ClickContext context) {
        player.sendMessage(Component.text("\nPurchase the ", NamedTextColor.GREEN).append(
            Component.text(rank + " ", NamedTextColor.AQUA),
            Component.text("rank ", NamedTextColor.GREEN),
            Component.text("here", NamedTextColor.AQUA).clickEvent(ClickEvent.openUrl(url))));

        context.guiView().close();
    }
}