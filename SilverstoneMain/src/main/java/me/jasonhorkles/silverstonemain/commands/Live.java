package me.jasonhorkles.silverstonemain.commands;

import me.jasonhorkles.silverstonemain.SilverstoneMain;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class Live implements CommandExecutor {

    private final JavaPlugin plugin;

    public Live(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    final LuckPerms luckPerms = SilverstoneMain.getInstance().getLuckPerms();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Sorry, but only players can do that.");
            return true;
        }

        Group group = luckPerms.getGroupManager().getGroup("live");

        if (group == null) {
            player.sendMessage(ChatColor.RED + "Couldn't find the live group!");
            return true;
        }

        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);

        player.performCommand("esocialspy");
        player.sendMessage(Component.text("Click here to toggle socialspy")
                .color(NamedTextColor.GRAY)
                .decorate(TextDecoration.UNDERLINED)
                .clickEvent(ClickEvent.runCommand("/socialspy")));

        if (player.hasPermission("silverstone.live")) user.data().remove(InheritanceNode.builder(group).build());
        else {
            user.data().add(InheritanceNode.builder(group).build());
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    for (int x = 0; x < 50; x++) player.sendMessage("");
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "You're live!");
                }
            };
            task.runTaskLater(plugin, 100);
        }
        luckPerms.getUserManager().saveUser(user);
        return true;
    }
}
