package net.silverstonemc.silverstonemain.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.Economy;
import net.silverstonemc.silverstonemain.SilverstoneMain;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class RemoveRiptide implements Listener {

    private final JavaPlugin plugin;

    public RemoveRiptide(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final Economy econ = SilverstoneMain.getInstance().getEconomy();

    @EventHandler
    public void onRiptide(PlayerRiptideEvent event) {
        ItemMeta meta = event.getItem().getItemMeta();
        meta.removeEnchant(Enchantment.RIPTIDE);
        event.getItem().setItemMeta(meta);

        Player player = event.getPlayer();
        Location loc = player.getLocation();

        BukkitRunnable teleport = new BukkitRunnable() {
            @Override
            public void run() {
                player.setInvulnerable(true);
                player.teleportAsync(loc);
                player.setInvulnerable(false);
            }
        };
        teleport.runTaskLater(plugin, 2);

        econ.depositPlayer(player, 1000);
        player.sendMessage(Component.text("Sorry, but the Riptide enchant is disabled. Here's $1000 to make up for it.")
                .color(NamedTextColor.RED));
    }

    @EventHandler
    public void preEnchant(PrepareItemEnchantEvent event) {
        EnchantmentOffer[] offers = event.getOffers();
        for (int x = 0; x < offers.length; x++) {
            if (offers[x] == null) continue;
            if (offers[x].getEnchantment().equals(Enchantment.RIPTIDE)) {
                event.getOffers()[x].setEnchantment(Enchantment.DURABILITY);
                event.getOffers()[x].setEnchantmentLevel(3);
            }
        }
    }

    @EventHandler
    public void postEnchant(EnchantItemEvent event) {
        if (event.getEnchantsToAdd().containsKey(Enchantment.RIPTIDE)) {
            event.getEnchantsToAdd().remove(Enchantment.RIPTIDE);
            if (!event.getEnchantsToAdd().containsKey(Enchantment.DURABILITY))
                event.getEnchantsToAdd().put(Enchantment.DURABILITY, 3);
        }
    }

    @EventHandler
    public void villagerEnchant(VillagerAcquireTradeEvent event) {
        if (event.getRecipe().getResult().hasItemMeta())
            if (event.getRecipe().getResult().getItemMeta().toString().toUpperCase().contains("RIPTIDE")) {
                event.getEntity().resetOffers();
                plugin.getLogger()
                        .severe("Jason: Re-rolling villager Riptide trades @ " + event.getEntity().getLocation());
            }
    }

    @EventHandler
    public void fishEvent(PlayerFishEvent event) {
        if (!(event.getCaught() instanceof Item item)) return;
        ItemStack itemStack = item.getItemStack();
        if (!itemStack.hasItemMeta()) return;
        if (!itemStack.getItemMeta().hasEnchant(Enchantment.RIPTIDE)) return;

        itemStack.removeEnchantment(Enchantment.RIPTIDE);
        if (!itemStack.containsEnchantment(Enchantment.DURABILITY))
            itemStack.addEnchantment(Enchantment.DURABILITY, 3);

        item.setItemStack(itemStack);
        plugin.getLogger().severe("Jason: Removed Riptide enchant from " + event.getPlayer().getName() + " (Fishing)");
    }
}
