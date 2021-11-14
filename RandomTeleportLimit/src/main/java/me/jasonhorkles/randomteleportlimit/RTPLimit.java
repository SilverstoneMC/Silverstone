package me.jasonhorkles.randomteleportlimit;

import me.darkeyedragon.randomtp.event.RandomPreTeleportEvent;
import me.darkeyedragon.randomtp.event.RandomTeleportCompletedEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public record RTPLimit(JavaPlugin plugin) implements Listener {

    @EventHandler
    public void preRTP(RandomPreTeleportEvent event) {
        Player player = event.getPlayer();
        int value = 0;

        // If player has data, set it to 'value'
        // Otherwise leave value at 0 to be set later
        if (RandomTeleportLimit.data.getConfig().contains("data." + player.getUniqueId()))
            value = RandomTeleportLimit.data.getConfig().getInt("data." + player.getUniqueId());

        String rank = getLimit(player);
        if (rank.equals("admin")) return;

        if (value >= (plugin.getConfig().getInt("RTP." + rank))) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou cannot randomly teleport more than &7" + plugin
                    .getConfig()
                    .getInt("RTP." + rank) + "&c times!\nPurchase a higher rank to increase this limit."));
        }
    }

    @EventHandler
    public void postRTP(RandomTeleportCompletedEvent event) {
        Player player = event.getPlayer();
        int value = 0;

        // If player has data, set it to 'value'
        // Otherwise leave value at 0 to be set later
        if (RandomTeleportLimit.data.getConfig().contains("data." + player.getUniqueId()))
            value = RandomTeleportLimit.data.getConfig().getInt("data." + player.getUniqueId());

        // Add 1 to the player's value
        RandomTeleportLimit.data.getConfig().set("data." + player.getUniqueId(), (value + 1));
        RandomTeleportLimit.data.saveConfig();

        // Set 'value' to the new amount
        value = RandomTeleportLimit.data.getConfig().getInt("data." + player.getUniqueId());

        String rank = getLimit(player);

        if (!rank.equals("admin")) if ((plugin.getConfig().getInt("RTP." + rank) - value) == 1)
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou may randomly teleport &7" + (plugin
                    .getConfig()
                    .getInt("RTP." + rank) - value) + "&c more time."));
        else
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou may randomly teleport &7" + (plugin
                    .getConfig()
                    .getInt("RTP." + rank) - value) + "&c more times."));
    }

    private String getLimit(Player player) {
        String rank = "default";

        if (player.hasPermission("rtplimit.limit.admin")) rank = "admin";
        else if (player.hasPermission("rtplimit.limit.mvp")) rank = "mvp";
        else if (player.hasPermission("rtplimit.limit.vip+")) rank = "vip+";
        else if (player.hasPermission("rtplimit.limit.vip")) rank = "vip";
        else if (player.hasPermission("rtplimit.limit.member")) rank = "member";

        return rank;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (RTPLimit) obj;
        return Objects.equals(this.plugin, that.plugin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plugin);
    }

    @Override
    public String toString() {
        return "RTPLimit[" +
                "plugin=" + plugin + ']';
    }

}
