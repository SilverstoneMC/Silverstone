package net.silverstonemc.silverstoneglobal;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class Heartbeat {
    public Heartbeat(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;

    public void sendHeartbeat() {
        String heartbeatUrl = plugin.getConfig().getString("heartbeat-url");
        if (heartbeatUrl == null) {
            plugin.getLogger().warning("No heartbeat URL is set in the config!");
            return;
        }

        try {
            InputStream url = new URI(heartbeatUrl).toURL().openStream();
            url.close();

        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
