package net.silverstonemc.silverstonesurvival.commands;

import me.rerere.matrix.api.HackType;
import net.silverstonemc.silverstonesurvival.SilverstoneMain;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class Fly implements Listener {

    // AFK at night
    @EventHandler
    public void onAFK(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().toLowerCase().startsWith("/fly")) {
            Player player = event.getPlayer();
            World world = player.getWorld();

            if (!player.getGameMode().equals(GameMode.SURVIVAL)) return;
            if (!world.getName().startsWith("survival")) return;

            SilverstoneMain.matrix.tempBypass(player, HackType.MOVE, 10L);
        }
    }
}
