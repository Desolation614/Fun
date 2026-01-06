/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.classic.Level
 *  ch.qos.logback.classic.Logger
 *  ch.qos.logback.classic.turbo.TurboFilter
 *  ch.qos.logback.core.spi.FilterReply
 *  org.slf4j.Marker
 *  org.slf4j.MarkerFactory
 */
package net.runelite.client.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import java.util.Arrays;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class DeduplicationFilter
extends TurboFilter {
    private static final Marker deduplicateMarker = MarkerFactory.getMarker((String)"DEDUPLICATE");
    private static final int CACHE_SIZE = 8;
    private static final int DUPLICATE_LOG_COUNT = 1000;
    private final Deque<LogException> cache = new ConcurrentLinkedDeque<LogException>();

    public void stop() {
        this.cache.clear();
        super.stop();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public FilterReply decide(Marker marker, Logger logger, Level level, String s, Object[] objects, Throwable throwable) {
        if (marker != deduplicateMarker || logger.isDebugEnabled() || throwable == null) {
            return FilterReply.NEUTRAL;
        }
        LogException logException = new LogException(s, throwable.getStackTrace());
        for (LogException e : this.cache) {
            if (!logException.equals(e)) continue;
            if (++e.count % 1000 == 0) {
                logger.warn("following log message logged 1000 times!");
                return FilterReply.NEUTRAL;
            }
            return FilterReply.DENY;
        }
        Deque<LogException> deque = this.cache;
        synchronized (deque) {
            if (this.cache.size() >= 8) {
                this.cache.pop();
            }
            this.cache.push(logException);
        }
        return FilterReply.NEUTRAL;
    }

    private static class LogException {
        private final String message;
        private final StackTraceElement[] stackTraceElements;
        private volatile int count;

        public LogException(String message, StackTraceElement[] stackTraceElements) {
            this.message = message;
            this.stackTraceElements = stackTraceElements;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof LogException)) {
                return false;
            }
            LogException other = (LogException)o;
            if (!other.canEqual(this)) {
                return false;
            }
            String this$message = this.message;
            String other$message = other.message;
            if (this$message == null ? other$message != null : !this$message.equals(other$message)) {
                return false;
            }
            return Arrays.deepEquals(this.stackTraceElements, other.stackTraceElements);
        }

        protected boolean canEqual(Object other) {
            return other instanceof LogException;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            String $message = this.message;
            result = result * 59 + ($message == null ? 43 : $message.hashCode());
            result = result * 59 + Arrays.deepHashCode(this.stackTraceElements);
            return result;
        }
    }
}

