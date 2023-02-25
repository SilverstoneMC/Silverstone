package net.silverstonemc.silverstoneglobal.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.silverstonemc.silverstoneglobal.SilverstoneGlobal;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class ChatnSounds implements CommandExecutor, Listener {
    public ChatnSounds(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;
    private final LuckPerms luckPerms = SilverstoneGlobal.getInstance().getLuckPerms();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Sorry, but only players can do that.");
            return true;
        }

        //todo migrate to proxy
        switch (cmd.getName().toLowerCase()) {
            case "chatsounds" -> {
                boolean value = !sender.hasPermission("silverstone.chatsounds.enabled");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
                        if (value) {
                            user.data().add(Node.builder("silverstone.chatsounds.enabled").build());
                            sender.sendMessage(ChatColor.GREEN + "Chat sounds enabled.");
                        } else {
                            user.data().remove(Node.builder("silverstone.chatsounds.enabled").build());
                            sender.sendMessage(ChatColor.RED + "Chat sounds disabled.");
                        }
                        luckPerms.getUserManager().saveUser(user);
                    }
                }.runTaskAsynchronously(plugin);
            }

            case "joinleavesounds" -> {
                boolean value = !sender.hasPermission("silverstone.jlsounds.enabled");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
                        if (value) {
                            user.data().add(Node.builder("silverstone.jlsounds.enabled").build());
                            sender.sendMessage(ChatColor.GREEN + "Join/leave sounds enabled.");
                        } else {
                            user.data().remove(Node.builder("silverstone.jlsounds.enabled").build());
                            sender.sendMessage(ChatColor.RED + "Join/leave sounds disabled.");
                        }
                        luckPerms.getUserManager().saveUser(user);
                    }
                }.runTaskAsynchronously(plugin);
            }
        }


        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void cancelChat(AsyncChatEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncChatEvent event) {
        if (event.isCancelled()) return;
        // Play chat sound
        for (Player player : Bukkit.getOnlinePlayers())
            if (player.hasPermission("silverstone.chatsounds.enabled")) if (player != event.getPlayer())
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                    SoundCategory.PLAYERS, 0.5f, 1);
    }
}
