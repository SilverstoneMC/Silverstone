package net.silverstonemc.silverstoneglobal.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"DataFlowIssue"})
public record Restart(JavaPlugin plugin) implements CommandExecutor {
    private static boolean restarting = false;

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        // #serverSpecific
        // Send to minigames server if anything but the minigames server
        String server;
        if (plugin.getConfig().getString("server").equals("minigames")) server = "creative";
        else server = "minigames";

        String finalServer = server;
        switch (cmd.getName().toLowerCase()) {
            case "restart" -> {
                if (!restarting) {
                    restarting = true;
                    final int[] sec = {10};

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!restarting) {
                                this.cancel();
                                return;
                            }

                            if (Bukkit.getOnlinePlayers().isEmpty()) {
                                this.cancel();
                                plugin.getServer().spigot().restart();
                                return;
                            }

                            for (Player player : Bukkit.getOnlinePlayers())
                                player.sendActionBar(
                                    Component.text("SERVER RESTARTING IN: ", NamedTextColor.GOLD,
                                            TextDecoration.BOLD)
                                        .append(Component.text(sec[0], NamedTextColor.YELLOW)));
                            plugin.getLogger().info("Server restarting in: " + sec[0]);

                            TextComponent evacuate = Component.text("Click ", NamedTextColor.RED,
                                    TextDecoration.BOLD).append(
                                    Component.text("here", NamedTextColor.GRAY, TextDecoration.UNDERLINED)
                                        .clickEvent(ClickEvent.runCommand("/server " + finalServer)))
                                .append(Component.text(" to evacuate!", NamedTextColor.RED));

                            switch (sec[0]) {
                                case 10 -> {
                                    for (Player player : Bukkit.getOnlinePlayers()) {
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP,
                                            SoundCategory.MASTER, 100, 1.2f);
                                        player.sendMessage(evacuate);
                                    }
                                }
                                case 9, 8, 7, 6, 5, 4 -> {
                                    for (Player player : Bukkit.getOnlinePlayers())
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP,
                                            SoundCategory.MASTER, 100, 1.2f);
                                }
                                case 3 -> {
                                    for (Player player : Bukkit.getOnlinePlayers()) {
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP,
                                            SoundCategory.MASTER, 100, 1.6f);
                                        player.sendMessage(evacuate);
                                    }
                                }
                                case 2, 1 -> {
                                    for (Player player : Bukkit.getOnlinePlayers())
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP,
                                            SoundCategory.MASTER, 100, 1.6f);
                                }
                                case 0 -> {
                                    for (Player player : Bukkit.getOnlinePlayers()) {
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP,
                                            SoundCategory.MASTER, 100, 2);
                                        send(player, server);
                                    }
                                }
                                case -10 -> {
                                    this.cancel();
                                    plugin.getServer().spigot().restart();
                                }
                            }
                            sec[0] -= 1;
                        }
                    }.runTaskTimer(plugin, 0, 20);

                } else {
                    restarting = false;
                    for (Player player : Bukkit.getOnlinePlayers())
                        player.sendActionBar(Component.text("SERVER RESTART CANCELLED!", NamedTextColor.GREEN,
                            TextDecoration.BOLD));
                    plugin.getLogger().info("Server restart cancelled!");
                }
            }

            case "forcerestart" -> plugin.getServer().spigot().restart();

            case "quickrestart" -> {
                if (!Bukkit.getOnlinePlayers().isEmpty()) {
                    plugin.getLogger().info("Restarting...");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendActionBar(
                            Component.text("SERVER RESTARTING... ATTEMPTING TO TRANSFER PLAYERS",
                                NamedTextColor.AQUA, TextDecoration.BOLD));
                        send(player, server);
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            plugin.getServer().spigot().restart();
                        }
                    }.runTaskLater(plugin, 20);
                } else plugin.getServer().spigot().restart();
            }

            case "restartwhenempty" -> {
                for (Player player : Bukkit.getOnlinePlayers())
                    player.sendActionBar(
                        Component.text("SERVER SCHEDULED TO RESTART WHEN EMPTY", NamedTextColor.RED,
                            TextDecoration.BOLD));
                plugin.getLogger().info("Server scheduled to restart when empty.");

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (Bukkit.getOnlinePlayers().isEmpty()) plugin.getServer().spigot().restart();
                    }
                }.runTaskTimer(plugin, 0, 300);
            }

            case "schedulerestart" -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(Component.text()
                        .append(Component.text("WARNING", NamedTextColor.RED, TextDecoration.BOLD))
                        .append(Component.text(" > ", NamedTextColor.AQUA, TextDecoration.BOLD)).append(
                            Component.text("The server is scheduled to restart in ", NamedTextColor.GREEN))
                        .append(Component.text("5", NamedTextColor.AQUA))
                        .append(Component.text(" minutes!", NamedTextColor.GREEN)));

                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.MASTER,
                        100, 1.6f);
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendMessage(Component.text()
                                .append(Component.text("WARNING", NamedTextColor.RED, TextDecoration.BOLD))
                                .append(Component.text(" > ", NamedTextColor.AQUA, TextDecoration.BOLD))
                                .append(Component.text("The server is scheduled to restart in ",
                                    NamedTextColor.GREEN)).append(Component.text("1", NamedTextColor.AQUA))
                                .append(Component.text(" minute!", NamedTextColor.GREEN)));

                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP,
                                SoundCategory.MASTER, 100, 1.6f);
                        }
                    }
                }.runTaskLater(plugin, 4800);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
                    }
                }.runTaskLater(plugin, 6000);
            }
        }
        return true;
    }

    public void send(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }
}
