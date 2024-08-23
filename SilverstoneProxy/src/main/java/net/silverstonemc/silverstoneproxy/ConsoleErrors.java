package net.silverstonemc.silverstoneproxy;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.silverstonemc.silverstoneproxy.events.Leave;
import net.silverstonemc.silverstoneproxy.utils.NicknameUtils;
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
import java.util.UUID;
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
        String message = event.getMessage().getFormattedMessage();
        if (message.contains("unable to connect to server")) return;
        if (message.contains("disconnected while connecting to")) return;
        if (message.contains(
            "exception encountered in com.velocitypowered.proxy.connection.backend.BackendPlaySessionHandler"))
            return;

        if (message.contains(
            "exception encountered in com.velocitypowered.proxy.connection.client.ClientPlaySessionHandler")) {
            sendDisconnectMessage(message, "likely crashed");
            return;
        }

        if (message.contains("read timed out")) {
            sendDisconnectMessage(message, "timed out");
            return;
        }

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

    private void sendDisconnectMessage(String log, String reason) {
        // Run the task after 1 second to help with ordering issues
        SilverstoneProxy i = SilverstoneProxy.getInstance();
        i.server.getScheduler().buildTask(SilverstoneProxy.getInstance(), () -> {
            String username = log.replaceAll(".*\\[connected player] ", "").replaceAll(" \\(.*", "");
            UUID uuid = Leave.recentlyQuit.get(username);
            // The player will be null if they're vanished too
            if (uuid == null) return;

            TextComponent.Builder message = Component.text().decorate(TextDecoration.ITALIC).color(
                NamedTextColor.GRAY).append(new NicknameUtils(i).getDisplayName(uuid)).colorIfAbsent(
                NamedTextColor.GRAY).append(Component.text(" " + reason));

            for (Player players : i.server.getAllPlayers()) players.sendMessage(message);
        }).delay(1, TimeUnit.SECONDS).schedule();
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
