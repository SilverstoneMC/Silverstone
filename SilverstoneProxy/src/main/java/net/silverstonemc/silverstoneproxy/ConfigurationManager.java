package net.silverstonemc.silverstoneproxy;


import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigurationManager {
    public ConfigurationManager(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public final Map<FileType, ConfigurationNode> files = new HashMap<>();

    private final Map<FileType, YamlConfigurationLoader> loaders = new HashMap<>();
    private final Path dataDirectory;

    public enum FileType {
        CONFIG("config.yml"), NICKNAMES("nicknames.yml"), USERCACHE("usercache.yml"), WARNDATA("warndata.yml"), WARNQUEUE(
            "warnqueue.yml");

        private final String fileName;

        FileType(String fileName) {
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }
    }

    public void loadFiles() {
        // Generate configs if they don't exist
        if (!dataDirectory.toFile().exists()) //noinspection ResultOfMethodCallIgnored
            dataDirectory.toFile().mkdir();

        files.clear();

        files.put(FileType.CONFIG, loadFile(FileType.CONFIG));
        files.put(FileType.NICKNAMES, loadFile(FileType.NICKNAMES));
        files.put(FileType.USERCACHE, loadFile(FileType.USERCACHE));
        files.put(FileType.WARNDATA, loadFile(FileType.WARNDATA));
        files.put(FileType.WARNQUEUE, loadFile(FileType.WARNQUEUE));
    }

    @Nullable
    private ConfigurationNode loadFile(FileType fileType) {
        File file = new File(dataDirectory.toFile(), fileType.getFileName());

        if (!file.exists())
            try (InputStream in = getClass().getClassLoader().getResourceAsStream(fileType.getFileName())) {
                Files.copy(Objects.requireNonNull(in), file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

        // Load the config
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(file.toPath()).build();
        loaders.put(fileType, loader);

        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void save(FileType fileType) {
        try {
            loaders.get(fileType).save(files.get(fileType));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
