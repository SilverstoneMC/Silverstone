package net.silverstonemc.silverstoneproxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Heartbeat {
    public Heartbeat(URL heartbeatUrl) {
        this.heartbeatUrl = heartbeatUrl;
    }

    private final URL heartbeatUrl;

    public void sendHeartbeat() {
        try {
            InputStream url = heartbeatUrl.openStream();
            url.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
