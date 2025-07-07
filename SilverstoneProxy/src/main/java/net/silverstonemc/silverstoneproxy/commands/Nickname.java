package net.silverstonemc.silverstoneproxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.silverstonemc.silverstoneproxy.SilverstoneProxy;
import net.silverstonemc.silverstoneproxy.utils.NicknameUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.silverstonemc.silverstoneproxy.ConfigurationManager.FileType.CONFIG;

public class Nickname implements SimpleCommand {
    public Nickname(SilverstoneProxy instance) {
        i = instance;
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("silverstone.nickname");
    }

    private final SilverstoneProxy i;

    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        Player player = null;
        if (sender instanceof Player) player = (Player) sender;

        if (args.length == 0) {
            if (sender.hasPermission("silverstone.nickname.others")) if (sender.hasPermission(
                "silverstone.nickname.colors")) sender.sendMessage(Component.text("Usage: /nick <nickname | reset | colors> [player]",
                NamedTextColor.RED));
            else sender.sendMessage(Component.text(
                    "Usage: /nick <nickname | reset> [player]",
                    NamedTextColor.RED));
            else if (sender.hasPermission("silverstone.nickname.colors")) sender.sendMessage(Component.text("Usage: /nick <nickname | reset | colors>",
                NamedTextColor.RED));
            else sender.sendMessage(Component.text("Usage: /nick <nickname | reset>", NamedTextColor.RED));
            return;
        }

        boolean differentTarget = false;
        if (sender.hasPermission("silverstone.nickname.others") && args.length > 1) {
            Player target = i.server.getPlayer(args[1]).isPresent() ? i.server.getPlayer(args[1])
                .get() : null;
            if (target == null) {
                sender.sendMessage(Component.text("That player isn't online!").color(NamedTextColor.RED));
                return;
            }
            player = target;
            differentTarget = true;
        }

        // Reset nickname
        if (args[0].equalsIgnoreCase("reset")) {
            if (new NicknameUtils(i).setNickname(player, sender, null, null)) return;

            if (differentTarget) {
                sender.sendMessage(Component.text("You have reset ", NamedTextColor.GREEN)
                    .append(Component.text(player.getUsername())).append(Component.text("'s nickname.")));
                player.sendMessage(Component.text("Your nickname was reset.", NamedTextColor.GREEN));
            } else sender.sendMessage(Component.text("Your nickname has been reset.", NamedTextColor.GREEN));
            return;
        }

        // Colors help
        if (args[0].equalsIgnoreCase("colors") && (sender.hasPermission("silverstone.nickname.colors") || sender.hasPermission(
            "silverstone.nickname.formats"))) {
            sender.sendMessage(Component.text("Click ", NamedTextColor.DARK_GREEN)
                .append(Component.text("here", NamedTextColor.DARK_AQUA, TextDecoration.UNDERLINED)
                    .clickEvent(ClickEvent.openUrl("https://docs.advntr.dev/minimessage/format.html#color")))
                .append(Component.text(" to see available formatting.", NamedTextColor.DARK_GREEN)));
            return;
        }

        // Whether or not to allow colors/decorations
        TagResolver.Builder allowedTags = TagResolver.builder();
        if (sender.hasPermission("silverstone.nickname.colors")) allowedTags.resolvers(
            StandardTags.color(),
            StandardTags.rainbow(),
            StandardTags.gradient());
        if (sender.hasPermission("silverstone.nickname.decorations"))
            allowedTags.resolver(StandardTags.decorations());
        allowedTags.resolver(StandardTags.reset());

        MiniMessage miniMessage = MiniMessage.builder().tags(allowedTags.build()).build();
        String strippedName = miniMessage.stripTags(args[0]);

        // Check if nickname is alphanumeric
        if (!sender.hasPermission("silverstone.nickname.bypass.alphanumeric")) if (!strippedName.matches(
            "^[a-zA-Z0-9]+$")) {
            sender.sendMessage(Component.text("Nicknames must be alphanumeric!", NamedTextColor.RED));
            return;
        }

        // Check if nickname is too long
        if (!sender.hasPermission("silverstone.nickname.bypass.length")) {
            int maxLength = i.fileManager.files.get(CONFIG).node("nicknames", "max-length").getInt();
            if (strippedName.length() > maxLength) {
                sender.sendMessage(Component.text(
                    "Nicknames cannot be longer than " + maxLength + " characters!",
                    NamedTextColor.RED));
                return;
            }
        }

        Component nickname = miniMessage.deserialize(args[0]);

        if (new NicknameUtils(i).setNickname(
            player,
            sender,
            nickname,
            MiniMessage.miniMessage().stripTags(args[0]))) return;

        if (differentTarget) {
            sender.sendMessage(Component.text("You have set ", NamedTextColor.GREEN).append(Component.text(
                    player.getUsername())).append(Component.text("'s nickname to "))
                .append(miniMessage.deserialize(args[0])));
            player.sendMessage(Component.text("Your nickname was changed to ", NamedTextColor.GREEN)
                .append(miniMessage.deserialize(args[0])));
        } else sender.sendMessage(Component.text("Your nickname has been set to ", NamedTextColor.GREEN)
            .append(miniMessage.deserialize(args[0])));
    }

    final List<String> arguments = new ArrayList<>();

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (sender.hasPermission("silverstone.nickname.colors") || sender.hasPermission(
            "silverstone.nickname.decorations")) {
            if (!arguments.contains("colors")) arguments.add("colors");
        } else arguments.remove("colors");

        if (!arguments.contains("reset")) arguments.add("reset");

        if (args.length == 0) return CompletableFuture.completedFuture(arguments);

        List<String> arguments2 = new ArrayList<>();
        if (sender.hasPermission("silverstone.nickname.others"))
            for (Player player : i.server.getAllPlayers())
                arguments2.add(player.getUsername());

        List<String> result = new ArrayList<>();
        switch (args.length) {
            case 1 -> {
                for (String a : arguments)
                    if (a.toLowerCase().startsWith(args[0].toLowerCase())) result.add(a);
            }

            case 2 -> {
                for (String a : arguments2)
                    if (a.toLowerCase().startsWith(args[1].toLowerCase())) result.add(a);
            }
        }

        return CompletableFuture.completedFuture(result);
    }
}