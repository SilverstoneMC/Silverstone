package net.silverstonemc.silverstoneglobal;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Whitelist {
    public static void whitelist() {
        if (Bukkit.getServer().hasWhitelist()) {
            SilverstoneGlobal.getInstance().getLogger().warning("Whitelist is on");

            Player jason = Bukkit.getPlayer(UUID.fromString("a28173af-f0a9-47fe-8549-19c6bccf68da"));
            if (jason == null) return;

            jason.sendActionBar(Component.text("Whitelist is on", NamedTextColor.YELLOW));
        }
    }
}
