package net.silverstonemc.silverstoneglobal.discord;

import net.silverstonemc.silverstoneglobal.SilverstoneGlobal;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Plugin(name = "SilverstoneErrorLogger", category = "Core", elementType = "appender", printObject = true)
public class Errors extends AbstractAppender {
    private final List<String> errorQueue = new ArrayList<>();
    private boolean isErrorGroup;

    public Errors(JavaPlugin plugin) {
        super("SilverstoneErrorLogger", null, null, false, null);
        ((Logger) LogManager.getRootLogger()).addAppender(this);

        new BukkitRunnable() {
            @Override
            public void run() {
                dumpQueue();
            }
        }.runTaskTimer(plugin, 100, 100);
    }

    @Override
    public void append(LogEvent event) {
        if (event.getLevel() != Level.ERROR && event.getLevel() != Level.FATAL && event.getLevel() != Level.TRACE) {
            if (isErrorGroup) {
                dumpQueue();
                isErrorGroup = false;
            }
            return;
        }

        String loggerName = event.getLoggerName().replaceAll(".*\\.", "");

        Instant instant = Instant.ofEpochMilli(event.getTimeMillis());
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm:ss a", Locale.US);
        String time = dtf.format(zdt);

        errorQueue.add("[" + time + " | " + event.getLevel() + "]: [" + loggerName + "] " + event.getMessage()
            .getFormattedMessage());
        isErrorGroup = true;
    }

    public void remove() {
        ((Logger) LogManager.getRootLogger()).removeAppender(this);
    }

    public void dumpQueue() {
        if (errorQueue.isEmpty()) return;

        StringBuilder builder = new StringBuilder("```accesslog\n");
        List<String> finalErrorQueue = new ArrayList<>(errorQueue);
        for (String error : finalErrorQueue) {
            if (builder.length() + error.length() >= 1997) {
                sendDiscordMessage(builder);
                builder = new StringBuilder("```accesslog\n");
            }

            builder.append(error).append("\n");
        }

        errorQueue.removeAll(finalErrorQueue);
        finalErrorQueue.clear();
        sendDiscordMessage(builder);
    }

    private void sendDiscordMessage(StringBuilder builder) {
        builder.append("```");
        //noinspection DataFlowIssue
        SilverstoneGlobal.jda.getTextChannelById(1076713224612880404L).sendMessage(builder.toString())
            .queue();
    }
}
