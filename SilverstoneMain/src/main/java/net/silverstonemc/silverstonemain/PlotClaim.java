package net.silverstonemc.silverstonemain;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;

public class PlotClaim implements Listener {

    private final ArrayList<Player> claimed = new ArrayList<>();

    @EventHandler
    public void onPlotClaimCommand(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage().toLowerCase();
        if (cmd.startsWith("/p") && (cmd.contains("claim") || cmd.contains("auto")))
            if (JoinEvent.newPlayers.containsKey(event.getPlayer())) {
                int x = 0;
                for (Player players : Bukkit.getOnlinePlayers()) if (players.hasPermission("silverstone.trialmod")) x++;

                if (x == 0) {
                    Player player = event.getPlayer();
                    if (claimed.contains(player)) return;
                    TextChannel discord = DiscordSRV.getPlugin()
                            .getDestinationTextChannelForGameChannelName("newplayers");
                    discord.sendMessage("<@&667793980318154783>, **" + player
                            .getName() + "** claimed a plot!").queue();
                    claimed.add(player);
                }
            }
    }
}
