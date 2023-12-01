package net.silverstonemc.silverstoneglobal.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.silverstonemc.silverstoneglobal.SilverstoneGlobal;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BackendWarn implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (args.length < 2) return false;
        
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(Component.text("Player not found!", NamedTextColor.RED));
            return true;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("warn");
        out.writeUTF(args[1]);
        player.sendPluginMessage(SilverstoneGlobal.getInstance(), "silverstone:pluginmsg", out.toByteArray());

        return true;
    }
}
