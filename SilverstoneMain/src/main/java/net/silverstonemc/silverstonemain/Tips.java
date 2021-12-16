package net.silverstonemc.silverstonemain;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public record Tips(JavaPlugin plugin) implements Listener {

    @EventHandler
    public void breakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material block = event.getBlock().getType();

        // Survival world
        if (player.getWorld().getName().toLowerCase().startsWith("survival")) switch (block) {
            // Ores
            case COAL_ORE:
            case COPPER_ORE:
            case DEEPSLATE_COAL_ORE:
            case DEEPSLATE_COPPER_ORE:
            case DEEPSLATE_DIAMOND_ORE:
            case DEEPSLATE_EMERALD_ORE:
            case DEEPSLATE_GOLD_ORE:
            case DEEPSLATE_IRON_ORE:
            case DEEPSLATE_LAPIS_ORE:
            case DEEPSLATE_REDSTONE_ORE:
            case DIAMOND_ORE:
            case EMERALD_ORE:
            case GOLD_ORE:
            case IRON_ORE:
            case LAPIS_ORE:
            case NETHER_GOLD_ORE:
            case NETHER_QUARTZ_ORE:
            case REDSTONE_ORE:
                if (!SilverstoneMain.data.getConfig().contains("data." + player.getUniqueId() + ".tips.ores")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lTIP &b&l> &aHold crouch while mining an ore (and some other blocks) to quickly mine the whole vein!"));
                    tellJason("VeinMiner", player);

                    SilverstoneMain.data.getConfig().set("data." + player.getUniqueId() + ".tips.ores", true);
                    SilverstoneMain.data.saveConfig();
                }
                break;

            // Logs
            case ACACIA_LOG:
            case BIRCH_LOG:
            case DARK_OAK_LOG:
            case JUNGLE_LOG:
            case OAK_LOG:
            case SPRUCE_LOG:
                if (!SilverstoneMain.data.getConfig().contains("data." + player.getUniqueId() + ".tips.logs")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lTIP &b&l> &aHold crouch while breaking a tree with an iron axe (or better) to make the tree topple!"));
                    tellJason("tree", player);

                    SilverstoneMain.data.getConfig().set("data." + player.getUniqueId() + ".tips.logs", true);
                    SilverstoneMain.data.saveConfig();
                }
        }
    }

    @EventHandler
    public void placeBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Material block = event.getBlock().getType();

        // Survival world
        // Block place
        if (player.getWorld().getName().toLowerCase().startsWith("survival"))
            if (!SilverstoneMain.data.getConfig().contains("data." + player.getUniqueId() + ".tips.place")) {
                BukkitRunnable task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lTIP &b&l> &aType &b/sethome [home_name] &ato keep your location (bed spawns don't count)!"));
                        tellJason("sethome", player);
                    }
                };
                task.runTaskLater(plugin, 100);
                SilverstoneMain.data.getConfig().set("data." + player.getUniqueId() + ".tips.place", true);
                SilverstoneMain.data.saveConfig();
            }

        // Ladders
        if (block == Material.LADDER)
            if (!SilverstoneMain.data.getConfig().contains("data." + player.getUniqueId() + ".tips.ladder")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lTIP &b&l> &aLadders use gravity, so you can use them in a similar way to sand."));
                tellJason("ladder", player);

                SilverstoneMain.data.getConfig().set("data." + player.getUniqueId() + ".tips.ladder", true);
                SilverstoneMain.data.saveConfig();
            }
    }

    @SuppressWarnings("ConstantConditions")
    private void tellJason(String tip, Player player) {
        try {
            Bukkit.getPlayer(UUID.fromString("a28173af-f0a9-47fe-8549-19c6bccf68da"))
                    .sendMessage(Component.text("[")
                            .color(TextColor.fromHexString("#919191"))
                            .append(Component.text("SS").color(NamedTextColor.DARK_GREEN))
                            .append(Component.text("]").color(TextColor.fromHexString("#919191")))
                            .append(Component.text(" Showing " + tip + " tip to " + player.getName())
                                    .decorate(TextDecoration.ITALIC)));
        } catch (NullPointerException ignored) {
        }
    }
}
