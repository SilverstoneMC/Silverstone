package net.silverstonemc.silverstoneproxy.commands;

import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;

import java.util.ArrayList;
import java.util.List;

public class Rules extends Command implements TabExecutor {
    public Rules() {
        super("rules");
    }

    private final BungeeAudiences audience = SilverstoneProxy.getAdventure();
    private final Plugin plugin = SilverstoneProxy.getPlugin();

    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            if (!sender.hasPermission("silverstone.moderator")) {
                sendSelfRules(sender);
                return;
            }

            ProxiedPlayer target = plugin.getProxy().getPlayer(args[0]);
            // If target is null, cancel the command
            if (target == null) {
                sender.sendMessage(
                    new ComponentBuilder("Please provide an online player!").color(ChatColor.RED).create());
                return;
            }

            // If rule number specified
            if (args.length > 1) {
                if (args[1].equalsIgnoreCase("all")) args[1] = "-1";
                int rule;
                try {
                    rule = Integer.parseInt(args[1]);
                } catch (NumberFormatException ignored) {
                    sender.sendMessage(new ComponentBuilder("Not a number!").color(ChatColor.RED).create());
                    return;
                }

                if (args.length > 2) sendRules(target, rule, args[2].equalsIgnoreCase("-s"));
                else sendRules(target, rule, false);
                
            } else sendRuleGUI(sender, target);
        } else sendSelfRules(sender);
    }

    private void sendRules(ProxiedPlayer target, int rule, boolean silent) {
        if (rule == -1) {
            for (String header : SilverstoneProxy.config.getStringList("rules.header"))
                audience.player(target).sendMessage(MiniMessage.miniMessage().deserialize(header));
            for (int x = 1; x <= SilverstoneProxy.config.getSection("rules.rules").getKeys().size(); x++)
                audience.player(target).sendMessage(MiniMessage.miniMessage().deserialize(
                    SilverstoneProxy.config.getString("rules.rule-prefix")
                        .replace("{#}", String.valueOf(x)) + SilverstoneProxy.config.getString(
                        "rules.rules." + x)));
            for (String footer : SilverstoneProxy.config.getStringList("rules.footer"))
                audience.player(target).sendMessage(MiniMessage.miniMessage().deserialize(footer));

            // Tell mod+
            if (!silent) for (ProxiedPlayer online : plugin.getProxy().getPlayers())
                if (online.hasPermission("silverstone.moderator")) audience.player(online).sendMessage(
                    Component.text("The rules have been sent to ").color(NamedTextColor.RED)
                        .append(Component.text(target.getName()).color(NamedTextColor.GRAY)));
        } else {
            audience.player(target).sendMessage(MiniMessage.miniMessage().deserialize(
                "<dark_green>Rule " + SilverstoneProxy.config.getString("rules.rule-prefix")
                    .replace("{#}", String.valueOf(rule)) + SilverstoneProxy.config.getString(
                    "rules.rules." + rule)));

            // Tell mod+
            for (ProxiedPlayer online : plugin.getProxy().getPlayers())
                if (online.hasPermission("silverstone.moderator")) audience.player(online).sendMessage(
                    Component.text("Rule ").color(NamedTextColor.RED)
                        .append(Component.text(rule).color(NamedTextColor.GRAY))
                        .append(Component.text(" has been sent to ").color(NamedTextColor.RED))
                        .append(Component.text(target.getName()).color(NamedTextColor.GRAY)));
        }
    }

    private void sendSelfRules(CommandSender sender) {
        for (String header : SilverstoneProxy.config.getStringList("rules.header"))
            audience.sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(header));
        for (int x = 1; x <= SilverstoneProxy.config.getSection("rules.rules").getKeys().size(); x++)
            audience.sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(
                SilverstoneProxy.config.getString("rules.rule-prefix")
                    .replace("{#}", String.valueOf(x)) + SilverstoneProxy.config.getString(
                    "rules.rules." + x)));
        for (String footer : SilverstoneProxy.config.getStringList("rules.footer"))
            audience.sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(footer));
    }

    private void sendRuleGUI(CommandSender sender, ProxiedPlayer target) {
        String header = "<red><bold>Available rules:";
        int ruleCount = SilverstoneProxy.config.getSection("rules.rules").getKeys().size();
        ArrayList<String> rules = new ArrayList<>();
        for (int rule = 1; rule <= ruleCount; rule++)
            rules.add(SilverstoneProxy.config.getString("rules.rules." + rule));
        String footer = "\n\n<reset><gray><italic>Click to send rule to " + target.getName();

        StringBuilder message = new StringBuilder(header);
        message.append("\n<bold><gray><hover:show_text:'<#23B8CF>All rules").append(footer)
            .append("'><click:run_command:/rules ").append(target.getName())
            .append(" -1>ALL</click></hover>");

        for (int x = 0; x < ruleCount; x = x + 3) {
            message.append("\n");

            try {
                String rule1 = rules.get(x).replace("'", "\\'");
                String rule2 = rules.get(x + 1).replace("'", "\\'");
                String rule3 = rules.get(x + 2).replace("'", "\\'");

                String command1 = "/rules " + target.getName() + " " + (x + 1);
                String command2 = "/rules " + target.getName() + " " + (x + 2);
                String command3 = "/rules " + target.getName() + " " + (x + 3);

                message.append("<bold><gray><hover:show_text:'<#23B8CF>").append(rule1).append(footer)
                    .append("'><click:run_command:").append(command1).append(">").append(x + 1)
                    .append("</click></hover> <dark_gray><bold>| ")
                    .append("<bold><gray><hover:show_text:'<#23B8CF>").append(rule2).append(footer)
                    .append("'><click:run_command:").append(command2).append(">").append(x + 2)
                    .append("</click></hover> <dark_gray><bold>| ")
                    .append("<bold><gray><hover:show_text:'<#23B8CF>").append(rule3).append(footer)
                    .append("'><click:run_command:").append(command3).append(">").append(x + 3)
                    .append("</click></hover>");
            } catch (IndexOutOfBoundsException e1) {
                try {
                    String rule1 = rules.get(x).replace("'", "\\'");
                    String rule2 = rules.get(x + 1).replace("'", "\\'");

                    String command1 = "/rules " + target.getName() + " " + (x + 1);
                    String command2 = "/rules " + target.getName() + " " + (x + 2);

                    message.append("<bold><gray><hover:show_text:'<#23B8CF>").append(rule1).append(footer)
                        .append("'><click:run_command:").append(command1).append(">").append(x + 1)
                        .append("</click></hover> <dark_gray><bold>| ")
                        .append("<bold><gray><hover:show_text:'<#23B8CF>").append(rule2).append(footer)
                        .append("'><click:run_command:").append(command2).append(">").append(x + 2)
                        .append("</click></hover>");
                } catch (IndexOutOfBoundsException e2) {
                    String rule1 = rules.get(x).replace("'", "\\'");

                    String command1 = "/rules " + target.getName() + " " + (x + 1);

                    message.append("<bold><gray><hover:show_text:'<#23B8CF>").append(rule1).append(footer)
                        .append("'><click:run_command:").append(command1).append(">").append(x + 1)
                        .append("</click></hover>");
                }
            }
        }

        audience.sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(message.toString()));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> arguments = new ArrayList<>();
        for (ProxiedPlayer players : plugin.getProxy().getPlayers()) arguments.add(players.getName());

        List<String> result = new ArrayList<>();
        if (args.length == 1) for (String a : arguments)
            if (a.toLowerCase().startsWith(args[0].toLowerCase())) result.add(a);
        return result;
    }
}
