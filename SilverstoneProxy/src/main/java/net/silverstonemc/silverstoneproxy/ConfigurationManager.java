package net.silverstonemc.silverstoneproxy;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigurationManager {
    public static Configuration config;
    public static Configuration data;
    public static Configuration queue;
    public static Configuration userCache;
    
    private final SilverstoneProxy plugin = SilverstoneProxy.getPlugin();
    
    public void initialize() {
        config = loadFile("config.yml");
        data = loadFile("data.yml");
        queue = loadFile("queue.yml");
        userCache = loadFile("usercache.yml");
    }
    
    public Configuration loadFile(String fileName) {
        if (!plugin.getDataFolder().exists())
            //noinspection ResultOfMethodCallIgnored
            plugin.getDataFolder().mkdir();

        File file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) try (InputStream in = plugin.getResourceAsStream(fileName)) {
            Files.copy(in, file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class)
                .load(new File(plugin.getDataFolder(), fileName));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveData() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class)
                .save(data, new File(plugin.getDataFolder(), "data.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveQueue() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class)
                .save(queue, new File(plugin.getDataFolder(), "queue.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveUserCache() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class)
                .save(userCache, new File(plugin.getDataFolder(), "usercache.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
