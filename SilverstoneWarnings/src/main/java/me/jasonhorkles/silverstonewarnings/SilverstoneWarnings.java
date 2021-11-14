package me.jasonhorkles.silverstonewarnings;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import me.jasonhorkles.silverstonewarnings.commands.*;
import me.jasonhorkles.silverstonewarnings.managers.DataManager;
import me.jasonhorkles.silverstonewarnings.managers.QueueManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.UUID;

@SuppressWarnings("ConstantConditions")
public class SilverstoneWarnings extends JavaPlugin implements Listener {

    public static DataManager data;
    public static QueueManager queue;

    public static boolean discordEnabled = false;

    // Startup
    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("DiscordSRV") != null) {
            discordEnabled = true;
            DiscordSRV.api.subscribe(new DiscordReady(this));
        }

        data = new DataManager(this);
        queue = new QueueManager(this);

        saveDefaultConfig();
        data.saveDefaultConfig();
        queue.saveDefaultConfig();

        getCommand("reasons").setExecutor(new ReasonsCommand(this));
        getCommand("warn").setExecutor(new WarnCommand(this));
        getCommand("warnings").setExecutor(new WarningsCommand());
        getCommand("warnqueue").setExecutor(new WarnQueueCommand());
        getCommand("warnlist").setExecutor(new WarnListCommand());
        getCommand("warn").setTabCompleter(new TabComplete(this));
        getCommand("silverstonewarnings").setTabCompleter(new TabComplete(this));

        PluginManager pluginManager = this.getServer().getPluginManager();

        pluginManager.registerEvents(new JoinEvent(this), this);
        pluginManager.registerEvents(new WarnCommand(this), this);

        WarnCommand.createMainInv();
        WarnCommand.createHackInv();
        WarnCommand.createChatInv();
        WarnCommand.createOtherInv();
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length > 0) {
            TextChannel discord = null;
            if (SilverstoneWarnings.discordEnabled)
                discord = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("warnings");
            // Reload
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("sswarnings.admin")) {
                    saveDefaultConfig();
                    data.saveDefaultConfig();
                    queue.saveDefaultConfig();
                    reloadConfig();
                    data.reloadConfig();
                    queue.reloadConfig();
                    data = new DataManager(this);
                    queue = new QueueManager(this);
                    sender.sendMessage(ChatColor.GREEN + "SilverstoneWarnings reloaded!");
                    return true;
                }

            } else if (args[0].equalsIgnoreCase("remove")) { // remove <reason> <player>
                if (args.length < 3) return false;

                OfflinePlayer offlinePlayer = getOfflinePlayer(sender, args[2]);
                if (offlinePlayer == null) return true;
                UUID uuid = offlinePlayer.getUniqueId();

                int count = data.getConfig().getInt("data." + uuid + "." + args[1]);

                // Already has 0 warnings
                if ((count - 1) < 0) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + offlinePlayer.getName() + " &calready has 0 &7" + args[1] + " &cwarnings."));
                    return true;

                } else {
                    new UndoWarning(this).undoWarning(offlinePlayer, args[1], count);

                    if ((count - 1) == 0) data.getConfig().set("data." + uuid + "." + args[1], null);
                    else data.getConfig().set("data." + uuid + "." + args[1], count - 1);
                    data.saveConfig();
                    getLogger().info(ChatColor.translateAlternateColorCodes('&', "&7" + sender.getName() + " &cremoved 1 &7" + args[1] + " &cwarning from &7" + offlinePlayer
                            .getName()));

                    // Message staff
                    for (Player online : Bukkit.getOnlinePlayers())
                        if (online.hasPermission("sswarnings.warn"))
                            online.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + sender.getName() + " &cremoved 1 &7" + args[1] + " &cwarning from &7" + offlinePlayer
                                    .getName()));

                    // Message player if online and command not silent
                    try {
                        if (!args[3].equalsIgnoreCase("-s")) if (offlinePlayer.isOnline())
                            offlinePlayer.getPlayer()
                                    .sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + sender.getName() + " &cremoved 1 &7" + args[1] + " &cwarning from you."));
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                        if (offlinePlayer.isOnline())
                            offlinePlayer.getPlayer()
                                    .sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + sender.getName() + " &cremoved 1 &7" + args[1] + " &cwarning from you."));
                    }

                    if (SilverstoneWarnings.discordEnabled) {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setAuthor(sender.getName() + " removed 1 '" + args[1] + "' warning from " + offlinePlayer.getName(), null, "https://crafatar.com/avatars/" + offlinePlayer
                                .getUniqueId() + "?overlay=true");
                        embed.setColor(new Color(42, 212, 85));
                        discord.sendMessageEmbeds(embed.build()).queue();
                    }
                }
                return true;

            } else if (args[0].equalsIgnoreCase("clear")) { // clear <[reason]|all> <player>
                if (args.length < 3) return false;

                OfflinePlayer offlinePlayer = getOfflinePlayer(sender, args[2]);
                if (offlinePlayer == null) return true;
                UUID uuid = offlinePlayer.getUniqueId();

                // If all
                if (args[1].equalsIgnoreCase("all")) {
                    new UndoWarning(this).undoWarning(offlinePlayer, args[1], null);

                    data.getConfig().set("data." + uuid, null);
                    data.saveConfig();

                    getLogger().info(ChatColor.translateAlternateColorCodes('&', "&7" + sender.getName() + " &ccleared all of &7" + offlinePlayer
                            .getName() + "'s &cwarnings"));

                    // Message staff
                    for (Player online : Bukkit.getOnlinePlayers())
                        if (online.hasPermission("sswarnings.warn"))
                            online.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + sender.getName() + " &ccleared all of &7" + offlinePlayer
                                    .getName() + "'s &cwarnings"));

                    // Message player if online and command not silent
                    try {
                        if (!args[3].equalsIgnoreCase("-s")) if (offlinePlayer.isOnline())
                            offlinePlayer.getPlayer()
                                    .sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + sender.getName() + " &ccleared all your warnings."));
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                        if (offlinePlayer.isOnline())
                            offlinePlayer.getPlayer()
                                    .sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + sender.getName() + " &ccleared all your warnings."));
                    }

                    if (SilverstoneWarnings.discordEnabled) {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setAuthor(sender.getName() + " cleared all of " + offlinePlayer.getName() + "'s warnings", null, "https://crafatar.com/avatars/" + offlinePlayer
                                .getUniqueId() + "?overlay=true");
                        embed.setColor(new Color(42, 212, 85));
                        discord.sendMessageEmbeds(embed.build()).queue();
                    }

                } else { // If a reason is defined
                    new UndoWarning(this).undoWarning(offlinePlayer, args[1], null);

                    data.getConfig().set("data." + uuid + "." + args[1], null);
                    data.saveConfig();

                    getLogger().info(ChatColor.translateAlternateColorCodes('&', "&7" + sender.getName() + " &ccleared all &7" + args[1] + " &cwarnings from &7" + offlinePlayer
                            .getName()));

                    // Message staff
                    for (Player online : Bukkit.getOnlinePlayers())
                        if (online.hasPermission("sswarnings.warn"))
                            online.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + sender.getName() + " &ccleared all &7" + args[1] + " &cwarnings from &7" + offlinePlayer
                                    .getName()));

                    // Message player if online and command not silent
                    try {
                        if (!args[3].equalsIgnoreCase("-s")) if (offlinePlayer.isOnline())
                            offlinePlayer.getPlayer()
                                    .sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + sender.getName() + " &ccleared all your &7" + args[1] + " &cwarnings."));
                    } catch (ArrayIndexOutOfBoundsException ignored) {
                        if (offlinePlayer.isOnline())
                            offlinePlayer.getPlayer()
                                    .sendMessage(ChatColor.translateAlternateColorCodes('&', "&7" + sender.getName() + " &ccleared all your &7" + args[1] + " &cwarnings."));
                    }

                    if (SilverstoneWarnings.discordEnabled) {
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setAuthor(sender.getName() + " cleared all '" + args[1] + "' warnings from " + offlinePlayer
                                .getName(), null, "https://crafatar.com/avatars/" + offlinePlayer
                                .getUniqueId() + "?overlay=true");
                        embed.setColor(new Color(42, 212, 85));
                        discord.sendMessageEmbeds(embed.build()).queue();
                    }
                }
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    public static OfflinePlayer getOfflinePlayer(CommandSender sender, String offlinePlayerName) {
        Player player = Bukkit.getPlayer(offlinePlayerName);
        if (player != null) return player;

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(offlinePlayerName);
        if (offlinePlayer == null) {
            sender.sendMessage(ChatColor.GRAY + "Player not cached. The server may lag while the player is retrieved...");
            offlinePlayer = Bukkit.getOfflinePlayer(offlinePlayerName);
        }
        if (!offlinePlayer.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "That player has never joined before!");
            return null;
        }

        return offlinePlayer;
    }
}
