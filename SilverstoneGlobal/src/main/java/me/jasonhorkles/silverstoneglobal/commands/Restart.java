package me.jasonhorkles.silverstoneglobal.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings({"UnstableApiUsage", "ConstantConditions"})
public record Restart(JavaPlugin plugin) implements CommandExecutor {

    private static boolean restarting = false;
    private static String server = null;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (plugin.getConfig().getString("server").equalsIgnoreCase("main")) server = "fallback";
        else server = "main";

        switch (cmd.getName().toLowerCase()) {
            case "restart" -> {
                if (!restarting) {
                    restarting = true;
                    final int[] sec = {10};

                    BukkitRunnable task = new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!restarting) {
                                this.cancel();
                                return;
                            }

                            if (Bukkit.getOnlinePlayers().size() == 0) {
                                this.cancel();
                                plugin.getServer().spigot().restart();
                                return;
                            }

                            for (Player player : Bukkit.getOnlinePlayers())
                                player.sendActionBar(Component.text(ChatColor.translateAlternateColorCodes('&', "&6&lSERVER RESTARTING IN: &e&l" + sec[0])));
                            plugin.getLogger().info("Server restarting in: " + sec[0]);

                            switch (sec[0]) {
                                case 10 -> {
                                    for (Player player : Bukkit.getOnlinePlayers()) {
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.MASTER, 100, 1.2f);
                                        player.sendMessage(Component.text(ChatColor.translateAlternateColorCodes('&', "&c&lClick "))
                                                .append(Component.text("here")
                                                        .color(NamedTextColor.GRAY)
                                                        .decorate(TextDecoration.BOLD)
                                                        .decorate(TextDecoration.UNDERLINED)
                                                        .clickEvent(ClickEvent.runCommand("/server " + server)))
                                                .append(Component.text(ChatColor.translateAlternateColorCodes('&', " &c&lto evacuate!"))));
                                    }
                                    sec[0] -= 1;
                                }
                                case 9, 8, 7, 6, 5, 4 -> {
                                    for (Player player : Bukkit.getOnlinePlayers())
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.MASTER, 100, 1.2f);
                                    sec[0] -= 1;
                                }
                                case 3 -> {
                                    for (Player player : Bukkit.getOnlinePlayers()) {
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.MASTER, 100, 1.6f);
                                        player.sendMessage(Component.text(ChatColor.translateAlternateColorCodes('&', "&c&lClick "))
                                                .append(Component.text("here")
                                                        .color(NamedTextColor.GRAY)
                                                        .decorate(TextDecoration.BOLD)
                                                        .decorate(TextDecoration.UNDERLINED)
                                                        .clickEvent(ClickEvent.runCommand("/server " + server)))
                                                .append(Component.text(ChatColor.translateAlternateColorCodes('&', " &c&lto evacuate!"))));
                                    }
                                    sec[0] -= 1;
                                }
                                case 2, 1 -> {
                                    for (Player player : Bukkit.getOnlinePlayers())
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.MASTER, 100, 1.6f);
                                    sec[0] -= 1;
                                }
                                case 0 -> {
                                    for (Player player : Bukkit.getOnlinePlayers()) {
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.MASTER, 100, 2);
                                        send(player);
                                    }
                                    sec[0] -= 1;
                                }
                                case -10 -> {
                                    this.cancel();
                                    plugin.getServer().spigot().restart();
                                }
                            }
                        }
                    };
                    task.runTaskTimer(plugin, 0, 20);
                } else {
                    restarting = false;
                    for (Player player : Bukkit.getOnlinePlayers())
                        player.sendActionBar(Component.text(ChatColor.translateAlternateColorCodes('&', "&a&lSERVER RESTART CANCELLED!")));
                    plugin.getLogger().info("Server restart cancelled!");
                }
            }

            case "forcerestart" -> plugin.getServer().spigot().restart();

            case "quickrestart" -> {
                if (Bukkit.getOnlinePlayers().size() > 0) {
                    plugin.getLogger().info("Restarting...");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendActionBar(Component.text(ChatColor.translateAlternateColorCodes('&', "&b&lSERVER RESTARTING... ATTEMPTING TO TRANSFER PLAYERS")));
                        send(player);
                    }

                    BukkitRunnable task = new BukkitRunnable() {
                        @Override
                        public void run() {
                            plugin.getServer().spigot().restart();
                        }
                    };
                    task.runTaskLater(plugin, 20);
                } else plugin.getServer().spigot().restart();
            }

            case "restartwhenempty" -> {
                for (Player player : Bukkit.getOnlinePlayers())
                    player.sendActionBar(Component.text(ChatColor.translateAlternateColorCodes('&', "&c&lSERVER SCHEDULED TO RESTART WHEN EMPTY")));
                plugin.getLogger().info("Server scheduled to restart when empty.");

                BukkitRunnable task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (Bukkit.getOnlinePlayers().size() == 0) plugin.getServer().spigot().restart();
                    }
                };
                task.runTaskTimer(plugin, 0, 300);
            }

            case "schedulerestart" -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lWARNING &b&l> &aThe server is scheduled to restart in &b5 &aminutes!"));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.MASTER, 100, 1.6f);
                }

                BukkitRunnable fourMin = new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lWARNING &b&l> &aThe server is scheduled to restart in &b1 &aminute!"));
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.MASTER, 100, 1.6f);
                        }
                    }
                };
                fourMin.runTaskLater(plugin, 4800);

                BukkitRunnable fiveMin = new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
                    }
                };
                fiveMin.runTaskLater(plugin, 6000);
            }
        }
        return true;
    }

    public void send(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }
}
