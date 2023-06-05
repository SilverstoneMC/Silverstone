package net.silverstonemc.silverstoneproxy;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.UUID;

public class UndoWarning {
    private final SilverstoneProxy plugin = SilverstoneProxy.getPlugin();

    public void undoWarning(UUID uuid, String reason, @Nullable Integer currentWarningCount) {
        String username = new UserManager().getUsername(uuid);

        if (currentWarningCount != null) {
            // Grab the amount of un-punishments in the config
            int unpunishmentCount = ConfigurationManager.config.getSection("reasons." + reason + ".remove")
                .getKeys().toArray().length;

            // Get the correct warning number
            int unpunishmentNumber = currentWarningCount % unpunishmentCount;
            if (unpunishmentNumber == 0) unpunishmentNumber = unpunishmentCount;

            // Un-warn the player
            ArrayList<String> cmdList = new ArrayList<>(ConfigurationManager.config.getStringList(
                "reasons." + reason + ".remove." + unpunishmentNumber));
            for (String s : cmdList)
                plugin.getProxy().getPluginManager()
                    .dispatchCommand(plugin.getProxy().getConsole(), s.replace("{player}", username));

        } else if (reason.equals("all")) {
            plugin.getLogger().info("=============================================");
            plugin.getLogger().info("Clearing all of " + username + "'s warnings...");
            plugin.getLogger().info("=============================================");

            // For each warn reason in the config
            // For each number in said reason
            // Run each command in each number
            Thread thread = new Thread(() -> {
                try {
                    for (String configReasons : ConfigurationManager.config.getSection("reasons").getKeys()) {
                        for (String configReasonNumbers : ConfigurationManager.config.getSection(
                            "reasons." + configReasons + ".remove").getKeys()) {
                            for (String configReasonCommands : ConfigurationManager.config.getStringList(
                                "reasons." + configReasons + ".remove." + configReasonNumbers))
                                plugin.getProxy().getPluginManager()
                                    .dispatchCommand(plugin.getProxy().getConsole(),
                                        configReasonCommands.replace("{player}", username));
                            Thread.sleep(100);
                        }
                        Thread.sleep(50);
                    }

                    plugin.getLogger().info("=============================================");
                    plugin.getLogger().info("Done clearing all of " + username + "'s warnings!");
                    plugin.getLogger().info("=============================================");
                } catch (InterruptedException ignored) {
                }
            });
            thread.start();

        } else {
            plugin.getLogger().info("=============================================");
            plugin.getLogger().info("Clearing all of " + username + "'s '" + reason + "' warnings...");
            plugin.getLogger().info("=============================================");

            // For each number in reason
            // Run each command in each number
            Thread thread = new Thread(() -> {
                try {
                    for (String configReasonNumbers : ConfigurationManager.config.getSection(
                        "reasons." + reason + ".remove").getKeys()) {
                        for (String configReasonCommands : ConfigurationManager.config.getStringList(
                            "reasons." + reason + ".remove." + configReasonNumbers))
                            plugin.getProxy().getPluginManager()
                                .dispatchCommand(plugin.getProxy().getConsole(),
                                    configReasonCommands.replace("{player}", username));
                        Thread.sleep(100);
                    }

                    plugin.getLogger().info("=============================================");
                    plugin.getLogger()
                        .info("Done clearing all of " + username + "'s '" + reason + "' warnings!");
                    plugin.getLogger().info("=============================================");
                } catch (InterruptedException ignored) {
                }
            });
            thread.start();
        }
    }
}