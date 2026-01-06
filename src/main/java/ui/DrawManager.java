/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Singleton
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.runelite.client.ui;

import java.awt.Image;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DrawManager {
    private static final Logger log = LoggerFactory.getLogger(DrawManager.class);
    private final List<Runnable> everyFrame = new CopyOnWriteArrayList<Runnable>();
    private final Queue<Consumer<Image>> nextFrame = new ConcurrentLinkedQueue<Consumer<Image>>();

    public void registerEveryFrameListener(Runnable everyFrameListener) {
        if (!this.everyFrame.contains(everyFrameListener)) {
            this.everyFrame.add(everyFrameListener);
        }
    }

    public void unregisterEveryFrameListener(Runnable everyFrameListener) {
        this.everyFrame.remove(everyFrameListener);
    }

    public void requestNextFrameListener(Consumer<Image> nextFrameListener) {
        this.nextFrame.add(nextFrameListener);
    }

    public void processDrawComplete(Supplier<Image> imageSupplier) {
        for (Runnable everyFrameListener : this.everyFrame) {
            try {
                everyFrameListener.run();
            }
            catch (Exception e) {
                log.error("Error in draw consumer", (Throwable)e);
            }
        }
        Consumer<Image> nextFrameListener = this.nextFrame.poll();
        Image image = null;
        while (nextFrameListener != null) {
            if (image == null) {
                try {
                    image = imageSupplier.get();
                }
                catch (Exception ex) {
                    log.warn("error getting screenshot", (Throwable)ex);
                }
            }
            if (image == null) {
                this.nextFrame.clear();
                break;
            }
            try {
                nextFrameListener.accept(image);
            }
            catch (Exception e) {
                log.error("Error in draw consumer", (Throwable)e);
            }
            nextFrameListener = this.nextFrame.poll();
        }
    }
}

