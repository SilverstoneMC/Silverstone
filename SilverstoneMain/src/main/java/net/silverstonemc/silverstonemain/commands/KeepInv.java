package net.silverstonemc.silverstonemain.commands;

import net.silverstonemc.silverstonemain.SilverstoneMain;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class KeepInv implements CommandExecutor {

    private final JavaPlugin plugin;

    public KeepInv(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    final LuckPerms luckPerms = SilverstoneMain.getInstance().getLuckPerms();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Sorry, but only players can do that.");
            return true;
        }

        // keepInv is on (default group)
        if (player.getWorld().getName().toLowerCase().contains("survival") && !player.getWorld()
                .getName()
                .toLowerCase()
                .contains("the_end") && player.getGameMode().equals(GameMode.SURVIVAL))
            if (player.hasPermission("essentials.keepinv")) {
                BukkitRunnable task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
                        Group group = luckPerms.getGroupManager().getGroup("default-noki");

                        if (group == null) {
                            player.sendMessage(ChatColor.RED + "There was an error turning off keepInventory! Please contact a server admin.");
                            return;
                        }

                        user.data().add(InheritanceNode.builder(group).build());
                        luckPerms.getUserManager().saveUser(user);

                        player.sendMessage(ChatColor.GREEN + "keepInventory deactivated!");
                    }
                };
                task.runTaskAsynchronously(plugin);
                // keepInv is off (default-noki group)
            } else {
                player.sendMessage(ChatColor.YELLOW + "Activating keepInventory! Please hold still for 3 seconds for it to activate.");
                double x = player.getLocation().getX();
                double y = player.getLocation().getY();
                double z = player.getLocation().getZ();

                BukkitRunnable delay = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (player.getLocation().getX() == x &&
                                player.getLocation().getY() == y &&
                                player.getLocation().getZ() == z) {
                            BukkitRunnable task = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
                                    Group group = luckPerms.getGroupManager().getGroup("default-noki");

                                    if (group == null) {
                                        player.sendMessage(ChatColor.RED + "There was an error turning on keepInventory! Please contact a server admin.");
                                        return;
                                    }

                                    user.data().remove(InheritanceNode.builder(group).build());
                                    luckPerms.getUserManager().saveUser(user);

                                    player.sendMessage(ChatColor.GREEN + "keepInventory activated!");
                                }
                            };
                            task.runTaskAsynchronously(plugin);
                        } else player.sendMessage(ChatColor.RED + "keepInventory was not activated because you moved!");
                    }
                };
                delay.runTaskLater(plugin, 60);
            }
        else player.sendMessage(ChatColor.RED + "You must be in the Survival or Survival (Nether) world to do that!");
        return true;
    }
}
