package me.jasonhorkles.silverstonemain;

import dev.norska.dsw.api.DeluxeSellwandPreSellEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public record SellStickConfirmation(JavaPlugin plugin) implements CommandExecutor, Listener {

    private static final Map<Player, Block> confirmationSentTo = new HashMap<>();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Sorry, but only players can do that.");
            return true;
        }

        if (SilverstoneMain.data.getConfig().contains("data." + player.getUniqueId() + ".sellstick")) {
            boolean value = SilverstoneMain.data.getConfig().getBoolean("data." + player.getUniqueId() + ".sellstick");
            SilverstoneMain.data.getConfig().set("data." + player.getUniqueId() + ".sellstick", !value);

            if (!value) player.sendMessage(ChatColor.GREEN + "Enabled Sell Stick confirmations.");
            else player.sendMessage(ChatColor.RED + "Disabled Sell Stick confirmations.");
        } else {
            SilverstoneMain.data.getConfig().set("data." + player.getUniqueId() + ".sellstick", false);
            player.sendMessage(ChatColor.RED + "Disabled Sell Stick confirmations.");
        }

        SilverstoneMain.data.saveConfig();
        return true;
    }

    @EventHandler
    public void preSellEvent(DeluxeSellwandPreSellEvent event) {
        Player player = event.getPlayer();
        Block block = player.getTargetBlock(5);
        if (block == null) return;

        boolean value = true;
        if (SilverstoneMain.data.getConfig().contains("data." + player.getUniqueId() + ".sellstick"))
            value = SilverstoneMain.data.getConfig().getBoolean("data." + player.getUniqueId() + ".sellstick");
        if (!value) return;

        if (confirmationSentTo.containsKey(player)) if (confirmationSentTo.get(player).equals(block)) return;

        event.setCancelled(true);
        player.sendMessage(Component.text("\nAre you sure you want to sell all the contents of that container?\nIf so, click the container again.")
                .color(NamedTextColor.RED)
                .append(Component.text("\nClick "))
                .color(NamedTextColor.GRAY)
                .append(Component.text("here")
                        .color(NamedTextColor.RED)
                        .clickEvent(ClickEvent.runCommand("/togglesellstickconfirmation"))
                        .hoverEvent(HoverEvent.showText(Component.text("/togglesellstickconfirmation")
                                .color(NamedTextColor.DARK_AQUA))))
                .append(Component.text(" to toggle this confirmation.").color(NamedTextColor.GRAY)));

        confirmationSentTo.put(player, block);
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (confirmationSentTo.containsKey(player))
                    confirmationSentTo.remove(player, block);
            }
        };
        task.runTaskLaterAsynchronously(plugin, 200);
    }
}
