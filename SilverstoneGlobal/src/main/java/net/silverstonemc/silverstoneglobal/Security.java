package net.silverstonemc.silverstoneglobal;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings("ConstantConditions")
public record Security(JavaPlugin plugin) {

    private static final LuckPerms luckPerms = SilverstoneGlobal.getInstance().getLuckPerms();

    public void check() {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers())
                    if (player.hasPermission("silverstone.owner")) {
                        String uuid = player.getUniqueId().toString();
                        // me | alt | ace | panda | dragon
                        if (!uuid.equals("a28173af-f0a9-47fe-8549-19c6bccf68da") && !uuid.equals("bc9848dd-5dd9-4141-9790-f023134cbb7d") && !uuid
                                .equals("5c3d3b7c-aa02-4751-ae4b-60b277da9c35") && !uuid.equals("75fb05a2-9d9e-49cb-be34-6bd5215548ba") && !uuid
                                .equals("e70a4622-85b6-417d-9201-7322e5094465")) {
                            Group group = luckPerms.getGroupManager().getGroup("owner");
                            User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
                            user.data().remove(InheritanceNode.builder(group).build());
                            luckPerms.getUserManager().saveUser(user);

                            for (int x = 0; x < 15; x++)
                                plugin.getLogger().warning(player.getName() + " has an owner permission!");

                            BukkitRunnable task = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    plugin.getLogger().warning("Deopping " + player.getName() + "...");
                                    if (player.isOp()) player.setOp(false);
                                    plugin.getLogger().warning("Warning " + player.getName() + "...");
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "hopecommander warn " + player.getName() + " owner");
                                }
                            };
                            task.runTask(plugin);
                        }
                    }
            }
        };
        task.runTaskTimerAsynchronously(plugin, 100, 15);
    }
}
