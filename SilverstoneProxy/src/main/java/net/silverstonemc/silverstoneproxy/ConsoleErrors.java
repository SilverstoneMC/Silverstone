package net.silverstonemc.silverstoneproxy;

import com.velocitypowered.api.scheduler.ScheduledTask;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Plugin(name = "SilverstoneErrorLogger", category = "Core", elementType = "appender", printObject = true)
public class ConsoleErrors extends AbstractAppender {
    private boolean isErrorGroup;
    private final List<String> errorQueue = new ArrayList<>();
    private final ScheduledTask dumpTask;

    public ConsoleErrors(SilverstoneProxy instance) {
        super("SilverstoneErrorLogger", null, null, false, null);
        ((Logger) LogManager.getRootLogger()).addAppender(this);

        dumpTask = instance.server.getScheduler().buildTask(instance, this::dumpQueue).delay(5,
            TimeUnit.SECONDS).repeat(5, TimeUnit.SECONDS).schedule();
    }

    @Override
    public void append(LogEvent event) {
        if (event.getMessage().getFormattedMessage().contains("unable to connect to server")) return;
        if (event.getMessage().getFormattedMessage().contains("disconnected while connecting to")) return;
        if (event.getMessage().getFormattedMessage().contains(
            "exception encountered in com.velocitypowered.proxy.connection.backend.BackendPlaySessionHandler"))
            return;
        if (event.getMessage().getFormattedMessage().contains(
            "exception encountered in com.velocitypowered.proxy.connection.client.ClientPlaySessionHandler"))
            return;
        if (event.getMessage().getFormattedMessage().contains("read timed out")) return;

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
        dumpQueue();

        dumpTask.cancel();
        ((Logger) LogManager.getRootLogger()).removeAppender(this);
    }

    private void dumpQueue() {
        if (errorQueue.isEmpty()) return;

        StringBuilder builder = new StringBuilder("```accesslog\n");
        for (String error : errorQueue) {
            if (builder.length() + error.length() >= 1997) {
                sendDiscordMessage(builder);
                builder = new StringBuilder("```accesslog\n");
            }

            builder.append(error).append("\n");
        }

        errorQueue.clear();
        sendDiscordMessage(builder);
    }

    private void sendDiscordMessage(StringBuilder builder) {
        builder.append("```");
        //noinspection DataFlowIssue
        SilverstoneProxy.jda.getTextChannelById(1076713224612880404L).sendMessage(builder.toString()).queue();
    }
}
