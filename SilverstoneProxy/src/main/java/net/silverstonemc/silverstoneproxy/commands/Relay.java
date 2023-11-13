package net.silverstonemc.silverstoneproxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;

@SuppressWarnings("DataFlowIssue")
public class Relay implements SimpleCommand {
    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("silverstone.owner");
    }

    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (args.length > 1) {
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
                    SilverstoneProxy.jda.getTextChannelById(592208420602380328L).sendMessage(message).queue();
                case "staff" ->
                    SilverstoneProxy.jda.getTextChannelById(667793661991583744L).sendMessage(message).queue();
                default -> sender.sendMessage(Component.text("Invalid channel!", NamedTextColor.RED));
            }
        }
    }
}