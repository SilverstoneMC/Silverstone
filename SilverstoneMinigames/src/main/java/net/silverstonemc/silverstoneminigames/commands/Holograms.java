package net.silverstonemc.silverstoneminigames.commands;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.api.hologram.HologramType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Holograms implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        Optional<Hologram> optionalHologram = FancyHologramsPlugin.get().getHologramManager()
            .getHologram(args[0]);
        if (optionalHologram.isEmpty()) {
            sender.sendMessage(Component.text("Hologram not found!", NamedTextColor.RED));
            return true;
        }

        HologramData data = optionalHologram.get().getData();
        if (data.getType() != HologramType.TEXT) {
            sender.sendMessage(Component.text("Hologram not text!", NamedTextColor.RED));
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
        TextHologramData textData = (TextHologramData) data;
        List<String> holoText = new ArrayList<>(textData.getText());
        holoText.set(holoText.size() - 1, text);
        textData.setText(holoText);
        hologram.queueUpdate();
        hologram.refreshHologram(Bukkit.getOnlinePlayers());
        return true;
    }
}
