package net.silverstonemc.silverstonemain.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public record Chat(JavaPlugin plugin) implements Listener {

    private static final Map<String, Long> cooldown = new HashMap<>();

    private static boolean listening = false;

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncChatEvent event) {
        // My epic stuff
        if (event.getPlayer().getUniqueId().toString().equals("a28173af-f0a9-47fe-8549-19c6bccf68da") && listening) {
            if (checkMessage(event.message()).equalsIgnoreCase("discord plz")) {
                BukkitRunnable task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Player player : Bukkit.getOnlinePlayers()) player.performCommand("discord");
                    }
                };
                task.runTask(plugin);

            } else if (checkMessage(event.message()).equalsIgnoreCase("restart plz")) {
                BukkitRunnable task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
                    }
                };
                task.runTask(plugin);

            } else if (checkMessage(event.message()).equalsIgnoreCase("ily")) say("ily too ♥", 16);
            else if (checkMessage(event.message()).equalsIgnoreCase("nm wbu")) say("just watchin y'all", 15);
            listening = false;
            return;
        }

        // plz kill me
        if (checkMessage(event.message()).toLowerCase()
                .contains("pls kill me") || checkMessage(event.message()).toLowerCase()
                .contains("plz kill me") || checkMessage(event.message()).toLowerCase()
                .contains("please kill me") || checkMessage(event.message()).toLowerCase()
                .contains("kill me pls") || checkMessage(event.message()).toLowerCase()
                .contains("kill me plz") || checkMessage(event.message()).toLowerCase()
                .contains("kill me please")) {

            if (checkCooldown(event.getPlayer().getName(), "kill")) return;
            if (!event.getPlayer().getWorld().getName().toLowerCase().startsWith("survival")) return;
            if (checkMessage(event.message()).toLowerCase()
                    .contains("don't") || checkMessage(event.message()).toLowerCase()
                    .contains("dont") || checkMessage(event
                    .message()).toLowerCase().contains("do not"))
                return;

            say("okay lol", 4);

            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    event.getPlayer()
                            .setLastDamageCause(new EntityDamageEvent(event.getPlayer(), DamageCause.CUSTOM, Float.MAX_VALUE));
                    event.getPlayer().setHealth(0);
                }
            };
            task.runTaskLater(plugin, 14);

            setCooldown(event.getPlayer().getName(), "kill");
        }

        // hey server
        if (checkMessage(event.message()).equalsIgnoreCase("hey server"))
            if (event.getPlayer().getUniqueId().toString().equals("a28173af-f0a9-47fe-8549-19c6bccf68da")) {
                Random r = new Random();
                int x = r.nextInt(3);
                switch (x) {
                    case 0 -> say("hey, what's up?", 7);
                    case 1 -> say("sup", 5);
                    case 2 -> say("what u want", 6);
                }
                listening = true;
                BukkitRunnable task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        listening = false;
                    }
                };
                task.runTaskLater(plugin, 300);
            } else {
                if (checkCooldown(event.getPlayer().getName(), "hey")) return;
                say("hey", 12);
                setCooldown(event.getPlayer().getName(), "hey");
            }

        // hi server
        if (checkMessage(event.message()).equalsIgnoreCase("hi server"))
            if (event.getPlayer().getUniqueId().toString().equals("a28173af-f0a9-47fe-8549-19c6bccf68da"))
                say("hi Jason ☺", 14);
            else {
                if (checkCooldown(event.getPlayer().getName(), "hi")) return;
                say("hi", 10);
                setCooldown(event.getPlayer().getName(), "hi");
            }

        // ily server
        if (checkMessage(event.message()).equalsIgnoreCase("ily server")) {
            if (checkCooldown(event.getPlayer().getName(), "ily")) return;
            say("ily too ♥", 16);
            setCooldown(event.getPlayer().getName(), "ily");
        }

        // this is the best server
        if (checkMessage(event.message()).toLowerCase().contains("this is the best server")) {
            if (checkCooldown(event.getPlayer().getName(), "best")) return;
            say(":D", 10);
            setCooldown(event.getPlayer().getName(), "best");
        }

        // bbl?
        if (checkMessage(event.message()).toLowerCase()
                .contains("what's bbl") || checkMessage(event.message()).toLowerCase()
                .contains("whats bbl") || checkMessage(event.message()).toLowerCase()
                .contains("bbl?")) {

            if (checkCooldown(event.getPlayer().getName(), "bbl")) return;
            say("BBL = Be Back Later", 10);
            setCooldown(event.getPlayer().getName(), "bbl");
        }

        // thanks server
        if (checkMessage(event.message()).toLowerCase()
                .contains("thanks server") || checkMessage(event.message()).toLowerCase()
                .contains("thx server")) {
            if (checkCooldown(event.getPlayer().getName(), "thanks")) return;
            say("You're welcome! ☺", 14);
            setCooldown(event.getPlayer().getName(), "thanks");
        }
    }

    private String checkMessage(Component text) {
        return PlainTextComponentSerializer.plainText().serialize(text);
    }

    private boolean checkCooldown(String player, String string) {
        // Check if on cooldown
        // Still on cooldown
        if (cooldown.containsKey(player + "-" + string))
            return cooldown.get(player + "-" + string) > System.currentTimeMillis();
        return false;
    }

    private void setCooldown(String player, String string) {
        cooldown.put(player + "-" + string, System.currentTimeMillis() + 15000);
    }

    private void say(String message, int delay) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "say " + message);
            }
        };
        task.runTaskLater(plugin, delay);
    }
}
