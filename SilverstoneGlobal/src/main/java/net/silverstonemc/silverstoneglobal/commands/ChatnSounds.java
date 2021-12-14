package net.silverstonemc.silverstoneglobal.commands;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.silverstonemc.silverstoneglobal.SilverstoneGlobal;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class ChatnSounds implements CommandExecutor, Listener, PluginMessageListener {

    final LuckPerms luckPerms = SilverstoneGlobal.getInstance().getLuckPerms();

    private final JavaPlugin plugin;

    public ChatnSounds(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Sorry, but only players can do that.");
            return true;
        }

        switch (cmd.getName().toLowerCase()) {
            case "chatsounds" -> {
                boolean value = !sender.hasPermission("silverstone.chatsounds.enabled");
                BukkitRunnable task = new BukkitRunnable() {
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
                };
                task.runTaskAsynchronously(plugin);
            }

            case "joinleavesounds" -> {
                boolean value = !sender.hasPermission("silverstone.jlsounds.enabled");
                BukkitRunnable task = new BukkitRunnable() {
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
                };
                task.runTaskAsynchronously(plugin);
            }
        }


        return true;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        for (MetadataValue meta : event.getPlayer().getMetadata("vanished")) if (meta.asBoolean()) return;

        joinSound(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        for (MetadataValue meta : event.getPlayer().getMetadata("vanished")) if (meta.asBoolean()) return;

        quitSound(event.getPlayer());
    }

    @Override
    public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] message) {
        if (!channel.equals("silverstone:pluginmsg")) return;
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("FakeJoinLeave")) {
            int status = in.readInt();
            String uuid = in.readUTF();
            Player target = Bukkit.getPlayer(UUID.fromString(uuid));
            if (target == null) return;

            if (status == 1) joinSound(target);
            else if (status == 2) quitSound(target);
        }
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
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.5f, 1);

        if (PlainTextComponentSerializer.plainText().serialize(event.message()).equalsIgnoreCase("jason")) {
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    event.getPlayer()
                            .sendMessage(Component.text("If you have a question or something to say to Jason, please just say it. Simply saying \"Jason\" will not get you a reply.")
                                    .color(TextColor.fromHexString("#deda00")));
                }
            };
            task.runTask(plugin);
        }
    }

    public void joinSound(Player player) {
        for (Player players : Bukkit.getOnlinePlayers()) {
            if (players == player) continue;

            if (players.hasPermission("silverstone.jlsounds.enabled"))
                players.playSound(players.getLocation(), Sound.BLOCK_BELL_USE, SoundCategory.PLAYERS, 1, 1.5f);
        }
    }

    public void quitSound(Player player) {
        for (Player players : Bukkit.getOnlinePlayers()) {
            if (players == player) continue;

            if (players.hasPermission("silverstone.jlsounds.enabled"))
                players.playSound(players.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.PLAYERS, 1, 1.75f);
        }
    }
}
