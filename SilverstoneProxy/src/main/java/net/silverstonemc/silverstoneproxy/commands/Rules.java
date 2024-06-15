package net.silverstonemc.silverstoneproxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.silverstonemc.silverstoneproxy.ConfigurationManager.FileType.CONFIG;

public class Rules implements SimpleCommand {
    public Rules(SilverstoneProxy instance) {
        i = instance;
    }

    private final SilverstoneProxy i;

    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (args.length > 0) {
            if (!sender.hasPermission("silverstone.moderator")) {
                sendRules(sender, -1, true);
                return;
            }

            Player target = i.server.getPlayer(args[0]).isPresent() ? i.server.getPlayer(args[0])
                .get() : null;
            // If target is null, cancel the command
            if (target == null) {
                sender.sendMessage(Component.text("Please provide an online player!", NamedTextColor.RED));
                return;
            }

            // If rule number specified
            if (args.length > 1) {
                if (args[1].equalsIgnoreCase("all")) args[1] = "-1";
                int rule;
                try {
                    rule = Integer.parseInt(args[1]);
                } catch (NumberFormatException ignored) {
                    sender.sendMessage(Component.text("Not a number!", NamedTextColor.RED));
                    return;
                }

                if (args.length > 2) sendRules(target, rule, args[2].equalsIgnoreCase("-s"));
                else sendRules(target, rule, false);

            } else sendRuleGUI(sender, target);
        } else sendRules(sender, -1, true);
    }

    private void sendRules(CommandSource target, int rule, boolean silent) {
        String targetName = target.get(Identity.NAME).orElse("Console");

        if (rule == -1) {
            try {
                //noinspection DataFlowIssue
                for (String header : i.fileManager.files.get(CONFIG).node("rules", "header")
                    .getList(String.class))
                    target.sendMessage(MiniMessage.miniMessage().deserialize(header));

                for (ConfigurationNode rules : i.fileManager.files.get(CONFIG).node("rules", "rules")
                    .childrenMap().values())
                    //noinspection DataFlowIssue
                    target.sendMessage(MiniMessage.miniMessage()
                        .deserialize(i.fileManager.files.get(CONFIG).node("rules", "rule-prefix")
                            .getString("null").replace("{#}", rules.key().toString()) + rules.getString()));

                //noinspection DataFlowIssue
                for (String footer : i.fileManager.files.get(CONFIG).node("rules", "footer")
                    .getList(String.class))
                    target.sendMessage(MiniMessage.miniMessage().deserialize(footer));

            } catch (SerializationException e) {
                throw new RuntimeException(e);
            }

            // Tell mod+
            if (!silent) for (Player online : i.server.getAllPlayers())
                if (online.hasPermission("silverstone.moderator")) online.sendMessage(Component
                    .text("The rules have been sent to ", NamedTextColor.RED)
                    .append(Component.text(targetName, NamedTextColor.GRAY)));
        } else {
            target.sendMessage(MiniMessage.miniMessage()
                .deserialize("<dark_green>Rule " + i.fileManager.files.get(CONFIG).node("rules",
                        "rule-prefix").getString("null")
                    .replace("{#}", String.valueOf(rule)) + i.fileManager.files.get(CONFIG)
                    .node("rules", "rules", rule).getString()));

            // Tell mod+
            if (!silent) for (Player online : i.server.getAllPlayers())
                if (online.hasPermission("silverstone.moderator")) online.sendMessage(Component.text("Rule ",
                        NamedTextColor.RED).append(Component.text(rule, NamedTextColor.GRAY))
                    .append(Component.text(" has been sent to ", NamedTextColor.RED))
                    .append(Component.text(targetName, NamedTextColor.GRAY)));
        }
    }

    private void sendRuleGUI(CommandSource sender, Player target) {
        String header = "<red><bold>Available rules:";

        ArrayList<String> rules = new ArrayList<>();
        for (ConfigurationNode rule : i.fileManager.files.get(CONFIG).node("rules", "rules").childrenMap()
            .values())
            rules.add(rule.getString());

        String footer = "\n\n<reset><gray><italic>Click to send rule to " + target.getUsername();

        StringBuilder message = new StringBuilder(header);
        message.append("\n<bold><gray><hover:show_text:'<#23B8CF>All rules").append(footer).append(
            "'><click:run_command:/rules ").append(target.getUsername()).append(" -1>ALL</click></hover>");

        // Loop through the rules in increments of 3
        for (int x = 0; x < rules.size(); x += 3) {
            // Append a new line to the message
            message.append("\n");

            // Loop through the current rules to add them to the message
            for (int y = 0; y < 3; y++) {
                int ruleIndex = x + y;

                // Get the current rule to add to the message
                String rule = rules.get(ruleIndex);

                // Create a command to use
                String command = "/rules " + target.getUsername() + " " + (ruleIndex + 1);

                // Add the current rule to the message
                message.append("<bold><gray><hover:show_text:'<#23B8CF>").append(rule).append(footer).append(
                    "'><click:run_command:").append(command).append(">").append(ruleIndex + 1).append(
                    "</click></hover>");

                // If this is not the last rule, add a separator to the message
                if (y < 2) message.append(" <dark_gray><bold>| ");
            }
        }

        sender.sendMessage(MiniMessage.miniMessage().deserialize(message.toString()));
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {
        String[] args = invocation.arguments();

        List<String> arguments = new ArrayList<>();
        for (Player players : i.server.getAllPlayers()) arguments.add(players.getUsername());

        if (args.length == 0) return CompletableFuture.completedFuture(arguments);

        List<String> result = new ArrayList<>();
        if (args.length == 1) for (String a : arguments)
            if (a.toLowerCase().startsWith(args[0].toLowerCase())) result.add(a);

        return CompletableFuture.completedFuture(result);
    }
}
