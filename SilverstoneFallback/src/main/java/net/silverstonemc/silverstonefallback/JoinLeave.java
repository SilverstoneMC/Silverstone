package net.silverstonemc.silverstonefallback;

import com.viaversion.viaversion.api.Via;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public record JoinLeave(JavaPlugin plugin) implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.joinMessage(null);
        Player player = event.getPlayer();

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!event.getPlayer().isOnline()) {
                    this.cancel();
                    return;
                }

                int serverProtocolVersion = plugin.getConfig().getInt("server-protocol");
                String serverVersion = plugin.getConfig().getString("server-version");
                int playerVersion = Via.getAPI().getPlayerVersion(player);

                // Outdated server
                if (serverProtocolVersion < playerVersion) {
                    BukkitRunnable task = new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe server is currently on version &7" + serverVersion + "&c! Please change your client version to play."));
                        }
                    };
                    task.runTaskLater(plugin, 85);

                    if (!player.hasPermission("silverstone.vanished"))
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hopecommander versionmsg &7&o" + player.getName() + " &7&ojoined with a newer client (" + playerVersion + ")");

                    // Outdated client
                } else if (serverProtocolVersion > playerVersion) {
                    BukkitRunnable task = new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe server is currently on version &7" + serverVersion + "&c! Please update your client to play."));
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou will not be able to exit the fallback server unless you're on a &7" + serverVersion + "&c client!"));

                            if (playerVersion < 558)
                                player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Disclaimer: This parkour is impossible to beat with your client version. Please update to 1.15 or later for the full experience.");
                        }
                    };
                    task.runTaskLater(plugin, 85);

                    if (!player.hasPermission("silverstone.vanished"))
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hopecommander versionmsg &7&o" + player.getName() + " &7&ojoined with an outdated client (" + playerVersion + ")");

                    // Updated client
                } else player.sendMessage(Component.text(ChatColor.translateAlternateColorCodes('&', """
                                &r
                                &c----------------------------------
                                &r
                                &6Type\s"""))
                        .append(Component.text("/exit")
                                .color(NamedTextColor.YELLOW)
                                .clickEvent(ClickEvent.runCommand("/exit")))
                        .append(Component.text(ChatColor.translateAlternateColorCodes('&', """
                                &6 to return to the main server
                                &r
                                &c----------------------------------
                                """))));

                player.playSound(player.getLocation(), Sound.MUSIC_DISC_WAIT, SoundCategory.RECORDS, 1000000, 1);

                plugin.getServer()
                        .getScoreboardManager()
                        .getMainScoreboard()
                        .getTeam("Parkour")
                        .addEntry(player.getName());
            }
        };
        task.runTaskLater(plugin, 15);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        event.quitMessage(null);
    }
}
