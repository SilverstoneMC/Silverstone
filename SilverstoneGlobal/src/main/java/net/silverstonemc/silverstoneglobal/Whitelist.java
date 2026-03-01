package net.silverstonemc.silverstoneglobal;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

public class Whitelist {
    public static void whitelist() {
        if (Bukkit.getServer().hasWhitelist()) {
            SilverstoneGlobal.getInstance().getLogger().warning("Whitelist is on");

            Optional<Player> jason = new GetJasonUtil().getJason();
            if (jason.isEmpty()) return;

            jason.get().sendActionBar(Component.text("Whitelist is on", NamedTextColor.YELLOW));
        }
    }
}
