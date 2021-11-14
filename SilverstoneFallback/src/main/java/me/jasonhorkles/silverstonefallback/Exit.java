package me.jasonhorkles.silverstonefallback;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public record Exit(JavaPlugin plugin) implements CommandExecutor {

    private void send(Player player) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF("main");
                player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
            }
        };
        task.runTaskLater(plugin, 1);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("exit")) {
            player.sendMessage(Component.text(ChatColor.GREEN + "Returning you to the main server!"));
            send(player);
        }
        return true;
    }
}
