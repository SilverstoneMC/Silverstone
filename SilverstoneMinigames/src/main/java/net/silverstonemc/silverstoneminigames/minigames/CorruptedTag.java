package net.silverstonemc.silverstoneminigames.minigames;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class CorruptedTag implements CommandExecutor {
    private static final HashMap<Player, BossBar> bossBars = new HashMap<>();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        switch (cmd.getName().toLowerCase()) {
            case "corruptedtagstart" -> {
                //                float value = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("mctagcorruption")
                //                    .getScore(player).getScore() / 1000f;
            }

            case "corruptedtagstop" -> {

            }
        }

        return true;
    }
}
