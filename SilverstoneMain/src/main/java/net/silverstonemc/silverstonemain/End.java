package net.silverstonemc.silverstonemain;

import com.onarandombox.MultiverseCore.MultiverseCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.DefaultContextKeys;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("ConstantConditions")
public class End implements CommandExecutor, Listener {

    private final JavaPlugin plugin;
    private final String end = "survival_the_end";

    public End(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final Map<String, Long> endBounce = new HashMap<>();
    private final Map<Player, Long> ranBackCommand = new HashMap<>();

    final LuckPerms luckPerms = SilverstoneMain.getInstance().getLuckPerms();
    final MultiverseCore mv = SilverstoneMain.getInstance().getMVCore();

    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("regenend")) {
            final int[] timer = {0};
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    switch (timer[0]) {
                        case 0:
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sudo " + Bukkit.getPlayer(UUID.fromString("a28173af-f0a9-47fe-8549-19c6bccf68da"))
                                    .getName() + " home end");
                            break;
                        case 3:
                            for (Entity dragon : Bukkit.getWorld(end).getLivingEntities())
                                if (dragon.getType().equals(EntityType.ENDER_DRAGON)) dragon.remove();
                            break;
                        case 4:
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sudo " + Bukkit.getPlayer(UUID.fromString("a28173af-f0a9-47fe-8549-19c6bccf68da"))
                                    .getName() + " home home");
                            break;
                        case 5:
                            for (Player players : Bukkit.getOnlinePlayers())
                                players.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lWARNING &b&l> &aThe server may experience lag for a moment!"));
                            mv.getMVWorldManager().regenWorld(end, true, true, "", true);
                            break;
                        case 10:
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sudo " + Bukkit.getPlayer(UUID.fromString("a28173af-f0a9-47fe-8549-19c6bccf68da"))
                                    .getName() + " home end");
                            for (Player players : Bukkit.getOnlinePlayers())
                                players.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lNOTICE &b&l> &aThe End has been reset!"));
                            dragonIsAlive();
                            this.cancel();
                    }
                    timer[0] = timer[0] + 1;
                }
            };
            task.runTaskTimer(plugin, 0, 20);
        }

        if (cmd.getName().equalsIgnoreCase("dragon"))
            if (Bukkit.getWorld(end).getLivingEntities().toString().contains("CraftEnderDragon"))
                sender.sendMessage(Component.text("The Ender Dragon is still alive! Visit ")
                        .color(NamedTextColor.RED)
                        .append(Component.text("this link")
                                .color(NamedTextColor.AQUA)
                                .clickEvent(ClickEvent.openUrl("https://rebrand.ly/SilverstoneEndReset")))
                        .append(Component.text(" to see when The End resets.")
                                .color(NamedTextColor.RED)));
            else sender.sendMessage(Component.text("The Ender Dragon has been defeated! Visit ")
                    .color(NamedTextColor.GREEN)
                    .append(Component.text("this link")
                            .color(NamedTextColor.AQUA)
                            .clickEvent(ClickEvent.openUrl("https://rebrand.ly/SilverstoneEndReset")))
                    .append(Component.text(" to see when The End resets next.")
                            .color(NamedTextColor.GREEN)));

        if (cmd.getName().equalsIgnoreCase("allowend")) {
            Player player = (Player) sender;
            SilverstoneMain.data.getConfig().set("data." + player.getUniqueId() + ".end", true);
            SilverstoneMain.data.saveConfig();
            sender.sendMessage(ChatColor.GREEN + "You may now enter The End.");
        }
        return true;
    }

    @EventHandler
    public void onBack(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().toLowerCase().startsWith("/back"))
            ranBackCommand.put(event.getPlayer(), System.currentTimeMillis() + 5000);
    }

    @EventHandler
    public void dragonDeath(EntityDeathEvent event) {
        if (event.getEntity().getType().equals(EntityType.ENDER_DRAGON))
            if (event.getEntity().getWorld().getName().equalsIgnoreCase(end)) {
                BukkitRunnable task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        luckPerms.getGroupManager().modifyGroup("default", group -> {
                            group.data().add(Node.builder("essentials.back.into." + end).build());
                            group.data()
                                    .remove(Node.builder("essentials.tpa")
                                            .withContext(DefaultContextKeys.WORLD_KEY, end)
                                            .value(false)
                                            .build());
                            group.data()
                                    .remove(Node.builder("essentials.tpaccept")
                                            .withContext(DefaultContextKeys.WORLD_KEY, end)
                                            .value(false)
                                            .build());
                            group.data()
                                    .remove(Node.builder("essentials.home")
                                            .withContext(DefaultContextKeys.WORLD_KEY, end)
                                            .value(false)
                                            .build());
                            group.data()
                                    .remove(Node.builder("essentials.warp")
                                            .withContext(DefaultContextKeys.WORLD_KEY, end)
                                            .value(false)
                                            .build());
                            group.data()
                                    .remove(Node.builder("essentials.back")
                                            .withContext(DefaultContextKeys.WORLD_KEY, end)
                                            .value(false)
                                            .build());
                            group.data()
                                    .remove(Node.builder("essentials.spawn")
                                            .withContext(DefaultContextKeys.WORLD_KEY, end)
                                            .value(false)
                                            .build());
                        });
                    }
                };
                task.runTaskAsynchronously(plugin);
                Bukkit.getLogger().info(ChatColor.translateAlternateColorCodes('&', "The Ender Dragon has died."));
            }
    }

    @EventHandler
    public void dragonSpawn(EntitySpawnEvent event) {
        if (event.getEntity().getType().equals(EntityType.ENDER_DRAGON))
            if (event.getEntity().getWorld().getName().equalsIgnoreCase(plugin.getConfig().getString("end-world")))
                dragonIsAlive();
    }

    public void dragonIsAlive() {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                luckPerms.getGroupManager().modifyGroup("default", group -> {
                    group.data().add(Node.builder("essentials.back.into." + end).value(false).build());
                    group.data()
                            .add(Node.builder("essentials.tpa")
                                    .withContext(DefaultContextKeys.WORLD_KEY, end)
                                    .value(false)
                                    .build());
                    group.data()
                            .add(Node.builder("essentials.tpaccept")
                                    .withContext(DefaultContextKeys.WORLD_KEY, end)
                                    .value(false)
                                    .build());
                    group.data()
                            .add(Node.builder("essentials.home")
                                    .withContext(DefaultContextKeys.WORLD_KEY, end)
                                    .value(false)
                                    .build());
                    group.data()
                            .add(Node.builder("essentials.warp")
                                    .withContext(DefaultContextKeys.WORLD_KEY, end)
                                    .value(false)
                                    .build());
                    group.data()
                            .add(Node.builder("essentials.back")
                                    .withContext(DefaultContextKeys.WORLD_KEY, end)
                                    .value(false)
                                    .build());
                    group.data()
                            .add(Node.builder("essentials.spawn")
                                    .withContext(DefaultContextKeys.WORLD_KEY, end)
                                    .value(false)
                                    .build());
                });
            }
        };
        task.runTaskAsynchronously(plugin);
        Bukkit.getLogger().info(ChatColor.translateAlternateColorCodes('&', "The Ender Dragon is now alive."));
    }

    @EventHandler
    public void enterEnd(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode().equals(GameMode.SURVIVAL) && player.getWorld().getName().equalsIgnoreCase(end)) {
            if (ranBackCommand.containsKey(player))
                if (ranBackCommand.get(player) >= System.currentTimeMillis()) return;
                else ranBackCommand.remove(player);

            player.teleportAsync(Bukkit.getWorld(end)
                    .getHighestBlockAt(0, 0)
                    .getLocation()
                    .add(0.5, 1, 0.5));
            player.sendMessage(ChatColor.RED + "Please note that The End will reset every 2 weeks on Saturday, keepInventory is off, and you cannot exit the world without dying unless the dragon has been defeated.");

            player.setInvulnerable(true);
            BukkitRunnable invulnerable = new BukkitRunnable() {
                @Override
                public void run() {
                    player.setInvulnerable(false);
                }
            };
            invulnerable.runTaskLater(plugin, 100);
        }
    }

    @EventHandler
    public void portalEnter(PlayerPortalEvent event) {
        Player player = event.getPlayer();

        // Check if the portal is anywhere above or below the player
        boolean portalNear = false;
        for (int y = -2; y < 3; y++)
            if (player.getLocation().add(0, y, 0).getBlock().getType().equals(Material.END_PORTAL)) portalNear = true;
        // If not near an end portal, return
        if (!portalNear) return;

        if (player.getGameMode() == GameMode.SURVIVAL) if (!player.isInvulnerable()) {
            player.setInvulnerable(true);
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.getFireTicks() > 0) player.setFireTicks(0);
                    player.setInvulnerable(false);
                }
            };
            task.runTaskLater(plugin, 60);
        }

        if (!SilverstoneMain.data.getConfig().contains("data." + player.getUniqueId() + ".end")) {
            event.setCancelled(true);
            if (endBounce.containsKey(player.getName())) {
                if (endBounce.get(player.getName()) <= System.currentTimeMillis())
                    player.sendMessage(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Are you sure? Click ")
                            .append(Component.text(ChatColor.GRAY + "" + ChatColor.BOLD + "here")
                                    .clickEvent(ClickEvent.runCommand("/allowend")))
                            .append(Component.text(ChatColor.translateAlternateColorCodes('&', "&c&l to enter.\n" +
                                    "&cNote: The End will reset every 2 weeks on Saturday. keepInventory is off and you cannot exit without dying unless the dragon has been defeated. Type ")))
                            .append(Component.text("/dragon")
                                    .color(NamedTextColor.GRAY)
                                    .clickEvent(ClickEvent.runCommand("/dragon")))
                            .append(Component.text(ChatColor.translateAlternateColorCodes('&', "&c to see if it's alive or not."))));
            } else player.sendMessage(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "Are you sure? Click ")
                    .append(Component.text(ChatColor.GRAY + "" + ChatColor.BOLD + "here")
                            .clickEvent(ClickEvent.runCommand("/allowend")))
                    .append(Component.text(ChatColor.translateAlternateColorCodes('&', "&c&l to enter.\n" +
                            "&cNote: The End will reset every 2 weeks on Saturday. keepInventory is off and you cannot exit without dying unless the dragon has been defeated. Type ")))
                    .append(Component.text("/dragon")
                            .color(NamedTextColor.GRAY)
                            .clickEvent(ClickEvent.runCommand("/dragon")))
                    .append(Component.text(ChatColor.translateAlternateColorCodes('&', "&c to see if it's alive or not."))));
            endBounce.put(player.getName(), System.currentTimeMillis() + 3000);

            Vector vector = player.getLocation().getDirection().multiply(-0.5).setY(0.5);
            player.setVelocity(vector);
        }
    }
}
