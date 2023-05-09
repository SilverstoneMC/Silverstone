package net.silverstonemc.silverstoneproxy;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.UUID;

public class UserManager {
    public static final BidiMap<UUID, String> playerMap = new DualHashBidiMap<>();
    
    public UUID getUUID(String username) {
        return playerMap.inverseBidiMap().get(username);
    }

    public String getUsername(UUID uuid) {
        return playerMap.get(uuid);
    }

    public void addUser(UUID uuid, String username) {
        playerMap.put(uuid, username);
        ConfigurationManager.userCache.set("users." + uuid.toString(), username);
        new ConfigurationManager().saveUserCache();
    }
}
