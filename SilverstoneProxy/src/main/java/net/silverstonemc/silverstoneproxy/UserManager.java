package net.silverstonemc.silverstoneproxy;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.UUID;

import static net.silverstonemc.silverstoneproxy.ConfigurationManager.FileType.USERCACHE;

public class UserManager {
    public UserManager(SilverstoneProxy instance) {
        i = instance;
    }

    public static final BidiMap<UUID, String> playerMap = new DualHashBidiMap<>();

    private final SilverstoneProxy i;

    public UUID getUUID(String username) {
        return playerMap.inverseBidiMap().get(username);
    }

    public String getUsername(UUID uuid) {
        return playerMap.get(uuid);
    }

    public void addUser(UUID uuid, String username) {
        playerMap.put(uuid, username);
        i.fileManager.files.get(USERCACHE).getNode("users", uuid.toString()).setValue(username);
        i.fileManager.save(USERCACHE);
    }
}
