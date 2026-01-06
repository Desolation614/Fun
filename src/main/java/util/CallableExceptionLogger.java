/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.util;

import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallableExceptionLogger<V>
implements Callable<V> {
    private static final Logger log = LoggerFactory.getLogger(CallableExceptionLogger.class);
    private final Callable<V> callable;

    @Override
    public V call() throws Exception {
        try {
            return this.callable.call();
        }
        catch (Throwable ex) {
            log.error("Uncaught exception in callable {}", this.callable, (Object)ex);
            throw ex;
        }
    }

    public static <V> CallableExceptionLogger<V> wrap(Callable<V> callable) {
        return new CallableExceptionLogger<V>(callable);
    }

    public CallableExceptionLogger(Callable<V> callable) {
        this.callable = callable;
    }
}

