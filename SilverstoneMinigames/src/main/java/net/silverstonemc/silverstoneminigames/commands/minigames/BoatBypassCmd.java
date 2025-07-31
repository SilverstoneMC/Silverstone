package net.silverstonemc.silverstoneminigames.commands.minigames;

import net.silverstonemc.silverstoneminigames.events.BoatMount;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BoatBypassCmd implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        if (args.length < 1) return false;

        BoatMount.allowBoatPassenger = Boolean.parseBoolean(args[0]);
        sender.sendMessage("Boat bypass is now set to " + BoatMount.allowBoatPassenger);

        return true;
    }
}
