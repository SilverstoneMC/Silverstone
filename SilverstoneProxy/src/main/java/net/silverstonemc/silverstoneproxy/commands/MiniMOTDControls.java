package net.silverstonemc.silverstoneproxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MiniMOTDControls implements SimpleCommand {
    public MiniMOTDControls(SilverstoneProxy instance) {
        i = instance;
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("silverstone.admin");
    }

    private final SilverstoneProxy i;

    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (args.length == 0) {
            sender.sendMessage(Component.text("Usage: /mmotd <enable | disable>", NamedTextColor.RED));
            return;
        }

        switch (args[0].toLowerCase()) {
            case "enable" -> {
                editMotdFiles("/home/container/plugins/minimotd-velocity/main.conf", true);
                editMotdFiles("/home/container/plugins/minimotd-velocity/extra-configs/survival.conf", true);
                i.server.getCommandManager().executeAsync(i.server.getConsoleCommandSource(),
                    "minimotd reload");
                sender.sendMessage(Component.text("MiniMOTD enabled.", NamedTextColor.GREEN));
            }

            case "disable" -> {
                editMotdFiles("/home/container/plugins/minimotd-velocity/main.conf", false);
                editMotdFiles("/home/container/plugins/minimotd-velocity/extra-configs/survival.conf", false);
                i.server.getCommandManager().executeAsync(i.server.getConsoleCommandSource(),
                    "minimotd reload");
                sender.sendMessage(Component.text("MiniMOTD disabled.", NamedTextColor.RED));
            }

            default -> sender.sendMessage(Component.text("Usage: /mmotd <enable | disable>",
                NamedTextColor.RED));
        }
    }

    private void editMotdFiles(String path, boolean enabled) {
        File file = new File(path);
        List<String> content;
        try {
            content = Files.readAllLines(file.toPath());

            try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8, false)) {
                for (String line : content)
                    fileWriter.write(line.replace("motd-enabled=" + !enabled, "motd-enabled=" + enabled)
                        .replace("icon-enabled=" + !enabled, "icon-enabled=" + enabled)
                        .replace("max-players-enabled=" + !enabled, "max-players-enabled=" + enabled) + "\n");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {
        String[] args = invocation.arguments();

        List<String> arguments = new ArrayList<>();
        arguments.add("enable");
        arguments.add("disable");

        if (args.length == 0) return CompletableFuture.completedFuture(arguments);

        List<String> result = new ArrayList<>();
        if (args.length == 1) for (String a : arguments)
            if (a.toLowerCase().startsWith(args[0].toLowerCase())) result.add(a);
        return CompletableFuture.completedFuture(result);
    }
}
