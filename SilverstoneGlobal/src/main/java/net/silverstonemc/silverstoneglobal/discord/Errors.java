package net.silverstonemc.silverstoneglobal.discord;

import net.dv8tion.jda.api.utils.MarkdownUtil;
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
import java.util.*;

@Plugin(name = "SilverstoneErrorLogger", category = "Core", elementType = "appender", printObject = true)
public class Errors extends AbstractAppender {
    private final List<String> errorQueue = new ArrayList<>();
    private final Deque<Long> messageTimestamps = new ArrayDeque<>();
    private boolean isErrorGroup;
    private boolean sendRateLimited = true;

    public Errors(JavaPlugin plugin) {
        super("SilverstoneErrorLogger", null, null, false, null);
        ((Logger) LogManager.getRootLogger()).addAppender(this);

        new BukkitRunnable() {
            @Override
            public void run() {
                dumpQueue();
            }
        }.runTaskTimerAsynchronously(plugin, 100, 100);
    }

    @Override
    public void append(LogEvent event) {
        String message = event.getMessage().getFormattedMessage();
        if (message.contains("Tried to load unrecognized recipe")) return;
        if (message.contains(
            "[com.fastasyncworldedit.core.queue.implementation.ParallelQueueExtent] Catching error")) return;

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

        StringBuilder builder = new StringBuilder();
        List<String> finalErrorQueue = new ArrayList<>(errorQueue);
        for (String error : finalErrorQueue) {
            if (builder.length() + error.length() >= 1997) {
                if (canSendMessage()) sendDiscordMessage(builder);
                builder = new StringBuilder();
            }

            builder.append(error).append("\n");
        }

        errorQueue.removeAll(finalErrorQueue);
        finalErrorQueue.clear();
        if (canSendMessage()) sendDiscordMessage(builder);
    }

    private boolean canSendMessage() {
        long now = System.currentTimeMillis();

        // Measured in milliseconds
        long timeWindow = 10000;
        // Remove timestamps outside the time window
        while (!messageTimestamps.isEmpty() && now - messageTimestamps.peekFirst() > timeWindow)
            messageTimestamps.pollFirst();

        // Max messages allowed per time window
        int rateLimit = 3;
        if (messageTimestamps.size() < rateLimit) {
            messageTimestamps.addLast(now);
            sendRateLimited = true;
            return true;
        } else {
            if (sendRateLimited) //noinspection DataFlowIssue
                SilverstoneGlobal.jda.getTextChannelById(1076713224612880404L).sendMessage(
                    "-# Limiting error output...").queue();
            sendRateLimited = false;
            return false;
        }
    }

    private void sendDiscordMessage(StringBuilder builder) {
        //noinspection DataFlowIssue
        SilverstoneGlobal.jda.getTextChannelById(1076713224612880404L).sendMessage(MarkdownUtil.codeblock("accesslog",
            builder.toString())).queue();
    }
}
