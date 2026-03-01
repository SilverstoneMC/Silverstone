package net.silverstonemc.silverstoneproxy;

import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.URL;

public class Heartbeat {
    private final URL heartbeatUrl;
    private final Logger logger;

    public Heartbeat(URL heartbeatUrl, Logger logger) {
        this.heartbeatUrl = heartbeatUrl;
        this.logger = logger;
    }

    public void sendHeartbeat() {
        try {
            InputStream url = heartbeatUrl.openStream();
            url.close();
        } catch (SocketException ignored) {
        } catch (IOException e) {
            logger.warn("Failed to send heartbeat: {}", e.getMessage());
        }
    }
}
