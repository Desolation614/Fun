/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.plugins.timetracking;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import javax.swing.JPanel;
import net.runelite.client.plugins.timetracking.TimeFormatMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TabContentPanel
extends JPanel {
    private static final Logger log = LoggerFactory.getLogger(TabContentPanel.class);
    private static final DateTimeFormatter DATETIME_FORMATTER_24H = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER_12H = DateTimeFormatter.ofPattern("h:mm a");

    public abstract int getUpdateInterval();

    public abstract void update();

    public static String getFormattedEstimate(long remainingSeconds, TimeFormatMode mode) {
        DateTimeFormatter formatter = TabContentPanel.getDateTimeFormatter(mode);
        if (formatter == null) {
            StringBuilder sb = new StringBuilder("in ");
            long duration = (remainingSeconds + 59L) / 60L;
            long minutes = duration % 60L;
            long hours = duration / 60L % 24L;
            long days = duration / 1440L;
            if (days > 0L) {
                sb.append(days).append("d ");
            }
            if (hours > 0L) {
                sb.append(hours).append("h ");
            }
            if (minutes > 0L) {
                sb.append(minutes).append("m ");
            }
            return sb.toString();
        }
        try {
            StringBuilder sb = new StringBuilder();
            LocalDateTime endTime = LocalDateTime.now().plus(remainingSeconds, ChronoUnit.SECONDS);
            LocalDateTime currentTime = LocalDateTime.now();
            if (endTime.getDayOfWeek() != currentTime.getDayOfWeek()) {
                sb.append(endTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault())).append(' ');
            }
            sb.append("at ");
            sb.append(formatter.format(endTime));
            return sb.toString();
        }
        catch (DateTimeException e) {
            log.warn("error formatting absolute time: now + {}", (Object)remainingSeconds, (Object)e);
            return "Invalid";
        }
    }

    private static DateTimeFormatter getDateTimeFormatter(TimeFormatMode mode) {
        switch (mode) {
            case ABSOLUTE_12H: {
                return DATETIME_FORMATTER_12H;
            }
            case ABSOLUTE_24H: {
                return DATETIME_FORMATTER_24H;
            }
        }
        return null;
    }
}

