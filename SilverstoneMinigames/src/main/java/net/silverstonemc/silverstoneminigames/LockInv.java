package net.silverstonemc.silverstoneminigames;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

@SuppressWarnings("DataFlowIssue")
public class LockInv implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;
        if (!player.getGameMode().equals(GameMode.ADVENTURE)) return;
        if (player.getInventory().getItem(22) == null) return;
        if (!player.getInventory().getItem(22).hasItemMeta()) return;
        if (!PlainTextComponentSerializer.plainText().serialize(player.getInventory().getItem(22)
            .getItemMeta().displayName()).equals("☺")) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        if (!player.getGameMode().equals(GameMode.ADVENTURE)) return;
        if (player.getInventory().getItem(22) == null) return;
        if (!player.getInventory().getItem(22).hasItemMeta()) return;
        if (!PlainTextComponentSerializer.plainText().serialize(player.getInventory().getItem(22)
            .getItemMeta().displayName()).equals("☺")) return;

        event.setCancelled(true);
    }
}
