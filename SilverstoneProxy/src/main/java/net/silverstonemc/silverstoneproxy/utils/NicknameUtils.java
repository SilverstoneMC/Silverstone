package net.silverstonemc.silverstoneproxy.utils;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import net.draycia.carbon.api.CarbonChatProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;
import net.silverstonemc.silverstoneproxy.UserManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static net.silverstonemc.silverstoneproxy.ConfigurationManager.FileType.CONFIG;
import static net.silverstonemc.silverstoneproxy.ConfigurationManager.FileType.NICKNAMES;

public class NicknameUtils {
    public NicknameUtils(SilverstoneProxy instance) {
        i = instance;
    }

    private final SilverstoneProxy i;

    // Set and save the nickname to the file
    public boolean setNickname(@Nullable Player player, CommandSource sender, @Nullable Component nickname, @Nullable String strippedName) {
        if (player == null) {
            sender.sendMessage(Component.text("You must be a player to do that!", NamedTextColor.RED));
            return true;
        }

        try {
            if (nickname == null) {
                changeDisplayName(player, null);

                i.fileManager.files.get(NICKNAMES).node("nicknames", player.getUniqueId().toString())
                    .set(null);
                i.fileManager.files.get(NICKNAMES).node("stripped-nicknames", player.getUniqueId().toString())
                    .set(null);
                i.fileManager.save(NICKNAMES);

                return false;
            } else changeDisplayName(player, nickname);

            i.fileManager.files.get(NICKNAMES).node("nicknames", player.getUniqueId().toString()).set(
                MiniMessage.miniMessage().serialize(nickname));
            i.fileManager.files.get(NICKNAMES).node("stripped-nicknames", player.getUniqueId().toString())
                .set(player.getUsername() + ":" + strippedName);
            i.fileManager.save(NICKNAMES);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    // Set the player's display name without saving anything
    @SuppressWarnings("DataFlowIssue")
    public void changeDisplayName(Player player, Component nickname) {
        if (player == null) return;

        // Delay name change to let player finish loading
        i.server.getScheduler().buildTask(
            i, () -> {
                TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
                if (nickname == null) {
                    TabAPI.getInstance().getTabListFormatManager().setName(tabPlayer, null);
                    try {
                        CarbonChatProvider.carbonChat().userManager().user(player.getUniqueId()).get()
                            .nickname(null);
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    return;
                }
                ConfigurationNode config = i.fileManager.files.get(CONFIG);

                Component prefix = MiniMessage.miniMessage().deserialize(config.node("nicknames", "prefix")
                    .getString());
                Component suffix = MiniMessage.miniMessage().deserialize(config.node("nicknames", "suffix")
                    .getString());

                Component displayName = Component.text().append(prefix).append(nickname).append(suffix)
                    .build();
                TabAPI.getInstance().getTabListFormatManager().setName(
                    tabPlayer,
                    LegacyComponentSerializer.builder().useUnusualXRepeatedCharacterHexFormat().hexColors()
                        .build().serialize(displayName));

                try {
                    CarbonChatProvider.carbonChat().userManager().user(player.getUniqueId()).get().nickname(
                        displayName);
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).delay(1, TimeUnit.SECONDS).schedule();
    }

    public Component getDisplayName(UUID uuid) {
        ConfigurationNode playerNode = i.fileManager.files.get(NICKNAMES).node("nicknames", uuid.toString());

        if (playerNode.virtual()) return Component.text(UserManager.playerMap.get(uuid));
        else //noinspection DataFlowIssue
            return MiniMessage.miniMessage().deserialize(playerNode.getString());
    }
}
