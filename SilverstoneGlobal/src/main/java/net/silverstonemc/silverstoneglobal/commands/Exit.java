package net.silverstonemc.silverstoneglobal.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"DataFlowIssue"})
public record Exit(JavaPlugin plugin) implements CommandExecutor, Listener {
    private void send(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF("minigames");
                player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
            }
        }.runTaskLater(plugin, 1);
    }

    // Perm based, so won't show up in minigames server
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("exit")) {
            player.sendMessage(
                Component.text("Returning you to the Minigames server!").color(NamedTextColor.GREEN));
            send(player);
        }
        return true;
    }

    // Add aliases for other servers
    @EventHandler
    public void aliases(PlayerCommandPreprocessEvent event) {
        if (plugin.getConfig().getString("server").equals("minigames")) return;

        String cmd = event.getMessage().toLowerCase();
        if (cmd.startsWith("/spawn") || cmd.startsWith("/lobby") || cmd.startsWith("/hub")) {
            event.setCancelled(true);
            event.getPlayer().performCommand("exit");
        }
    }
}
