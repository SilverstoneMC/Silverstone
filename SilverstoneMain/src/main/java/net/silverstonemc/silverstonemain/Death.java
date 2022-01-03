package net.silverstonemc.silverstonemain;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.Random;

@SuppressWarnings("ConstantConditions")
public class Death implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // If vanished
        for (MetadataValue meta : player.getMetadata("vanished")) if (meta.asBoolean()) return;

        try {
            EntityDamageEvent.DamageCause reason = player.getLastDamageCause().getCause();
            Random r = new Random();

            switch (reason) {
                case SUICIDE: // Suicide
                    event.deathMessage(null);
                    break;

                case FALL: // Fall damage
                    int random = r.nextInt(2);

                    String name = event.getEntity().getName();
                    String[] messages = new String[]{
                            "&7" + name + "&c failed to MLG water bucket",
                            "&7" + name + "&c broke their legs"
                    };
                    event.deathMessage(Component.text(ChatColor.translateAlternateColorCodes('&', messages[random])));
                    break;

                case VOID: // Fell into void
                    random = r.nextInt(3);

                    name = event.getEntity().getName();
                    messages = new String[]{
                            "&7" + name + "&c was ejected from the world",
                            "&7" + name + "&c fell into the abyss",
                            "&7" + name + "&c was yeeted into a void of despair"
                    };
                    event.deathMessage(Component.text(ChatColor.translateAlternateColorCodes('&', messages[random])));
                    break;

                case FLY_INTO_WALL: // Fly into wall
                    random = r.nextInt(2);

                    name = event.getEntity().getName();
                    messages = new String[]{
                            "&7" + name + "&c flew into a wall",
                            "&7" + name + "&c needs to take flying lessons"
                    };
                    event.deathMessage(Component.text(ChatColor.translateAlternateColorCodes('&', messages[random])));
                    break;

                case SUFFOCATION: // Suffocated
                    random = r.nextInt(3);

                    name = event.getEntity().getName();
                    messages = new String[]{
                            "&7" + name + "&c suffocated in a block",
                            "&7" + name + "&c forgot to breathe",
                            "&7" + name + "'s &clungs were blocked by a block"
                    };
                    event.deathMessage(Component.text(ChatColor.translateAlternateColorCodes('&', messages[random])));
                    break;

                case DROWNING: // Drowned
                    random = r.nextInt(3);

                    name = event.getEntity().getName();
                    messages = new String[]{
                            "&7" + name + "&c forgot to hold their breath underwater",
                            "&7" + name + "&c ran out of air",
                            "&cDrowning is not the way to go, &7" + name
                    };
                    event.deathMessage(Component.text(ChatColor.translateAlternateColorCodes('&', messages[random])));
                    break;

                case FIRE: // Burned (fire)
                case FIRE_TICK:
                    random = r.nextInt(3);

                    name = event.getEntity().getName();
                    messages = new String[]{
                            "&7" + name + "&c burned to death",
                            "&7" + name + "&c was charred whilst playing with fire",
                            "&7" + name + "&c has become one with the ashes"
                    };
                    event.deathMessage(Component.text(ChatColor.translateAlternateColorCodes('&', messages[random])));
                    break;

                case LAVA: // Burned (lava)
                    random = r.nextInt(2);

                    name = event.getEntity().getName();
                    messages = new String[]{
                            "&7" + name + "&c went swimming in lava",
                            "&7" + name + "&c got too close to lava",
                            "&7" + name + "&c burned in their hot tub"
                    };
                    event.deathMessage(Component.text(ChatColor.translateAlternateColorCodes('&', messages[random])));
                    break;

                case ENTITY_EXPLOSION: // Exploded
                case BLOCK_EXPLOSION:
                    random = r.nextInt(3);

                    name = event.getEntity().getName();
                    messages = new String[]{
                            "&7" + name + "&c exploded",
                            "&7" + name + "&c went kaboom",
                            "&7" + name + "&c was blown to bits"
                    };
                    event.deathMessage(Component.text(ChatColor.translateAlternateColorCodes('&', messages[random])));
                    break;

                case FALLING_BLOCK: // Falling block
                    random = r.nextInt(2);

                    name = event.getEntity().getName();
                    messages = new String[]{
                            "&7" + name + "&c was squashed by a block",
                            "&7" + name + "&c is now a pancake"
                    };
                    event.deathMessage(Component.text(ChatColor.translateAlternateColorCodes('&', messages[random])));
                    break;

                case CONTACT: // Cactus / berry bush / dripstone
                    random = r.nextInt(3);

                    name = event.getEntity().getName();
                    messages = new String[]{
                            "&7" + name + "&c was horrifically impaled",
                            "&7" + name + "&c was poked to death",
                            "&7" + name + "&c was killed by needles"
                    };
                    event.deathMessage(Component.text(ChatColor.translateAlternateColorCodes('&', messages[random])));
                    break;

                case HOT_FLOOR: // Magma block
                    random = r.nextInt(3);

                    name = event.getEntity().getName();
                    messages = new String[]{
                            "&7" + name + "'s &cfeet were singed",
                            "&7" + name + "&c burned their feet",
                            "&7" + name + "&c charred their toes"
                    };
                    event.deathMessage(Component.text(ChatColor.translateAlternateColorCodes('&', messages[random])));
                    break;

                case FREEZE: // Powdered snow
                    random = r.nextInt(3);

                    name = event.getEntity().getName();
                    messages = new String[]{
                            "&7" + name + "&c froze to death",
                            "&7" + name + "&c got frostbite",
                            "&7" + name + "&c became a popsicle"
                    };
                    event.deathMessage(Component.text(ChatColor.translateAlternateColorCodes('&', messages[random])));
                    break;

                default:
                    name = event.getEntity().getName();
                    Player killer = player.getKiller();

                    if (killer != null) {
                        // If killer is vanished
                        for (MetadataValue meta : killer.getMetadata("vanished")) if (meta.asBoolean()) return;

                        random = r.nextInt(4);

                        messages = new String[]{
                                "&7" + name + "&c has joined the dead thanks to &7" + killer.getName(),
                                "&7" + name + "&c was brutally killed by &7" + killer.getName(),
                                "&7" + name + "&c failed to live because of &7" + killer.getName(),
                                "&7" + name + "&c was murdered by &7" + killer.getName()
                        };
                    } else {
                        random = r.nextInt(5);

                        messages = new String[]{
                                "&7" + name + "&c has joined the dead",
                                "&cOop, &7" + name + "&c died",
                                "&7" + name + "&c was brutally killed",
                                "&7" + name + "&c failed to live",
                                "&7" + name + "&c was unable to Minecraft"
                        };
                    }
                    event.deathMessage(Component.text(ChatColor.translateAlternateColorCodes('&', messages[random])));
            }
        } catch (NullPointerException ignored) {
            Random r = new Random();
            int random = r.nextInt(2);

            String name = event.getEntity().getName();
            String[] messages = new String[]{
                    "&7" + name + "&c has joined the dead",
                    "&cOop, &7" + name + "&c died",
                    "&7" + name + "&c was brutally killed",
                    "&7" + name + "&c failed to live",
                    "&7" + name + "&c was unable to Minecraft"
            };
            event.deathMessage(Component.text(ChatColor.translateAlternateColorCodes('&', messages[random])));
        }
    }
}
