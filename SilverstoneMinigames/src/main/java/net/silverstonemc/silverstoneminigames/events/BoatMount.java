package net.silverstonemc.silverstoneminigames.events;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class BoatMount implements Listener {
    public static boolean allowBoatPassenger;

    @EventHandler
    public void onBoatMount(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player player)) return;
        if (allowBoatPassenger) return;
        if (event.getVehicle().getType() != EntityType.SPRUCE_BOAT) return;
        if (!player.hasPermission("silverstone.minigames.boatracing")) return;
        if (event.getVehicle().getPassengers().isEmpty()) return;

        event.setCancelled(true);
    }
}
