/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunnableExceptionLogger
implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(RunnableExceptionLogger.class);
    private final Runnable runnable;

    @Override
    public void run() {
        try {
            this.runnable.run();
        }
        catch (Throwable ex) {
            log.error("Uncaught exception in runnable {}", (Object)this.runnable, (Object)ex);
            throw ex;
        }
    }

    public static RunnableExceptionLogger wrap(Runnable runnable) {
        return new RunnableExceptionLogger(runnable);
    }

    public RunnableExceptionLogger(Runnable runnable) {
        this.runnable = runnable;
    }
}

