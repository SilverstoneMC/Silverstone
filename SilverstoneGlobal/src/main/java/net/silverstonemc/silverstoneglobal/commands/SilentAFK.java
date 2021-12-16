package net.silverstonemc.silverstoneglobal.commands;

import net.ess3.api.IEssentials;
import net.silverstonemc.silverstoneglobal.SilverstoneGlobal;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SilentAFK implements CommandExecutor {

    // safk
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Sorry, but only players can do that.");
            return true;
        }

        final IEssentials essentials = SilverstoneGlobal.getInstance().getEssentials();
        essentials.getUser(player).setAfk(!essentials.getUser(player).isAfk());

        return true;
    }
}
