package net.silverstonemc.silverstoneglobal;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

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
            URL url = new URI(heartbeatUrl).toURL();
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.connect();

        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
