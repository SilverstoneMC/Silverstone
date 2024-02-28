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
import ninja.leaping.configurate.ConfigurationNode;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

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

        if (nickname == null) {
            changeDisplayName(player, null);

            i.fileManager.files.get(NICKNAMES).getNode("nicknames", player.getUniqueId().toString()).setValue(
                null);
            i.fileManager.files.get(NICKNAMES).getNode("stripped-nicknames", player.getUniqueId().toString())
                .setValue(null);
            i.fileManager.save(NICKNAMES);

            return false;
        } else changeDisplayName(player, nickname);

        i.fileManager.files.get(NICKNAMES).getNode("nicknames", player.getUniqueId().toString()).setValue(
            MiniMessage.miniMessage().serialize(nickname));
        i.fileManager.files.get(NICKNAMES).getNode("stripped-nicknames", player.getUniqueId().toString())
            .setValue(player.getUsername() + ":" + strippedName);
        i.fileManager.save(NICKNAMES);

        return false;
    }

    // Set the player's display name without saving anything
    @SuppressWarnings("DataFlowIssue")
    public void changeDisplayName(Player player, Component nickname) {
        TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
        if (nickname == null) {
            TabAPI.getInstance().getTabListFormatManager().setName(tabPlayer, null);
            try {
                CarbonChatProvider.carbonChat().userManager().user(player.getUniqueId()).get().nickname(null);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            return;
        }
        ConfigurationNode config = i.fileManager.files.get(CONFIG);

        Component prefix = MiniMessage.miniMessage().deserialize(config.getNode("nicknames", "prefix")
            .getString());
        Component suffix = MiniMessage.miniMessage().deserialize(config.getNode("nicknames", "suffix")
            .getString());

        Component displayName = Component.text().append(prefix).append(nickname).append(suffix).build();
        TabAPI.getInstance().getTabListFormatManager().setName(tabPlayer,
            LegacyComponentSerializer.builder().useUnusualXRepeatedCharacterHexFormat().hexColors().build()
                .serialize(displayName));
        try {
            CarbonChatProvider.carbonChat().userManager().user(player.getUniqueId()).get().nickname(
                displayName);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Component getDisplayName(UUID uuid) {
        ConfigurationNode playerNode = i.fileManager.files.get(NICKNAMES).getNode("nicknames",
            uuid.toString());

        if (playerNode.isVirtual()) return Component.text(UserManager.playerMap.get(uuid));
        else //noinspection DataFlowIssue
            return MiniMessage.miniMessage().deserialize(playerNode.getString());
    }
}
