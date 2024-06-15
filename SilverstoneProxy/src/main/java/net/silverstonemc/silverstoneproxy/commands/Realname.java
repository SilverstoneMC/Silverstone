package net.silverstonemc.silverstoneproxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;
import net.silverstonemc.silverstoneproxy.utils.NicknameUtils;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.UUID;

import static net.silverstonemc.silverstoneproxy.ConfigurationManager.FileType.NICKNAMES;

public class Realname implements SimpleCommand {
    public Realname(SilverstoneProxy instance) {
        i = instance;
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("silverstone.realname");
    }

    private final SilverstoneProxy i;

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (args.length < 1) {
            sender.sendMessage(Component.text("Usage: /realname <nickname>", NamedTextColor.RED));
            return;
        }

        boolean noPlayerFound = true;
        for (ConfigurationNode uuid : i.fileManager.files.get(NICKNAMES).node("stripped-nicknames")
            .childrenMap().values()) {
            String strippedNickname = uuid.getString();
            if (strippedNickname.toLowerCase().replaceFirst(".*:", "").startsWith(args[0].toLowerCase())) {
                if (i.fileManager.files.get(NICKNAMES).node("nicknames", uuid.key().toString()).virtual())
                    continue;

                Component nickname = new NicknameUtils(i).getDisplayName(UUID.fromString(uuid.key()
                    .toString()));
                sender.sendMessage(Component.text().append(nickname)
                    .append(Component.text("'s real name is " + strippedNickname.replaceFirst(":.*",
                        "") + ".", NamedTextColor.GREEN)));
                noPlayerFound = false;
                break;
            }
        }

        if (noPlayerFound)
            sender.sendMessage(Component.text("No player found with the nickname " + args[0] + ".",
                NamedTextColor.RED));
    }
}