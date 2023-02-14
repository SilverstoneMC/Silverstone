package net.silverstonemc.silverstonesurvival.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class JoinLeaveSpam implements Listener {
    public JoinLeaveSpam(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;
    private final Map<String, Integer> leaves = new HashMap<>();

    public void removeLeave(Player player) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                leaves.put(player.getName(), leaves.get(player.getName()) - 1);
            }
        };
        task.runTaskLater(plugin, plugin.getConfig().getInt("join-leave-spam.expire-after") * 20L);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("silverstone.joinleavespam.bypass")) return;

        if (leaves.containsKey(player.getName()))
            if (leaves.get(player.getName()) >= plugin.getConfig().getInt("join-leave-spam.leaves"))
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "warn " + player.getName() + " " + plugin.getConfig().getString("join-leave-spam.warn"));
            else {
                leaves.put(player.getName(), leaves.get(player.getName()) + 1);
                removeLeave(player);
            }
        else {
            leaves.put(player.getName(), 1);
            removeLeave(player);
        }
    }
}
