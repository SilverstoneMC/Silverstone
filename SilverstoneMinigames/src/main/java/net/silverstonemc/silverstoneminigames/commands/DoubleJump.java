package net.silverstonemc.silverstoneminigames.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoubleJump implements CommandExecutor, Listener {
    public DoubleJump(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;
    private final Map<String, Long> cooldowns = new HashMap<>();
    private static final Map<Player, Integer> jumps = new HashMap<>();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length > 1) {
            List<Entity> selector = Bukkit.selectEntities(sender, args[0]);
            try {
                for (Entity entity : selector) {
                    Player player = Bukkit.getPlayer(entity.getName());
                    if (player == null) break;

                    jumps.put(player, Integer.parseInt(args[1]));

                    ItemStack item = new ItemStack(Material.FEATHER);
                    ItemMeta meta = item.getItemMeta();
                    meta.displayName(Component.text("Double Jump").color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD));
                    item.setItemMeta(meta);
                    player.getInventory().addItem(item);
                }
            } catch (IndexOutOfBoundsException e) {
                sender.sendMessage(
                    Component.text("Please provide a valid selector!").color(NamedTextColor.RED));
            }
            return true;
        }
        return false;
    }

    @EventHandler
    public void doubleJump(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction()
            .equals(Action.RIGHT_CLICK_BLOCK) || event.getAction()
            .equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK))
            if (player.getWorld().getName()
                .equalsIgnoreCase(plugin.getConfig().getString("minigame-world")))
                if (player.getInventory().getItemInMainHand().getType() == Material.FEATHER)
                    if (player.getInventory().getItemInMainHand().getItemMeta().hasDisplayName())
                        //noinspection DataFlowIssue
                        if (player.getInventory().getItemInMainHand().getItemMeta().displayName().equals(
                            Component.text("Double Jump").color(NamedTextColor.RED)
                                .decorate(TextDecoration.BOLD))) {
                            // Still on cooldown
                            if (cooldowns.containsKey(player.getName()))
                                if (cooldowns.get(player.getName()) > System.currentTimeMillis()) return;
                            cooldowns.put(player.getName(), System.currentTimeMillis() + 750);

                            int jumpCount = 0;
                            if (jumps.containsKey(player)) jumpCount = jumps.get(player);

                            if (jumpCount == 0) {
                                player.sendMessage(Component.text("You can't double jump any more!")
                                    .color(NamedTextColor.RED));
                                return;
                            }

                            Vector vector = player.getLocation().getDirection().multiply(0.5).setY(0.85);
                            player.setVelocity(vector);
                            jumps.put(player, jumpCount - 1);
                            player.sendMessage(
                                Component.text("You have " + (jumpCount - 1) + " double jumps remaining.")
                                    .color(NamedTextColor.RED));
                        }
    }
}
