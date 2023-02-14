package net.silverstonemc.silverstonesurvival.commands;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public record DiscordRelay(JavaPlugin plugin) implements CommandExecutor {

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length > 1) {
            TextChannel discord = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(args[0]);
            if (discord == null) {
                plugin.getLogger().severe("Could not find channel called " + args[0] + " in the DiscordSRV config!");
                return true;
            }

            String message = "";
            for (int arg = 1; arg < args.length; arg++) {
                message = message.concat(args[arg]);
                message = message.concat(" ");
            }

            message = message.trim();

            discord.sendMessage(message).queue();
            return true;
        }
        return false;
    }
}