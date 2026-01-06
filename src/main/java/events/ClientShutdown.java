/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.events;

import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClientShutdown {
    private static final Logger log = LoggerFactory.getLogger(ClientShutdown.class);
    private final Queue<Future<?>> tasks = new ConcurrentLinkedQueue();

    public void waitFor(Future<?> future) {
        this.tasks.add(future);
    }

    public void waitForAllConsumers(Duration totalTimeout) {
        Future<?> task;
        long deadline = System.nanoTime() + totalTimeout.toNanos();
        while ((task = this.tasks.poll()) != null) {
            long timeout = deadline - System.nanoTime();
            if (timeout < 0L) {
                log.warn("Timed out waiting for task completion");
                return;
            }
            try {
                task.get(timeout, TimeUnit.NANOSECONDS);
            }
            catch (ThreadDeath d) {
                throw d;
            }
            catch (Throwable t) {
                log.warn("Error during shutdown: ", t);
            }
        }
    }

    public Queue<Future<?>> getTasks() {
        return this.tasks;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ClientShutdown)) {
            return false;
        }
        ClientShutdown other = (ClientShutdown)o;
        Queue<Future<?>> this$tasks = this.getTasks();
        Queue<Future<?>> other$tasks = other.getTasks();
        return !(this$tasks == null ? other$tasks != null : !this$tasks.equals(other$tasks));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Queue<Future<?>> $tasks = this.getTasks();
        result = result * 59 + ($tasks == null ? 43 : $tasks.hashCode());
        return result;
    }

    public String toString() {
        return "ClientShutdown(tasks=" + String.valueOf(this.getTasks()) + ")";
    }
}

