package net.silverstonemc.silverstonemain.events;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.silverstonemc.silverstonemain.SilverstoneMain;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("ConstantConditions")
public class TeleportEvent implements Listener {

    private final Economy econ = SilverstoneMain.getInstance().getEconomy();

    private final JavaPlugin plugin;

    public TeleportEvent(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.COMMAND) return;

        if (event.getTo()
                .equals(SilverstoneMain.data.getConfig()
                        .getLocation("data." + event.getPlayer().getUniqueId() + ".death"))) {
            Player player = event.getPlayer();
            EconomyResponse result;
            String option = plugin.getConfig().getString("amount-to-take");
            DecimalFormat format = new DecimalFormat(plugin.getConfig().getString("currency-format"));

            if (option.equalsIgnoreCase("ALL"))
                result = econ.withdrawPlayer(player, econ.getBalance(player));
            else if (option.contains("%")) if (option.contains("-")) {
                double min = Double.parseDouble(option.replaceAll("-.*", ""));
                double max = Double.parseDouble(option.replaceAll(".*-", "").replace("%", ""));
                double r = ThreadLocalRandom.current().nextDouble(min, max + 1);
                result = econ.withdrawPlayer(player, (r / 100) * econ.getBalance(player));
            } else result = econ.withdrawPlayer(player,
                    (Double.parseDouble(option.replace("%", "")) / 100) * econ.getBalance(player));
            else result = econ.withdrawPlayer(player, Double.parseDouble(option));

            if (!plugin.getConfig().getString("death-message").isBlank())
                player.sendMessage(
                        ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("death-message")
                                .replace("{MONEY}", String.valueOf(format.format(result.amount)))
                                .replace("{BALANCE}", String.valueOf(format.format(result.balance)))));

            SilverstoneMain.data.getConfig().set("data." + player.getUniqueId() + ".death", null);
            SilverstoneMain.data.saveConfig();
        }
    }
}
