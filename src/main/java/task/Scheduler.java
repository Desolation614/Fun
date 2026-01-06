/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.task.Schedule;
import net.runelite.client.task.ScheduledMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Scheduler {
    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);
    private final List<ScheduledMethod> scheduledMethods = new CopyOnWriteArrayList<ScheduledMethod>();
    @Inject
    ScheduledExecutorService executor;

    public void addScheduledMethod(ScheduledMethod method) {
        this.scheduledMethods.add(method);
    }

    public void removeScheduledMethod(ScheduledMethod method) {
        this.scheduledMethods.remove(method);
    }

    public List<ScheduledMethod> getScheduledMethods() {
        return Collections.unmodifiableList(this.scheduledMethods);
    }

    public void tick() {
        Instant now = Instant.now();
        for (ScheduledMethod scheduledMethod : this.scheduledMethods) {
            Schedule schedule;
            Duration timeSinceRun;
            Instant last = scheduledMethod.getLast();
            Duration difference = Duration.between(last, now);
            if (difference.compareTo(timeSinceRun = Duration.of((schedule = scheduledMethod.getSchedule()).period(), schedule.unit())) <= 0) continue;
            log.trace("Scheduled task triggered: {}", (Object)scheduledMethod);
            scheduledMethod.setLast(now);
            if (schedule.asynchronous()) {
                this.executor.submit(() -> this.run(scheduledMethod));
                continue;
            }
            this.run(scheduledMethod);
        }
    }

    private void run(ScheduledMethod scheduledMethod) {
        try {
            Runnable lambda = scheduledMethod.getLambda();
            if (lambda != null) {
                lambda.run();
            } else {
                Method method = scheduledMethod.getMethod();
                method.invoke(scheduledMethod.getObject(), new Object[0]);
            }
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            log.warn("error invoking scheduled task", (Throwable)ex);
        }
        catch (Exception ex) {
            log.warn("error during scheduled task", (Throwable)ex);
        }
    }
}

