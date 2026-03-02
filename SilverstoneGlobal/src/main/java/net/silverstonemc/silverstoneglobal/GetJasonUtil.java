package net.silverstonemc.silverstoneglobal;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public final class GetJasonUtil {
    private static final UUID JASON_UUID = UUID.fromString("a28173af-f0a9-47fe-8549-19c6bccf68da");

    public static Optional<Player> getPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(JASON_UUID));
    }
}
