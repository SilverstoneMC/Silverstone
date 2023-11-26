package net.silverstonemc.silverstoneglobal.events;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public class PluginMessages implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player messenger, byte[] bytes) {
        if (!channel.equals("silverstone:pluginmsg")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String message = in.readUTF();
        Player player = Bukkit.getPlayer(UUID.fromString(in.readUTF()));

        switch (message) {
            case "chatsound" -> {
                for (Player players : Bukkit.getOnlinePlayers())
                    if (players.hasPermission("silverstone.chatsounds.enabled")) if (players != player)
                        players.playSound(players.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                            SoundCategory.PLAYERS, 0.5f, 1);
            }

            case "joinsound" -> {
                boolean isVanished = in.readBoolean();
                
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (players == player) continue;
                    if (isVanished) if (!players.hasPermission("silverstone.moderator")) continue;

                    if (players.hasPermission("silverstone.jlsounds.enabled"))
                        players.playSound(players.getLocation(), Sound.BLOCK_BELL_USE, SoundCategory.PLAYERS,
                            1, 1.5f);
                }
            }

            case "leavesound" -> {
                boolean isVanished = in.readBoolean();
                
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (players == player) continue;
                    if (isVanished) if (!players.hasPermission("silverstone.moderator")) continue;

                    if (players.hasPermission("silverstone.jlsounds.enabled"))
                        players.playSound(players.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE,
                            SoundCategory.PLAYERS, 1, 1.75f);
                }
            }
        }
    }
}
