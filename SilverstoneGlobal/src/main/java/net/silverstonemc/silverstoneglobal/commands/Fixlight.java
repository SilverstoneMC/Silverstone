package net.silverstonemc.silverstoneglobal.commands;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PermissionNode;
import net.silverstonemc.silverstoneglobal.SilverstoneGlobal;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Fixlight implements CommandExecutor {

    private final JavaPlugin plugin;

    public Fixlight(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    final LuckPerms luckPerms = SilverstoneGlobal.getInstance().getLuckPerms();
    private static final Map<Player, Long> cooldown = new HashMap<>();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Sorry, but only players can do that.");
            return true;
        }

        // Check if on cooldown
        // Still on cooldown
        if (cooldown.containsKey(player)) if (cooldown.get(player) > System.currentTimeMillis()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou cannot recalculate lighting for another &7" + ((cooldown
                    .get(player) - System.currentTimeMillis()) / 1000) + " &cseconds."));
            return true;
        }

        cooldown.put(player, System.currentTimeMillis() + 10000);

        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        user.data().add(PermissionNode.builder("bukkit.command.paper.fixlight").build());
        luckPerms.getUserManager().saveUser(user);

        player.performCommand("paper fixlight 3");

        BukkitRunnable removePerm = new BukkitRunnable() {
            @Override
            public void run() {
                user.data().remove(PermissionNode.builder("bukkit.command.paper.fixlight").build());
                luckPerms.getUserManager().saveUser(user);
            }
        };
        removePerm.runTaskLater(plugin, 1);
        return true;
    }
}
