package net.silverstonemc.silverstoneproxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class JDAManager implements SimpleCommand {
    public JDAManager(SilverstoneProxy instance) {
        i = instance;
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("silverstone.owner");
    }

    private final SilverstoneProxy i;

    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        TextComponent usage = Component.text("Usage: /jdamanager <start/stop>", NamedTextColor.RED);

        if (args.length < 1) {
            sender.sendMessage(usage);
            return;
        }

        if (args[0].equalsIgnoreCase("start")) {
            if (i.getJdaThread() != null) {
                sender.sendMessage(Component.text("JDA is already running!", NamedTextColor.RED));
                return;
            }

            sender.sendMessage(Component.text("Starting JDA...", NamedTextColor.YELLOW));
            i.startJDA();
            sender.sendMessage(Component.text("JDA started!", NamedTextColor.GREEN));

        } else if (args[0].equalsIgnoreCase("stop")) {
            if (i.getJdaThread() == null) {
                sender.sendMessage(Component.text("JDA is already stopped!", NamedTextColor.RED));
                return;
            }

            sender.sendMessage(Component.text("Stopping JDA...", NamedTextColor.YELLOW));
            i.shutdownJDA();
            sender.sendMessage(Component.text("JDA stopped!", NamedTextColor.GREEN));

        } else sender.sendMessage(usage);
    }

    final List<String> arguments = new ArrayList<>();

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {
        String[] args = invocation.arguments();

        if (arguments.isEmpty()) {
            arguments.add("start");
            arguments.add("stop");
        }

        if (args.length == 0) return CompletableFuture.completedFuture(arguments);

        List<String> result = new ArrayList<>();
        if (args.length == 1) for (String a : arguments)
            if (a.toLowerCase().startsWith(args[0].toLowerCase())) result.add(a);

        return CompletableFuture.completedFuture(result);
    }
}
