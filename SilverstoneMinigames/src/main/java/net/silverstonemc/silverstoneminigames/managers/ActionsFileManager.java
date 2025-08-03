package net.silverstonemc.silverstoneminigames.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ActionsFileManager {
    public ActionsFileManager(JavaPlugin plugin) {
        this.plugin = plugin;
        saveDefaultConfig();
    }

    private final JavaPlugin plugin;
    private File configFile;
    private FileConfiguration actionsConfig;

    public void reloadConfig() {
        if (configFile == null) configFile = new File(plugin.getDataFolder(), "actions.yml");

        actionsConfig = YamlConfiguration.loadConfiguration(configFile);

        InputStream defaultStream = plugin.getResource("actions.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream,
                StandardCharsets.UTF_8));
            actionsConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (actionsConfig == null) reloadConfig();
        return actionsConfig;
    }

    public void saveDefaultConfig() {
        if (configFile == null) configFile = new File(plugin.getDataFolder(), "actions.yml");
        if (!configFile.exists()) plugin.saveResource("actions.yml", false);
    }
}
