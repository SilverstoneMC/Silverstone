package net.silverstonemc.silverstonemain;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

public class WirelessButtons implements Listener {

    private static final Map<Player, Long> rulesCooldown = new HashMap<>();
    private static final Map<Player, Long> startingCooldown = new HashMap<>();
    private static final Map<Player, Long> socialCooldown = new HashMap<>();
    private static final Map<Player, Long> ranksCooldown = new HashMap<>();

    @EventHandler
    public void clickEvent(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;

        if (event.getClickedBlock().getType().equals(Material.DARK_OAK_BUTTON))
            if (event.getClickedBlock().getLocation().getWorld().getName().equalsIgnoreCase("utility")) {
                Location loc = event.getClickedBlock().getLocation();
                Player player = event.getPlayer();

                if (loc.getX() == 666 && loc.getY() == 67 && loc.getZ() == 661) {
                    // Check if on cooldown
                    // Still on cooldown
                    if (rulesCooldown.containsKey(player))
                        if (rulesCooldown.get(player) > System.currentTimeMillis()) return;
                    event.getPlayer().performCommand("rules");
                    rulesCooldown.put(player, System.currentTimeMillis() + 1500);

                } else if (loc.getX() == 671 && loc.getY() == 67 && loc.getZ() == 666) {
                    // Check if on cooldown
                    // Still on cooldown
                    if (startingCooldown.containsKey(player))
                        if (startingCooldown.get(player) > System.currentTimeMillis()) return;
                    event.getPlayer().performCommand("gettingstarted");
                    startingCooldown.put(player, System.currentTimeMillis() + 1500);

                } else if (loc.getX() == 666 && loc.getY() == 67 && loc.getZ() == 671) {
                    // Check if on cooldown
                    // Still on cooldown
                    if (socialCooldown.containsKey(player))
                        if (socialCooldown.get(player) > System.currentTimeMillis()) return;
                    event.getPlayer().performCommand("social");
                    socialCooldown.put(player, System.currentTimeMillis() + 1500);

                } else if (loc.getX() == 661 && loc.getY() == 67 && loc.getZ() == 666) {
                    // Check if on cooldown
                    // Still on cooldown
                    if (ranksCooldown.containsKey(player))
                        if (ranksCooldown.get(player) > System.currentTimeMillis()) return;
                    event.getPlayer().performCommand("ranks");
                    ranksCooldown.put(player, System.currentTimeMillis() + 1500);
                }
            }
    }
}
