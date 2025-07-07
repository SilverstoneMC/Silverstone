package net.silverstonemc.silverstoneglobal.events;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class PluginMessages implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, @NotNull Player messenger, byte @NotNull [] bytes) {
        if (!channel.equals("silverstone:pluginmsg")) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String message = in.readUTF();
        Player sender = null;
        if (!message.equals("broadcastsound")) sender = Bukkit.getPlayer(UUID.fromString(in.readUTF()));

        switch (message) {
            case "globalchatsound" -> {
                for (Player players : Bukkit.getOnlinePlayers())
                    if (players.hasPermission("silverstone.chatsounds.enabled"))
                        if (players != sender) players.playSound(
                            players.getLocation(),
                            Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                            SoundCategory.PLAYERS,
                            0.4f,
                            1);
            }

            case "privatesound" -> {
                // In this case, the sender variable is actually the receiver
                if (sender == null) return;
                if (sender.hasPermission("silverstone.chatsounds.enabled"))
                    sender.playSound(
                        sender.getLocation(),
                        Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM,
                        SoundCategory.PLAYERS,
                        1,
                        2);
            }

            case "partychatsound" -> {
                List<String> partyMembers = List.of(in.readUTF().split(","));

                for (String memberName : partyMembers) {
                    Player player = Bukkit.getPlayer(memberName);
                    if (player == null) continue;

                    if (player.hasPermission("silverstone.chatsounds.enabled"))
                        if (player != sender) player.playSound(
                            player.getLocation(),
                            Sound.ENTITY_ITEM_FRAME_PLACE,
                            SoundCategory.PLAYERS,
                            1,
                            2);
                }
            }

            case "staffchatsound" -> {
                for (Player players : Bukkit.getOnlinePlayers())
                    if (players.hasPermission("silverstone.chatsounds.enabled") && players.hasPermission(
                        "silverstone.moderator")) if (players != sender) players.playSound(
                        players.getLocation(),
                        Sound.ENTITY_EXPERIENCE_BOTTLE_THROW,
                        SoundCategory.PLAYERS,
                        0.6f,
                        2);
            }

            case "broadcastsound" -> {
                for (Player players : Bukkit.getOnlinePlayers())
                    players.playSound(
                        players.getLocation(),
                        Sound.BLOCK_BELL_USE,
                        SoundCategory.MASTER,
                        1,
                        0);
            }

            case "joinsound" -> {
                boolean isVanished = in.readBoolean();

                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (players == sender) continue;
                    if (isVanished) if (!players.hasPermission("silverstone.moderator")) continue;

                    if (players.hasPermission("silverstone.jlsounds.enabled"))
                        players.playSound(
                            players.getLocation(),
                            Sound.BLOCK_BELL_USE,
                            SoundCategory.PLAYERS,
                            1,
                            1.5f);
                }
            }

            case "switchsound" -> {
                boolean isVanished = in.readBoolean();

                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (players == sender) continue;
                    if (isVanished) if (!players.hasPermission("silverstone.moderator")) continue;

                    if (players.hasPermission("silverstone.jlsounds.enabled"))
                        players.playSound(
                            players.getLocation(),
                            Sound.UI_TOAST_IN,
                            SoundCategory.PLAYERS,
                            1,
                            1.3f);
                }
            }

            case "leavesound" -> {
                boolean isVanished = in.readBoolean();

                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (players == sender) continue;
                    if (isVanished) if (!players.hasPermission("silverstone.moderator")) continue;

                    if (players.hasPermission("silverstone.jlsounds.enabled"))
                        players.playSound(
                            players.getLocation(),
                            Sound.BLOCK_BEACON_DEACTIVATE,
                            SoundCategory.PLAYERS,
                            1,
                            1.75f);
                }
            }
        }
    }
}
