package net.silverstonemc.silverstoneproxy;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import javax.annotation.Nullable;
import java.util.ArrayList;
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
            int unpunishmentCount = i.fileManager.files.get(CONFIG).getNode("reasons", reason, "remove")
                .getChildrenList().size();

            // Get the correct warning number
            int unpunishmentNumber = currentWarningCount % unpunishmentCount;
            if (unpunishmentNumber == 0) unpunishmentNumber = unpunishmentCount;

            // Un-warn the player
            ArrayList<String> cmdList = new ArrayList<>(
                i.fileManager.files.get(CONFIG).getNode("reasons", reason, "remove", unpunishmentNumber)
                    .getChildrenList().stream().map(ConfigurationNode::toString).toList());
            for (String cmd : cmdList)
                i.server.getCommandManager()
                    .executeAsync(i.server.getConsoleCommandSource(), cmd.replace("{player}", username));

        } else if (reason.equals("all")) {
            i.logger.info("=============================================");
            i.logger.info("Clearing all of " + username + "'s warnings...");
            i.logger.info("=============================================");

            // For each warn reason in the config
            // For each number in said reason
            // Run each command in each number
            new Thread(() -> {
                try {
                    for (ConfigurationNode configReasons : i.fileManager.files.get(CONFIG).getNode("reasons")
                        .getChildrenList()) {
                        for (ConfigurationNode configReasonNumbers : configReasons.getNode("remove")
                            .getChildrenList()) {
                            for (String configReasonCommands : configReasonNumbers.getList(
                                TypeToken.of(String.class)))
                                i.server.getCommandManager().executeAsync(i.server.getConsoleCommandSource(),
                                    configReasonCommands.replace("{player}", username));
                            Thread.sleep(100);
                        }
                        Thread.sleep(50);
                    }

                    i.logger.info("=============================================");
                    i.logger.info("Done clearing all of " + username + "'s warnings!");
                    i.logger.info("=============================================");
                } catch (InterruptedException ignored) {
                } catch (ObjectMappingException e) {
                    throw new RuntimeException(e);
                }
            }).start();

        } else {
            i.logger.info("=============================================");
            i.logger.info("Clearing all of " + username + "'s '" + reason + "' warnings...");
            i.logger.info("=============================================");

            // For each number in reason
            // Run each command in each number
            new Thread(() -> {
                try {
                    for (ConfigurationNode configReasonNumbers : i.fileManager.files.get(CONFIG)
                        .getNode("reasons", reason, "remove").getChildrenList()) {
                        for (String configReasonCommands : configReasonNumbers.getList(
                            TypeToken.of(String.class)))
                            i.server.getCommandManager().executeAsync(i.server.getConsoleCommandSource(),
                                configReasonCommands.replace("{player}", username));
                        Thread.sleep(100);
                    }

                    i.logger.info("=============================================");
                    i.logger.info("Done clearing all of " + username + "'s '" + reason + "' warnings!");
                    i.logger.info("=============================================");
                } catch (InterruptedException ignored) {
                } catch (ObjectMappingException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }
}
