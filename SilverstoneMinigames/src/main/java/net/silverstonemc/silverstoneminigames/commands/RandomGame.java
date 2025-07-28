package net.silverstonemc.silverstoneminigames.commands;

import dev.triumphteam.gui.paper.Gui;
import dev.triumphteam.gui.paper.builder.item.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silverstonemc.silverstoneminigames.SilverstoneMinigames;
import net.silverstonemc.silverstoneminigames.managers.MinigameManager;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class RandomGame implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Sorry, but only players can do that.", NamedTextColor.RED));
            return true;
        }

        inventory(player).open(player);
        return true;
    }

    private Gui inventory(Player closedPlayer) {
        final int rows = 1;

        return Gui.of(rows).title(Component.text(
            "Select Player Count",
            NamedTextColor.DARK_GRAY,
            TextDecoration.BOLD)).statelessComponent(container -> {

            container.setItem(
                3, ItemBuilder.skull().texture(
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDJhNmYwZTg0ZGFlZmM4YjIxYWE5OTQxNWIxNmVkNWZkYWE2ZDhkYzBjM2NkNTkxZjQ5Y2E4MzJiNTc1In19fQ==")
                    .name(Component.text("1 Player", NamedTextColor.GRAY, TextDecoration.BOLD)
                        .decoration(TextDecoration.ITALIC, false)).asGuiItem((player, context) -> {

                        player.sendMessage(Component.text("You should play ", NamedTextColor.GREEN)
                            .append(Component.text(
                                getRandomGame(MinigameManager.MinigamePlayers.SINGLE),
                                NamedTextColor.AQUA)));

                        context.guiView().close();
                    }));

            container.setItem(
                5, ItemBuilder.skull().texture(
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWEyZDg5MWM2YWU5ZjZiYWEwNDBkNzM2YWI4NGQ0ODM0NGJiNmI3MGQ3ZjFhMjgwZGQxMmNiYWM0ZDc3NyJ9fX0=")
                    .name(Component.text("2+ Players", NamedTextColor.GRAY, TextDecoration.BOLD)
                        .decoration(TextDecoration.ITALIC, false)).asGuiItem((player, context) -> {

                        player.sendMessage(Component.text("You should play ", NamedTextColor.GREEN)
                            .append(Component.text(
                                getRandomGame(MinigameManager.MinigamePlayers.MULTI),
                                NamedTextColor.AQUA)));

                        context.guiView().close();
                    }));

        }).onClose(() -> closedPlayer.playSound(
            closedPlayer.getLocation(),
            Sound.UI_BUTTON_CLICK,
            SoundCategory.UI,
            1,
            1)).build();
    }

    @SuppressWarnings("DataFlowIssue")
    private String getRandomGame(MinigameManager.MinigamePlayers playerCount) {
        FileConfiguration config = SilverstoneMinigames.data.getConfig();
        Set<String> minigames = config.getConfigurationSection("minigame-data").getKeys(false);
        ArrayList<String> games = new ArrayList<>();

        for (String minigame : minigames) {
            String path = "minigame-data." + minigame;
            String status = config.getString(path + ".status");

            if (config.getString(path + ".players").equalsIgnoreCase(playerCount.name()) && !status.equals(
                "CLOSED")) games.add(config.getString(path + ".friendly-name"));
        }

        if (games.isEmpty()) return "[No " + playerCount.name().toLowerCase() + "player games available]";

        Random r = new Random();
        return games.get(r.nextInt(games.size()));
    }
}
