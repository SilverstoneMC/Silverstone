package net.silverstonemc.silverstonecreative;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;

public class StripNBT implements Listener {
    @EventHandler
    public void inventory(InventoryInteractEvent event) {
        if (event.getWhoClicked().hasPermission("silverstone.trialmod")) return;
        if (!event.getWhoClicked().getItemOnCursor().hasItemMeta()) return;
        event.getWhoClicked().getItemOnCursor().setItemMeta(null);
    }

    @EventHandler
    public void place(BlockPlaceEvent event) {
        System.out.println(event.getBlockPlaced().getBlockData());
    }
}
