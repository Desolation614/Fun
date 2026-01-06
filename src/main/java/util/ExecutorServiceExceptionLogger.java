/*
 * Decompiled with CFR 0.152.
 */
package net.runelite.client.util;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import net.runelite.client.util.CallableExceptionLogger;
import net.runelite.client.util.RunnableExceptionLogger;

public class ExecutorServiceExceptionLogger
implements ScheduledExecutorService {
    private final ScheduledExecutorService service;

    private static Runnable monitor(Runnable command) {
        return RunnableExceptionLogger.wrap(command);
    }

    private static <V> Callable<V> monitor(Callable<V> command) {
        return CallableExceptionLogger.wrap(command);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return this.service.submit(ExecutorServiceExceptionLogger.monitor(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return this.service.submit(ExecutorServiceExceptionLogger.monitor(task), result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return this.service.submit(ExecutorServiceExceptionLogger.monitor(task));
    }

    @Override
    public void execute(Runnable command) {
        this.service.execute(ExecutorServiceExceptionLogger.monitor(command));
    }

    @Override
    public void shutdown() {
        this.service.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return this.service.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return this.service.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.service.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.service.awaitTermination(timeout, unit);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.service.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return this.service.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return this.service.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.service.invokeAny(tasks, timeout, unit);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return this.service.schedule(command, delay, unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return this.service.schedule(callable, delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return this.service.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return this.service.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

    public ExecutorServiceExceptionLogger(ScheduledExecutorService service) {
        this.service = service;
    }
}

