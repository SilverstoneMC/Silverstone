package net.silverstonemc.silverstoneproxy;

import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.silverstonemc.silverstoneproxy.commands.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class SilverstoneProxy extends Plugin implements Listener {

    private static SilverstoneProxy plugin;
    private static LuckPerms luckPerms;
    private static BungeeAudiences adventure;

    public static Configuration config;

    @Override
    public void onEnable() {
        plugin = this;
        config = loadConfig();
        luckPerms = LuckPermsProvider.get();
        adventure = BungeeAudiences.create(this);

        PluginManager pluginManager = getProxy().getPluginManager();

        pluginManager.registerCommand(this, new BaseCommand());
        pluginManager.registerCommand(this, new Forums());
        pluginManager.registerCommand(this, new Mods());
        pluginManager.registerCommand(this, new Report());
        pluginManager.registerCommand(this, new Restart());
        pluginManager.registerCommand(this, new RestartWhenEmpty());
        pluginManager.registerCommand(this, new Rules());
        pluginManager.registerCommand(this, new Tips());

        pluginManager.registerListener(this, new JoinEvent());

        getProxy().registerChannel("silverstone:pluginmsg");
    }

    @Override
    public void onDisable() {
        if (adventure != null) {
            adventure.close();
            adventure = null;
        }
    }

    public static LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public static BungeeAudiences getAdventure() {
        if (adventure == null)
            throw new IllegalStateException("Cannot retrieve audience provider while plugin is not enabled");
        return adventure;
    }

    public static SilverstoneProxy getPlugin() {
        return plugin;
    }

    public Configuration loadConfig() {
        if (!getDataFolder().exists())
            //noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) try (InputStream in = getResourceAsStream("config.yml")) {
            Files.copy(in, file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
