package me.jasonhorkles.silverstonemain;

import me.darkeyedragon.randomtp.event.RandomPreTeleportEvent;
import me.darkeyedragon.randomtp.event.RandomTeleportCompletedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public record RTPLimit(JavaPlugin plugin) implements CommandExecutor, Listener {

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args[0].equalsIgnoreCase("check")) {
            // If player defined
            if (args.length > 1) {
                int value = 0;
                OfflinePlayer player = getOfflinePlayer(sender, args[1]);

                // If player is null, cancel the check
                if (player == null) return true;

                String username = player.getName();

                // Get the player's data
                if (SilverstoneMain.data.getConfig().contains("data." + player.getUniqueId() + ".rtps"))
                    value = SilverstoneMain.data.getConfig().getInt("data." + player.getUniqueId() + ".rtps");

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + username + " &chas randomly teleported &7" + value + " &ctimes."));
            } else sender.sendMessage(ChatColor.RED + "Please provide a player!");
            return true;
        }
        return false;
    }

    @EventHandler
    public void preRTP(RandomPreTeleportEvent event) {
        Player player = event.getPlayer();
        int value = 0;

        // If player has data, set it to 'value'
        // Otherwise leave value at 0 to be set later
        if (SilverstoneMain.data.getConfig().contains("data." + player.getUniqueId() + ".rtps"))
            value = SilverstoneMain.data.getConfig().getInt("data." + player.getUniqueId() + ".rtps");

        String rank = getLimit(player);
        if (rank.equals("bypass")) return;

        if (value >= (plugin.getConfig().getInt("rtp." + rank))) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou cannot randomly teleport more than &7" + plugin.getConfig()
                    .getInt("rtp." + rank) + "&c times!\nPurchase a higher rank to increase this limit."));
        }
    }

    @EventHandler
    public void postRTP(RandomTeleportCompletedEvent event) {
        Player player = event.getPlayer();
        int value = 0;

        // If player has data, set it to 'value'
        // Otherwise leave value at 0 to be set later
        if (SilverstoneMain.data.getConfig().contains("data." + player.getUniqueId() + ".rtps"))
            value = SilverstoneMain.data.getConfig().getInt("data." + player.getUniqueId() + ".rtps");

        // Add 1 to the player's value
        SilverstoneMain.data.getConfig().set("data." + player.getUniqueId() + ".rtps", (++value));
        SilverstoneMain.data.saveConfig();

        String rank = getLimit(player);
        if (!rank.equals("bypass")) if ((plugin.getConfig().getInt("rtp." + rank) - value) == 1)
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

        if (player.hasPermission("silverstone.rtplimit.bypass")) rank = "bypass";
        else if (player.hasPermission("silverstone.rtplimit.mvp")) rank = "mvp";
        else if (player.hasPermission("silverstone.rtplimit.vip+")) rank = "vip+";
        else if (player.hasPermission("silverstone.rtplimit.vip")) rank = "vip";
        else if (player.hasPermission("silverstone.rtplimit.member")) rank = "member";

        return rank;
    }

    @SuppressWarnings("deprecation")
    private OfflinePlayer getOfflinePlayer(CommandSender sender, String offlinePlayerName) {
        Player player = Bukkit.getPlayer(offlinePlayerName);
        if (player != null) return player;

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(offlinePlayerName);
        if (offlinePlayer == null) {
            sender.sendMessage(ChatColor.GRAY + "Player not cached. The server may lag while the player is retrieved...");
            offlinePlayer = Bukkit.getOfflinePlayer(offlinePlayerName);
        }
        if (!offlinePlayer.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "That player has never joined before!");
            return null;
        }

        return offlinePlayer;
    }
}

