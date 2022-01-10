package net.silverstonemc.silverstoneproxy.commands;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@SuppressWarnings("UnstableApiUsage")
public class FakeJoinLeave extends Command {

    private final Plugin plugin = SilverstoneProxy.getPlugin();
    private final LuckPerms luckPerms = SilverstoneProxy.getLuckPerms();

    public FakeJoinLeave() {
        super("fakejoinleave", "silverstone.fakejoinleave", "fjl");
    }

    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer player)) {
            plugin.getLogger().info(ChatColor.RED + "You must be a player to do that!");
            return;
        }
        User lpPlayer = luckPerms.getPlayerAdapter(ProxiedPlayer.class).getUser(player);
        String prefix = lpPlayer.getCachedData().getMetaData().getPrefix();
        if (prefix == null) prefix = "";

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("FakeJoinLeave"); // Sub channel

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        int x = 1;

        // If already vanished
        if (player.hasPermission("silverstone.vanished")) {
            player.chat("/v disable");
            for (ProxiedPlayer players : plugin.getProxy().getPlayers())
                players.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&8[&2+&8] &b" + prefix + player
                        .getDisplayName())));

            try {
                msgout.writeUTF(player.getUniqueId().toString());
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            plugin.getLogger()
                    .info(ChatColor.translateAlternateColorCodes('&', prefix + player.getDisplayName() + " &dfake arrived"));
        } else {
            // If not vanished yet
            player.chat("/v enable");
            for (ProxiedPlayer players : plugin.getProxy().getPlayers())
                players.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&8[&4-&8] &b" + prefix + player
                        .getDisplayName())));

            x = 2;

            try {
                msgout.writeUTF(player.getUniqueId().toString());
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            plugin.getLogger()
                    .info(ChatColor.translateAlternateColorCodes('&', prefix + player.getDisplayName() + " &dfake left"));
        }

        out.writeInt(x);
        out.write(msgbytes.toByteArray());

        if (plugin.getProxy().getServerInfo("main").getPlayers().size() > 0) {
            ProxiedPlayer messenger = Iterables.getFirst(plugin.getProxy()
                    .getServerInfo("main")
                    .getPlayers(), null);
            assert messenger != null;
            messenger.getServer().getInfo().sendData("silverstone:pluginmsg", out.toByteArray());
        }

        if (plugin.getProxy().getServerInfo("creative").getPlayers().size() > 0) {
            ProxiedPlayer messenger = Iterables.getFirst(plugin.getProxy()
                    .getServerInfo("creative")
                    .getPlayers(), null);
            assert messenger != null;
            messenger.getServer().getInfo().sendData("silverstone:pluginmsg", out.toByteArray());
        }

        if (plugin.getProxy().getServerInfo("minigames").getPlayers().size() > 0) {
            ProxiedPlayer messenger = Iterables.getFirst(plugin.getProxy()
                    .getServerInfo("minigames")
                    .getPlayers(), null);
            assert messenger != null;
            messenger.getServer().getInfo().sendData("silverstone:pluginmsg", out.toByteArray());
        }
    }
}
