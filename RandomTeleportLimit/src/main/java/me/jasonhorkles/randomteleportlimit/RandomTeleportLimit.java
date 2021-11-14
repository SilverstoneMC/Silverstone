package me.jasonhorkles.randomteleportlimit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("ConstantConditions")
public class RandomTeleportLimit extends JavaPlugin implements Listener {

    public static DataManager data;

    // Startup
    @Override
    public void onEnable() {
        data = new DataManager(this);

        saveDefaultConfig();

        getCommand("rtplimit").setTabCompleter(new TabComplete());

        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new RTPLimit(this), this);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length > 0) if (args[0].equalsIgnoreCase("reload")) {
            // No perm message
            if (sender.hasPermission("rtplimit.reload")) {
                saveDefaultConfig();
                reloadConfig();
                data = new DataManager(this);
                sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
            } else sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
            return true;
        } else if (args[0].equalsIgnoreCase("check")) {
            if (sender.hasPermission("rtplimit.check")) if (args.length > 1) {
                int value = 0;
                OfflinePlayer player = getOfflinePlayer(sender, args[1]);

                // If player is null, cancel the check
                if (player == null) return true;

                String username = player.getName();

                // Get the player's data
                if (data.getConfig().contains("data." + player.getUniqueId()))
                    value = data.getConfig().getInt("data." + player.getUniqueId());

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + username + " &chas randomly teleported &7" + value + " &ctimes."));
            } else sender.sendMessage(ChatColor.RED + "Please provide a player!");
            else sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    public static OfflinePlayer getOfflinePlayer(CommandSender sender, String offlinePlayerName) {
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
