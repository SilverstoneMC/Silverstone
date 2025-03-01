package net.silverstonemc.silverstoneglobal.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Live implements CommandExecutor {
    public Live(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Sorry, but only players can do that.", NamedTextColor.RED));
            return true;
        }

        if (sender.hasPermission("silverstone.live"))
            player.performCommand("esocialspy " + player.getName() + " enable");
        else player.performCommand("esocialspy " + player.getName() + " disable");

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("live");
        player.sendPluginMessage(plugin, "silverstone:pluginmsg", out.toByteArray());
        return true;
    }
}
