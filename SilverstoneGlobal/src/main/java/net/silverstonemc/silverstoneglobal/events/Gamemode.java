package net.silverstonemc.silverstoneglobal.events;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Gamemode implements Listener {
    public Gamemode(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;

    @EventHandler
    public void gamemodeChange(PlayerGameModeChangeEvent event) {
        // Night vision
        if (event.getNewGameMode().equals(GameMode.SPECTATOR)) event.getPlayer().addPotionEffect(
            new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 255, false, false, false));
        else if (event.getPlayer().getGameMode().equals(GameMode.SPECTATOR))
            event.getPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);

        // Fix fly being off
        new BukkitRunnable() {
            @Override
            public void run() {
                if (event.getNewGameMode() == GameMode.CREATIVE) event.getPlayer().setAllowFlight(true);
                if (event.getNewGameMode() == GameMode.SPECTATOR) event.getPlayer().setFlying(true);
            }
        }.runTaskLater(plugin, 10);
    }
}
