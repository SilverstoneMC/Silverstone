package net.silverstonemc.silverstoneglobal.commands.guis;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Tips implements CommandExecutor {
    public Tips(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final JavaPlugin plugin;

    @SuppressWarnings("DataFlowIssue")
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        List<Component> pages = new ArrayList<>();
        for (String page : plugin.getConfig().getConfigurationSection("tips").getKeys(false)) {
            // Header
            TextComponent.Builder contents = Component.text().append(MiniMessage.miniMessage()
                .deserialize(plugin.getConfig().getString("tips." + page + ".header") + "\n")
                .decorate(TextDecoration.BOLD));

            // Tips
            for (String tip : plugin.getConfig().getStringList("tips." + page + ".tips"))
                contents.append(MiniMessage.miniMessage().deserialize("\n" + tip));

            pages.add(contents.build());
        }

        if (!(sender instanceof Player)) {
            for (Component page : pages)
                sender.sendMessage(page);
            return true;
        }

        Component title = Component.text("Tips");
        Component author = Component.text("Jason");

        sender.openBook(Book.book(title, author, pages));
        return true;
    }
}
