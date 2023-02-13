package net.silverstonemc.silverstonewarnings.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.silverstonemc.silverstonewarnings.SilverstoneWarnings;

@SuppressWarnings("DataFlowIssue")
public class RelayCommand extends Command {

    public RelayCommand() {
        super("sendstaffdiscord", "silverstone.console");
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length > 1) {
            String message = "";
            for (String s : args) {
                message = message.concat(s);
                message = message.concat(" ");
            }
            message = message.trim();

            SilverstoneWarnings.jda.getTextChannelById(667793661991583744L).sendMessage(message).queue();
        }
    }
}