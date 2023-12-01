package net.silverstonemc.silverstoneglobal;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

import java.util.UUID;

@SuppressWarnings("DataFlowIssue")
public class Whitelist {
    public static void whitelist() {
        if (Bukkit.getServer().hasWhitelist()) {
            try {
                Bukkit.getPlayer(UUID.fromString("a28173af-f0a9-47fe-8549-19c6bccf68da"))
                    .sendActionBar(Component.text("Whitelist is on", NamedTextColor.YELLOW));
            } catch (NullPointerException ignored) {
            }

            SilverstoneGlobal.getInstance().getLogger().warning("Whitelist is on");
        }
    }
}
