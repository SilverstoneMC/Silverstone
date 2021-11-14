package me.jasonhorkles.silverstonemain.commands;

import me.jasonhorkles.silverstonemain.SilverstoneMain;
import net.ess3.api.IEssentials;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

public class AFK implements CommandExecutor, Listener {

    // safk
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Sorry, but only players can do that.");
            return true;
        }

        final IEssentials essentials = SilverstoneMain.getInstance().getEssentials();
        essentials.getUser(player).setAfk(!essentials.getUser(player).isAfk());

        return true;
    }

    // AFK at night
    @EventHandler
    public void onAFK(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().toLowerCase().startsWith("/afk")) {
            Player player = event.getPlayer();
            World world = player.getWorld();

            if (player.hasPermission("silverstone.moderator")) return;
            if (!world.getName().startsWith("survival")) return;

            // 6:30PM - 5:59AM
            if (world.getTime() >= 12500 && world.getTime() <= 23999) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You can't do /afk at night!");
            }
        }
    }
}
