package net.silverstonemc.silverstoneglobal;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Security {
    public Security(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;
    private static final LuckPerms luckPerms = SilverstoneGlobal.getInstance().getLuckPerms();

    public void check() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.hasPermission("silverstone.admin")) continue;

                    // me | alt | gage | panda | dragon
                    List<String> allowedUUIDs = List.of(
                        "a28173af-f0a9-47fe-8549-19c6bccf68da",
                        "bc9848dd-5dd9-4141-9790-f023134cbb7d",
                        "5c3d3b7c-aa02-4751-ae4b-60b277da9c35",
                        "75fb05a2-9d9e-49cb-be34-6bd5215548ba",
                        "e70a4622-85b6-417d-9201-7322e5094465");

                    String playerUUID = player.getUniqueId().toString();
                    if (allowedUUIDs.contains(playerUUID)) continue;

                    Group admin = luckPerms.getGroupManager().getGroup("admin");
                    Group owner = luckPerms.getGroupManager().getGroup("owner");
                    User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
                    //noinspection DataFlowIssue
                    user.data().remove(InheritanceNode.builder(admin).build());
                    //noinspection DataFlowIssue
                    user.data().remove(InheritanceNode.builder(owner).build());
                    luckPerms.getUserManager().saveUser(user);

                    for (int x = 0; x < 15; x++)
                        plugin.getLogger().warning(player.getName() + " has an admin permission!");

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            plugin.getLogger().warning("Deopping " + player.getName() + "...");
                            if (player.isOp()) player.setOp(false);
                            plugin.getLogger().warning("Warning " + player.getName() + "...");

                            ByteArrayDataOutput out = ByteStreams.newDataOutput();
                            out.writeUTF("warn");
                            out.writeUTF("admin");
                            player.sendPluginMessage(
                                SilverstoneGlobal.getInstance(),
                                "silverstone:pluginmsg",
                                out.toByteArray());
                        }
                    }.runTask(plugin);
                }
            }
        }.runTaskTimerAsynchronously(plugin, 100, 15);
    }
}
