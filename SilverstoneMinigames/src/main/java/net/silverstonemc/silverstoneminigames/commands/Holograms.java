package net.silverstonemc.silverstoneminigames.commands;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.Hologram;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;

public class Holograms implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        Optional<Hologram> optionalHologram = FancyHologramsPlugin.get().getHologramManager()
            .getHologram(args[0]);
        if (optionalHologram.isEmpty()) {
            sender.sendMessage(Component.text("Hologram not found!", NamedTextColor.RED));
            return true;
        }

        String text = null;
        switch (args[1]) {
            case "open" -> text = "<green><bold>Open";
            case "ready" -> text = "<green><bold>Ready";
            case "in_session" -> text = "<yellow><bold>In Session";
            case "resetting" -> text = "<gold><bold>Resetting...";
            case "closed" -> text = "<red><bold>Closed";
        }
        if (text == null) {
            sender.sendMessage(Component.text("Invalid game state!", NamedTextColor.RED));
            return true;
        }

        Hologram hologram = optionalHologram.get();
        ArrayList<String> holoText = new ArrayList<>(hologram.getData().getText());
        holoText.set(holoText.size() - 1, text);
        hologram.getData().setText(holoText);
        hologram.updateHologram();
        hologram.refreshHologram(Bukkit.getOnlinePlayers());
        return true;
    }
}
