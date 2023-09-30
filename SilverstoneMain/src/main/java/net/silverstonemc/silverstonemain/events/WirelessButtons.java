package net.silverstonemc.silverstonemain.events;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.silverstonemc.silverstonemain.SilverstoneMain;
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
    private static final Map<Player, Long> ranksCooldown = new HashMap<>();
    private static final Map<Player, Long> startingCooldown = new HashMap<>();

    @EventHandler
    public void clickEvent(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;

        if (event.getClickedBlock().getType().equals(Material.STONE_BUTTON))
            if (event.getClickedBlock().getLocation().getWorld().getName().equalsIgnoreCase("utility")) {
                Location loc = event.getClickedBlock().getLocation();
                Player player = event.getPlayer();

                if (loc.getX() == -102 && loc.getY() == 67 && loc.getZ() == -106) {
                    // Check if on cooldown
                    // Still on cooldown
                    if (rulesCooldown.containsKey(player))
                        if (rulesCooldown.get(player) > System.currentTimeMillis()) return;

                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("rules");
                    player.sendPluginMessage(SilverstoneMain.getInstance(), "silverstone:pluginmsg", out.toByteArray());
                    
                    rulesCooldown.put(player, System.currentTimeMillis() + 1000);

                } else if (loc.getX() == -98 && loc.getY() == 67 && loc.getZ() == -102) {
                    // Check if on cooldown
                    // Still on cooldown
                    if (startingCooldown.containsKey(player))
                        if (startingCooldown.get(player) > System.currentTimeMillis()) return;
                    event.getPlayer().performCommand("gettingstarted");
                    startingCooldown.put(player, System.currentTimeMillis() + 1000);

                } else if (loc.getX() == -106 && loc.getY() == 67 && loc.getZ() == -102) {
                    // Check if on cooldown
                    // Still on cooldown
                    if (ranksCooldown.containsKey(player))
                        if (ranksCooldown.get(player) > System.currentTimeMillis()) return;
                    event.getPlayer().performCommand("ranks");
                    ranksCooldown.put(player, System.currentTimeMillis() + 1000);
                }
            }
    }
}
