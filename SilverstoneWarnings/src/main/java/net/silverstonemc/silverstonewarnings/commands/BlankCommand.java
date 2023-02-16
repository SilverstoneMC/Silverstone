package net.silverstonemc.silverstonewarnings.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class BlankCommand extends Command {
    public BlankCommand() {
        super("zblankcmd", "silverstone.moderator");
    }

    public void execute(CommandSender sender, String[] args) {
    }
}