package net.silverstonemc.silverstoneproxy.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;

@SuppressWarnings("DataFlowIssue")
public class Relay extends Command {
    public Relay() {
        super("relay", "silverstone.console");
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length > 2) {
            String message = "";
            int iteration = 0;
            for (String s : args) {
                iteration++;
                if (iteration == 1) continue;
                message = message.concat(s);
                message = message.concat(" ");
            }
            message = message.trim();

            switch (args[0]) {
                case "mcchat" ->
                    SilverstoneProxy.jda.getTextChannelById(592208420602380328L).sendMessage(message)
                        .queue();
                case "staff" ->
                    SilverstoneProxy.jda.getTextChannelById(667793661991583744L).sendMessage(message)
                        .queue();
                default -> sender.sendMessage(
                    new ComponentBuilder("Invalid channel!").color(ChatColor.RED).create());
            }
        }
    }
}