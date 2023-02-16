package net.silverstonemc.silverstonewarnings.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import net.silverstonemc.silverstonewarnings.SilverstoneWarnings;

@SuppressWarnings("DataFlowIssue")
public class RelayCommand extends Command {
    public RelayCommand() {
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
                    SilverstoneWarnings.jda.getTextChannelById(592208420602380328L).sendMessage(message)
                        .queue();
                case "staff" ->
                    SilverstoneWarnings.jda.getTextChannelById(667793661991583744L).sendMessage(message)
                        .queue();
                default -> sender.sendMessage(
                    new ComponentBuilder("Invalid channel!").color(ChatColor.RED).create());
            }
        }
    }
}