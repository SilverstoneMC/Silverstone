package net.silverstonemc.silverstoneproxy;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static net.silverstonemc.silverstoneproxy.ConfigurationManager.FileType.CONFIG;

public class UndoWarning {
    public UndoWarning(SilverstoneProxy instance) {
        i = instance;
    }

    private final SilverstoneProxy i;

    public void undoWarning(UUID uuid, String reason, @Nullable Integer currentWarningCount) {
        String username = new UserManager(i).getUsername(uuid);

        if (currentWarningCount != null) {
            // Grab the amount of un-punishments in the config
            int unpunishmentCount = i.fileManager.files.get(CONFIG).node("reasons", reason, "remove")
                .childrenMap().values().size();

            // Get the correct warning number
            int unpunishmentNumber = currentWarningCount % unpunishmentCount;
            if (unpunishmentNumber == 0) unpunishmentNumber = unpunishmentCount;

            // Un-warn the player
            Iterable<String> cmdList = new ArrayList<>(i.fileManager.files.get(CONFIG).node("reasons",
                    reason,
                    "remove",
                    unpunishmentNumber).childrenMap().values().stream().map(ConfigurationNode::toString)
                .toList());
            for (String cmd : cmdList)
                i.server.getCommandManager().executeAsync(i.server.getConsoleCommandSource(),
                    cmd.replace("{player}", username));

        } else if (reason.equals("all")) {
            i.logger.info("=============================================");
            i.logger.info("Clearing all of {}'s warnings...", username);
            i.logger.info("=============================================");

            // For each warn reason in the config
            // For each number in said reason
            // Run each command in each number
            new Thread(() -> {
                try {
                    Set<String> commands = new HashSet<>();
                    for (ConfigurationNode configReasons : i.fileManager.files.get(CONFIG).node("reasons")
                        .childrenMap().values())
                        for (ConfigurationNode configReasonNumbers : configReasons.node("remove")
                            .childrenMap().values())
                            //noinspection DataFlowIssue
                            for (String configReasonCommands : configReasonNumbers.getList(String.class))
                                commands.add(configReasonCommands.replace("{player}", username));

                    for (String command : commands)
                        i.server.getCommandManager().executeAsync(i.server.getConsoleCommandSource(),
                            command);

                    Thread.sleep(100);

                    i.logger.info("=============================================");
                    i.logger.info("Done clearing all of {}'s warnings!", username);
                    i.logger.info("=============================================");
                } catch (InterruptedException ignored) {
                } catch (SerializationException e) {
                    throw new RuntimeException(e);
                }
            }).start();

        } else {
            i.logger.info("=============================================");
            i.logger.info("Clearing all of {}'s '{}' warnings...", username, reason);
            i.logger.info("=============================================");

            // For each number in reason
            // Run each command in each number
            new Thread(() -> {
                try {
                    Set<String> commands = new HashSet<>();
                    for (ConfigurationNode configReasonNumbers : i.fileManager.files.get(CONFIG).node("reasons",
                        reason,
                        "remove").childrenMap().values()) {
                        //noinspection DataFlowIssue
                        for (String configReasonCommands : configReasonNumbers.getList(String.class))
                            commands.add(configReasonCommands.replace("{player}", username));

                        for (String command : commands)
                            i.server.getCommandManager().executeAsync(i.server.getConsoleCommandSource(),
                                command);

                        Thread.sleep(100);
                    }

                    i.logger.info("=============================================");
                    i.logger.info("Done clearing all of {}'s '{}' warnings!", username, reason);
                    i.logger.info("=============================================");
                } catch (InterruptedException ignored) {
                } catch (SerializationException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }
}
